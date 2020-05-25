package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.intellij.openapi.actionSystem.AnActionEvent

class PrintDocumentAction: PdfEditorPdfjsAction() {
    override fun actionPerformed(event: AnActionEvent) {
        getPanel(event)?.printDocument()
    }

    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = false
    }
}
