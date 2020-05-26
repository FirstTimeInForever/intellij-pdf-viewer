package com.firsttimeinforever.intellij.pdf.viewer.actions.pdfjs

import com.intellij.openapi.actionSystem.AnActionEvent

// TODO: Add icon
class RotateClockwiseAction: PdfEditorPdfjsAction() {
    override fun actionPerformed(event: AnActionEvent) {
        getPanel(event)?.rotateClockwise()
    }
}
