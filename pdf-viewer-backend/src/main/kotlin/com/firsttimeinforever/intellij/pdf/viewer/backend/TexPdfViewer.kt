package com.firsttimeinforever.intellij.pdf.viewer.backend

import com.firsttimeinforever.intellij.pdf.viewer.common.events.PdfSyncTeXEvent
import com.firsttimeinforever.intellij.pdf.viewer.model.tex.SynctexPreciseLocation
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
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

      // Ensure the file is open and focused
      OpenFileDescriptor(project, file).navigate(false)

      val command = GeneralCommandLine(
        "synctex",
        "view",
        "-i",
        "$line:0:${texFile.path}",
        "-o",
        file.path
      ).withWorkDirectory(File(file.parent.path))
      
      val output = CommandExecutionUtils.getCommandStdoutIfSuccessful(command) ?: return
      
      val values: Map<String?, String?> = NUMBER_REGEX.toRegex().findAll(output)
        .associate { it.groups["id"]?.value to it.groups["value"]?.value }
        .filter { it.key != null && it.value != null }

      val location = SynctexPreciseLocation(
        values["Page"]?.toInt() ?: 1,
        values["h"]?.toDouble() ?: 0.0,
        values["v"]?.toDouble() ?: 0.0,
        values["W"]?.toDouble() ?: 0.0,
        values["H"]?.toDouble() ?: 0.0,
      )
      
      project.messageBus.syncPublisher(PdfSyncTeXEvent.TOPIC).scrollTo(file.path, location)
    }
  }

  override fun toString(): String {
    return displayName
  }

  companion object {
    const val NUMBER_REGEX = "(?<id>\\w+):(?<value>(\\d+)(.\\d+)?)"
  }
}
