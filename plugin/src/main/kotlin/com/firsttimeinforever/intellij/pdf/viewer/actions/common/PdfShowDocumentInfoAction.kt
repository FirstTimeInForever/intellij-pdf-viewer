package com.firsttimeinforever.intellij.pdf.viewer.actions.common

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfDumbAwareAction
import com.intellij.openapi.actionSystem.AnActionEvent

class PdfShowDocumentInfoAction : PdfDumbAwareAction() {
  override fun actionPerformed(event: AnActionEvent) {
    findController(event)?.requestDocumentInfo()
  }
}
