package com.firsttimeinforever.intellij.pdf.viewer.actions.view

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerBundle
import com.firsttimeinforever.intellij.pdf.viewer.actions.old.pdfjs.PdfPdfjsAction
import com.intellij.openapi.actionSystem.AnActionEvent

class ToggleScrollDirectionAction : PdfPdfjsAction(
  disabledInPresentationMode = true
) {

  override fun actionPerformed(event: AnActionEvent) {
    // val panel = getPanel(event) ?: return
    // // This is needed to prevent weird behaviour
    // panel.pageSpreadState = PageSpreadState.NONE
    // panel.toggleScrollDirection()
  }

  private companion object {
    val VERTICAL_TEXT = PdfViewerBundle.message("pdf.viewer.actions.pdfjs.set.vertical.scrolling.name")
    val VERTICAL_DESCRIPTION = PdfViewerBundle.message("pdf.viewer.actions.pdfjs.set.vertical.scrolling.description")
    val HORIZONTAL_TEXT = PdfViewerBundle.message("pdf.viewer.actions.pdfjs.set.horizontal.scrolling.name")
    val HORIZONTAL_DESCRIPTION =
      PdfViewerBundle.message("pdf.viewer.actions.pdfjs.set.horizontal.scrolling.description")
  }
}
