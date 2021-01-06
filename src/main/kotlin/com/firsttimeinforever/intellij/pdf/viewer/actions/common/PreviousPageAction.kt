package com.firsttimeinforever.intellij.pdf.viewer.actions.common

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfEditorAction
import com.firsttimeinforever.intellij.pdf.viewer.actions.findPdfFileEditor
import com.intellij.openapi.actionSystem.AnActionEvent

class PreviousPageAction: PdfEditorAction(
    disableInIdePresentationMode = false
) {
    override fun actionPerformed(event: AnActionEvent) {
        findPdfFileEditor(event)?.previousPage()
    }

    override fun update(event: AnActionEvent) {
        super.update(event)
        val editor = findPdfFileEditor(event) ?: return
        event.presentation.isEnabled = if (editor.viewPanel.pagesCount != 0) {
            editor.viewPanel.currentPageNumber != 1
        }
        else false
    }
}
