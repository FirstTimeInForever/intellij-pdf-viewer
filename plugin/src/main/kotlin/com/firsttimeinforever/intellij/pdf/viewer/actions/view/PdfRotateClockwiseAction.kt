package com.firsttimeinforever.intellij.pdf.viewer.actions.view

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfAction
import com.intellij.openapi.actionSystem.AnActionEvent

// TODO: Add icon
class PdfRotateClockwiseAction : PdfAction() {
  override fun actionPerformed(event: AnActionEvent) {
    findController(event)?.rotate(clockwise = true)
  }
}
