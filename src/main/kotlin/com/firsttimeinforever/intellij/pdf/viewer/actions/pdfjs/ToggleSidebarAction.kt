package com.firsttimeinforever.intellij.pdf.viewer.actions.pdfjs

import com.intellij.openapi.actionSystem.AnActionEvent

class ToggleSidebarAction: PdfEditorPdfjsAction(
    disabledInPresentationMode = true
) {
    override fun actionPerformed(event: AnActionEvent) {
        getPanel(event)?.toggleSidebar()
    }
}
