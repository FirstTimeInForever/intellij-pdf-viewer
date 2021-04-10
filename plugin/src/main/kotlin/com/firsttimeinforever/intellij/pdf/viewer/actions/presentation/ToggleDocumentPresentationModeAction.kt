package com.firsttimeinforever.intellij.pdf.viewer.actions.presentation

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerBundle
import com.firsttimeinforever.intellij.pdf.viewer.actions.old.pdfjs.PdfPdfjsAction
import com.intellij.openapi.actionSystem.AnActionEvent

class ToggleDocumentPresentationModeAction : PdfPdfjsAction(
  disabledInIdePresentationMode = false
) {
  override fun actionPerformed(event: AnActionEvent) {
    // val panel = getPanel(event)?: return
    // panel.presentationModeController.togglePresentationMode()
  }

  private companion object {
    val NORMAL_TEXT = PdfViewerBundle.message("pdf.viewer.actions.pdfjs.enter.pdf.presentation.mode.name")
    val NORMAL_DESCRIPTION = PdfViewerBundle.message("pdf.viewer.actions.pdfjs.enter.pdf.presentation.mode.description")
    val ACTIVE_TEXT = PdfViewerBundle.message("pdf.viewer.actions.pdfjs.exit.pdf.presentation.mode.name")
    val ACTIVE_DESCRIPTION = PdfViewerBundle.message("pdf.viewer.actions.pdfjs.exit.pdf.presentation.mode.description")
  }
}
