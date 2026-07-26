package com.firsttimeinforever.intellij.pdf.viewer.frontend.actions.common

import com.firsttimeinforever.intellij.pdf.viewer.frontend.actions.PdfDumbAwareAction
import com.firsttimeinforever.intellij.pdf.viewer.frontend.actions.ViewModeAwareness
import com.intellij.openapi.actionSystem.AnActionEvent

class PdfShowDocumentInfoAction : PdfDumbAwareAction(ViewModeAwareness.IDE) {
  override fun actionPerformed(event: AnActionEvent) {
    findController(event)?.requestDocumentInfo()
  }

  override fun update(event: AnActionEvent) {
    event.presentation.isEnabledAndVisible = false
  }
}
