package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.intellij.openapi.actionSystem.AnActionEvent

class FindNextInDocumentAction: PdfEditorAction() {
    override fun actionPerformed(event: AnActionEvent) {
        getEditor(event)?.findNext()
    }
}
