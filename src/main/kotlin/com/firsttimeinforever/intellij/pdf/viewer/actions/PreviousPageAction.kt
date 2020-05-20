package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.intellij.openapi.actionSystem.AnActionEvent

class PreviousPageAction: PdfEditorAction() {
    override fun actionPerformed(event: AnActionEvent) {
        getEditor(event)?.previousPage()
    }
}
