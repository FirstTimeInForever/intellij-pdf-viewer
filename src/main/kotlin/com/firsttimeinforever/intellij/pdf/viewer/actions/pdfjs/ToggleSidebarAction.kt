package com.firsttimeinforever.intellij.pdf.viewer.actions.pdfjs

import com.intellij.openapi.actionSystem.AnActionEvent

class ToggleSidebarAction: PdfEditorPdfjsAction(
    disabledInIdePresentationMode = false,
    disabledInPresentationMode = true
) {
    override fun actionPerformed(event: AnActionEvent) {
        getPanel(event)?.toggleSidebar()
    }
}
