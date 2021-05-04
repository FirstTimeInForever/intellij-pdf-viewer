package com.firsttimeinforever.intellij.pdf.viewer.tex

import com.firsttimeinforever.intellij.pdf.viewer.utility.CommandExecutionUtils
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.vfs.VirtualFile

object SynctexUtils {
  /**
   * Checks if there is a SyncTeX file in the same folder as [this] file,
   * with the same base name (until the first period).
   * When there is no such SyncTeX file, all SyncTeX features should be disabled.
   *
   * Call this function on a pdf file to check if it has an accompanying SyncTeX file in the same folder.
   */
  fun VirtualFile.isSynctexFileAvailable(): Boolean {
    return parent.children
      .filter { it.name.contains("synctex") }
      .any { file ->
        file.name.takeWhile { it != '.' } == name.takeWhile { it != '.' }
      }
  }

  /**
   * Check if the SyncTeX command line utility is installed by trying to execute a SyncTeX command.
   */
  fun isSynctexInstalled(): Boolean {
    val output = CommandExecutionUtils.runCommand(GeneralCommandLine("synctex", "version")) ?: return false
    return output.stdout.contains("This is SyncTeX command line utility") ||
      output.stderr.contains("This is SyncTeX command line utility")
  }
}
