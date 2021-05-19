package com.firsttimeinforever.intellij.pdf.viewer.actions.view

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfDumbAwareAction
import com.firsttimeinforever.intellij.pdf.viewer.actions.ViewModeAwareness
import com.intellij.openapi.actionSystem.AnActionEvent

// TODO: Add icon
class PdfRotateClockwiseAction : PdfDumbAwareAction() {
  override fun actionPerformed(event: AnActionEvent) {
    findController(event)?.rotate(clockwise = true)
  }
}
