package com.firsttimeinforever.intellij.pdf.viewer.backend

import com.firsttimeinforever.intellij.pdf.viewer.backend.CommandExecutionUtils.getCommandStdoutIfSuccessful
import com.firsttimeinforever.intellij.pdf.viewer.common.events.PdfSyncTeXEvent
import com.firsttimeinforever.intellij.pdf.viewer.model.tex.SynctexPreciseLocation
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

  // Keep implementing the legacy API used by older TeXiFy releases.
  @Deprecated("Use forwardSearch to return result.")
  override fun forwardSearch(outputPath: String?, sourceFilePath: String, line: Int, project: Project, focusAllowed: Boolean) {
    forwardSearch(outputPath, sourceFilePath, line, project, focusAllowed, raiseOnError = false)
  }

  override fun forwardSearch(outputPath: String?, sourceFilePath: String, line: Int, project: Project, focusAllowed: Boolean, raiseOnError: Boolean): Pair<Boolean, String> =
    forwardSearch(outputPath, sourceFilePath, line, project, focusAllowed, raiseOnError, runInWsl = false)

  override fun forwardSearch(outputPath: String?, sourceFilePath: String, line: Int, project: Project, focusAllowed: Boolean, raiseOnError: Boolean, runInWsl: Boolean): Pair<Boolean, String> {
    // Older TeXiFy versions do not provide the WSL flag for manually triggered searches.
    // Infer it from the VFS paths so those searches use the same command setup as compilation.
    val effectiveRunInWsl = runInWsl || SynctexUtils.isWslPath(sourceFilePath) || SynctexUtils.isWslPath(outputPath ?: pdfFilePath.orEmpty())

    if (!SynctexUtils.isSynctexInstalled(effectiveRunInWsl)) {
      return Pair(false, "Forward search and inverse search need the synctex command line tool to be installed.")
    }

    if (outputPath != null) pdfFilePath = outputPath
    if (pdfFilePath == null) {
      return Pair(false, "Please compile before using forward search.")
    } else {
      val file = LocalFileSystem.getInstance().refreshAndFindFileByPath(pdfFilePath!!) ?: return Pair(false, "PDF file $pdfFilePath not found.")
      val texFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(sourceFilePath) ?: return Pair(false, "LaTeX file $sourceFilePath not found.")
      val texPath = (if (effectiveRunInWsl) SynctexUtils.windowsPathToWsl(texFile.path) else texFile.path)
        ?: return Pair(false, "Could not convert LaTeX file path to WSL path.")
      val pdfPath = (if (effectiveRunInWsl) SynctexUtils.windowsPathToWsl(file.path) else file.path)
        ?: return Pair(false, "Could not convert PDF file path to WSL path.")
      val wslProjectPath = if (effectiveRunInWsl && SynctexUtils.isWslPath(file.parent.path)) {
        SynctexUtils.windowsPathToWsl(file.parent.path)
          ?: return Pair(false, "Could not convert project directory to WSL path.")
      } else {
        null
      }

      // Ensure the file is open and focused
      OpenFileDescriptor(project, file).navigate(false)

      val command = SynctexUtils.synctexCommand(
        "view",
        "-i",
        "$line:0:$texPath",
        "-o",
        pdfPath,
        runInWsl = effectiveRunInWsl,
        workingDirectory = wslProjectPath,
      ).withWorkDirectory(
        if (wslProjectPath == null) File(file.parent.path) else File(System.getProperty("user.home")),
      )
      
      val output = getCommandStdoutIfSuccessful(command) ?: return Pair(true, "")
      
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
    return Pair(true, "")
  }

  override fun toString(): String {
    return displayName
  }

  companion object {
    const val NUMBER_REGEX = "(?<id>\\w+):(?<value>(\\d+)(.\\d+)?)"
  }
}
