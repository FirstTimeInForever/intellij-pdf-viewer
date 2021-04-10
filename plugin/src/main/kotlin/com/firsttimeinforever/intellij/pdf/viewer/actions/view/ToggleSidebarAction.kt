package com.firsttimeinforever.intellij.pdf.viewer.actions.view

import com.firsttimeinforever.intellij.pdf.viewer.actions.old.pdfjs.PdfPdfjsAction
import com.intellij.openapi.actionSystem.AnActionEvent

class ToggleSidebarAction : PdfPdfjsAction(
  disabledInIdePresentationMode = false,
  disabledInPresentationMode = true
) {
  override fun actionPerformed(event: AnActionEvent) {
    // getPanel(event)?.toggleSidebar()
  }
}
