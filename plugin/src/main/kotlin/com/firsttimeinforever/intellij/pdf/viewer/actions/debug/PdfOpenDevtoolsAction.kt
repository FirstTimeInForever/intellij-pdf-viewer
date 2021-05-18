package com.firsttimeinforever.intellij.pdf.viewer.actions.debug

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
internal class PdfOpenDevtoolsAction : PdfDebugAction(), DumbAware {
  override fun actionPerformed(event: AnActionEvent) {
    findController(event)?.browser?.openDevtools()
  }
}
