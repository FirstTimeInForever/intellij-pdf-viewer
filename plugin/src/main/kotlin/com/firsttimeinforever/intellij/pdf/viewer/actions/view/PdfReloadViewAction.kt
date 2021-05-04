package com.firsttimeinforever.intellij.pdf.viewer.actions.view

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfAction
import com.intellij.openapi.actionSystem.AnActionEvent

class PdfReloadViewAction : PdfAction() {
  override fun actionPerformed(event: AnActionEvent) {
    findController(event)?.reload(tryToPreserveState = true)
  }

  override fun update(event: AnActionEvent) {
    event.presentation.isEnabled = true
  }
}
