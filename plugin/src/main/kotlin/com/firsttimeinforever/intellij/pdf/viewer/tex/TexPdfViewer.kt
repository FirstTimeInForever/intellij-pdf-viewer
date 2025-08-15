package com.firsttimeinforever.intellij.pdf.viewer.tex

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.firsttimeinforever.intellij.pdf.viewer.utility.CommandExecutionUtils.getCommandStdoutIfSuccessful
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.ide.actions.OpenInRightSplitAction
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import nl.hannahsten.texifyidea.run.pdfviewer.ExternalPdfViewer
import java.io.File
import java.util.*

/**
 * PDF viewer for TeXiFy IDEA.
 */
class TexPdfViewer : ExternalPdfViewer {

  /**
   * Remember the last compiled/viewed pdf file so we can forward search to it later. This implies that we always
   * execute a forward search to the document that was compiled last.
   */
  private var pdfFilePath: String? = null

  override val displayName: String = "Built-in PDF Viewer"

  override val name: String = displayName.uppercase(Locale.getDefault()).replace(" ", "-")

  /**
   * When this plugin is installed, the PDF viewer plugin is always available.
   */
  override fun isAvailable(): Boolean = true

  override fun forwardSearch(outputPath: String?, sourceFilePath: String, line: Int, project: Project, focusAllowed: Boolean) {
    if (!SynctexUtils.isSynctexInstalled()) {
      Notification(
        "LaTeX",
        "SyncTeX not installed",
        "Forward search and inverse search need the synctex command line tool to be installed.",
        NotificationType.WARNING
      ).notify(project)
      return
    }

    if (outputPath != null) pdfFilePath = outputPath
    if (pdfFilePath == null) {
      Notification(
        "LaTeX",
        "Please compile before using forward search",
        "",
        NotificationType.WARNING
      ).notify(project)
    } else {
      val file = LocalFileSystem.getInstance().refreshAndFindFileByPath(pdfFilePath!!) ?: return
      val texFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(sourceFilePath) ?: return
      val pdfEditor = OpenFileDescriptor(project, file)
      val fileEditorManager = FileEditorManager.getInstance(project)

      ApplicationManager.getApplication().invokeLater {
        val jcefEditor = if (fileEditorManager.isFileOpen(file)) {
          val editor = fileEditorManager.getSelectedEditor(file)
          pdfEditor.navigate(false)
          editor as PdfFileEditor
        } else {
          // Only open in right split when there already is an open file, otherwise it is not possible to open a right split because there
          // is nothing to split.
          if (fileEditorManager.hasOpenFiles()) {
            // There does not seem to be a public alternative
            @Suppress("UnstableApiUsage") val editorWindow = OpenInRightSplitAction.openInRightSplit(project, file, pdfEditor, requestFocus = false)
            editorWindow?.selectedComposite?.selectedWithProvider?.fileEditor as? PdfFileEditor
          } else {
            pdfEditor.navigate(false)
            fileEditorManager.getSelectedEditor(file) as PdfFileEditor
          }
        }

        val command = GeneralCommandLine(
          "synctex",
          "view",
          "-i",
          "$line:0:${texFile.path}",
          "-o",
          file.path
        ).withWorkDirectory(File(file.parent.path))
        val output = getCommandStdoutIfSuccessful(command) ?: return@invokeLater
        val values: Map<String?, String?> = NUMBER_REGEX.toRegex().findAll(output)
          .associate { it.groups["id"]?.value to it.groups["value"]?.value }
          .filter { it.key != null && it.value != null }

        jcefEditor?.viewComponent?.controller?.setForwardSearchData(
          SynctexPreciseLocation(
            values["Page"]?.toInt() ?: 1,
            values["h"]?.toDouble() ?: 0.0,
            values["v"]?.toDouble() ?: 0.0,
            values["W"]?.toDouble() ?: 0.0,
            values["H"]?.toDouble() ?: 0.0,
          )
        )
      }
    }
  }

  override fun toString(): String {
    return displayName
  }

  companion object {
    const val NUMBER_REGEX = "(?<id>\\w+):(?<value>(\\d+)(.\\d+)?)"
  }
}
