package com.firsttimeinforever.intellij.pdf.viewer.actions.common

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfEditorAction
import com.intellij.openapi.actionSystem.AnActionEvent

class ReloadDocumentAction: PdfEditorAction() {
    override fun actionPerformed(event: AnActionEvent) {
        getEditor(event)?.reloadDocument()
    }
}
