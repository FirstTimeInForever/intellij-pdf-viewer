package com.firsttimeinforever.intellij.pdf.viewer.actions.view

import com.firsttimeinforever.intellij.pdf.viewer.actions.old.pdfjs.PdfPdfjsAction
import com.intellij.openapi.actionSystem.AnActionEvent

// TODO: Add icon
class RotateCounterClockwiseAction : PdfPdfjsAction(
  disabledInIdePresentationMode = false
) {
  override fun actionPerformed(event: AnActionEvent) {
    // getPanel(event)?.rotateCounterClockwise()
  }
}
