package com.firsttimeinforever.intellij.pdf.viewer.frontend.actions.view

import com.firsttimeinforever.intellij.pdf.viewer.frontend.actions.PdfDumbAwareAction
import com.intellij.openapi.actionSystem.AnActionEvent

class PdfRotateCounterClockwiseAction : PdfDumbAwareAction() {
  override fun actionPerformed(event: AnActionEvent) {
    findController(event)?.rotate(clockwise = false)
  }
}
