package com.firsttimeinforever.intellij.pdf.viewer.actions.common

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfEditorAction
import com.intellij.openapi.actionSystem.AnActionEvent

class PreviousPageAction: PdfEditorAction() {
    override val disableInIdePresentationMode: Boolean = false

    override fun actionPerformed(event: AnActionEvent) {
        getEditor(event)?.previousPage()
    }

    override fun update(event: AnActionEvent) {
        super.update(event)
        val editor = getEditor(event)?: return
        if (editor.viewPanel.pagesCount == 0) {
            event.presentation.isEnabled = false
        }
        else {
            event.presentation.isEnabled = (editor.viewPanel.currentPageNumber != 1)
        }
    }
}
