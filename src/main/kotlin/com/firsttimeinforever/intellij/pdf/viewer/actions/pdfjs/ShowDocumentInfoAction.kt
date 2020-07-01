package com.firsttimeinforever.intellij.pdf.viewer.actions.pdfjs

import com.intellij.openapi.actionSystem.AnActionEvent

class ShowDocumentInfoAction: PdfEditorPdfjsAction() {
    override fun actionPerformed(event: AnActionEvent) {
        getPanel(event)?.getDocumentInfo()
    }
}
