package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.intellij.openapi.actionSystem.AnActionEvent

class IncreaseDocumentScaleAction: PdfEditorAction() {
    override fun actionPerformed(event: AnActionEvent) {
        getEditor(event)?.increaseScale()
    }
}
