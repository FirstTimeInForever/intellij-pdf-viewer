package com.firsttimeinforever.intellij.pdf.viewer.actions.debug

import com.intellij.openapi.actionSystem.AnActionEvent
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
internal class PdfOpenDevtoolsAction : PdfDebugAction() {
  override fun actionPerformed(event: AnActionEvent) {
    findController(event)?.browser?.openDevtools()
  }
}
