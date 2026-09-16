package com.firsttimeinforever.intellij.pdf.viewer.tex

import com.firsttimeinforever.intellij.pdf.viewer.utility.CommandExecutionUtils
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.vfs.VirtualFile

object SynctexUtils {
  private val WSL_COMMAND = arrayOf("wsl", "--exec", "bash", "-ic")

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
  fun isSynctexInstalled(runInWsl: Boolean = false): Boolean {
    val output = CommandExecutionUtils.runCommand(synctexCommand("version", runInWsl = runInWsl)) ?: return false
    return output.stdout.contains("This is SyncTeX command line utility") ||
      output.stderr.contains("This is SyncTeX command line utility")
  }

  /**
   * See TeXiFy for similar implementation.
   */
  fun windowsPathToWsl(path: String): String? {
    wslUncPathToLinux(path)?.let { return it }

    val command = GeneralCommandLine(*WSL_COMMAND, "wslpath -a ${shellQuote(path)}")
    return CommandExecutionUtils.getCommandStdoutIfSuccessful(command)?.trim()?.takeIf(String::isNotBlank)
  }

  fun synctexCommand(
    vararg arguments: String,
    runInWsl: Boolean,
    workingDirectory: String? = null,
  ): GeneralCommandLine {
    if (!runInWsl) {
      return GeneralCommandLine("synctex", *arguments)
    }

    val command = listOf("synctex", *arguments).joinToString(" ", transform = ::shellQuote)
    val commandWithWorkingDirectory = workingDirectory?.let { "cd ${shellQuote(it)} && $command" } ?: command
    return GeneralCommandLine(*WSL_COMMAND, commandWithWorkingDirectory)
  }

  fun isWslPath(path: String): Boolean = path.startsWith("//wsl", ignoreCase = true) || path.startsWith("\\\\wsl", ignoreCase = true)

  private fun wslUncPathToLinux(path: String): String? {
    if (!isWslPath(path)) return null

    val command = GeneralCommandLine("wsl", "wslpath", "-a", path)
    return CommandExecutionUtils.getCommandStdoutIfSuccessful(command)?.trim()?.takeIf(String::isNotBlank)
  }

  private fun shellQuote(value: String): String = "'${value.replace("'", "'\"'\"'")}'"
}
