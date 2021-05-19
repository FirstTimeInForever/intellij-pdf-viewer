package com.firsttimeinforever.intellij.pdf.viewer.actions.common

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfDumbAwareAction
import com.firsttimeinforever.intellij.pdf.viewer.actions.ViewModeAwareness
import com.intellij.openapi.actionSystem.AnActionEvent

class PdfShowDocumentInfoAction : PdfDumbAwareAction(ViewModeAwareness.IDE) {
  override fun actionPerformed(event: AnActionEvent) {
    findController(event)?.requestDocumentInfo()
  }
}
