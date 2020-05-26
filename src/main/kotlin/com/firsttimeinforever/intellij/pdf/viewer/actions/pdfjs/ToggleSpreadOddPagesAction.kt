package com.firsttimeinforever.intellij.pdf.viewer.actions.pdfjs

import com.intellij.openapi.actionSystem.AnActionEvent

// TODO: Add icon
class ToggleSpreadOddPagesAction: PdfEditorPdfjsAction() {
    override val disabledInPresentationMode = true

    override fun actionPerformed(event: AnActionEvent) {
        getPanel(event)?.toggleSpreadOddPages()
    }
}
