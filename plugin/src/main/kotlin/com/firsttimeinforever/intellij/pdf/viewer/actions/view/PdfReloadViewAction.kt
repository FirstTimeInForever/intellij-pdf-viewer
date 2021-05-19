package com.firsttimeinforever.intellij.pdf.viewer.actions.view

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfDumbAwareAction
import com.firsttimeinforever.intellij.pdf.viewer.actions.ViewModeAwareness
import com.intellij.openapi.actionSystem.AnActionEvent

class PdfReloadViewAction : PdfDumbAwareAction() {
  override fun actionPerformed(event: AnActionEvent) {
    findController(event)?.reload(tryToPreserveState = true)
  }

  override fun update(event: AnActionEvent) {
    event.presentation.isEnabled = true
  }
}
