package com.firsttimeinforever.intellij.pdf.viewer.utility

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessOutput
import com.intellij.execution.util.ExecUtil
import com.intellij.openapi.application.ApplicationManager

internal object CommandExecutionUtils {
  fun runCommand(commandLine: GeneralCommandLine): ProcessOutput? {
    return ApplicationManager.getApplication().executeOnPooledThread<ProcessOutput?> {
      try {
        ExecUtil.execAndGetOutput(commandLine, timeoutInMilliseconds = 3000)
      } catch (exception: ExecutionException) {
        exception.printStackTrace()
        null
      }
    }.get()
  }

  fun getCommandStdoutIfSuccessful(commandLine: GeneralCommandLine): String? {
    return runCommand(commandLine)?.let {
      when (it.exitCode) {
        0 -> it.stdout
        else -> null
      }
    }
  }
}
