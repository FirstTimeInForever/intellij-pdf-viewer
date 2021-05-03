package com.firsttimeinforever.intellij.pdf.viewer.actions.view

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfAction
import com.intellij.openapi.actionSystem.AnActionEvent

// TODO: Add icon
class PdfRotateCounterClockwiseAction : PdfAction() {
  override fun actionPerformed(event: AnActionEvent) {
    findController(event)?.rotate(clockwise = false)
  }
}
