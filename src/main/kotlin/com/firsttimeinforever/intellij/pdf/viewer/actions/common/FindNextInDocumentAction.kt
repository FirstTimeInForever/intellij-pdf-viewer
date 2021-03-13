package com.firsttimeinforever.intellij.pdf.viewer.actions.common

import com.firsttimeinforever.intellij.pdf.viewer.actions.ActionUtils.findPdfFileEditor
import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfEditorAction
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.PdfFileEditorJcefPanel
import com.intellij.openapi.actionSystem.AnActionEvent

class FindNextInDocumentAction: PdfEditorAction() {
    override fun actionPerformed(event: AnActionEvent) {
        findPdfFileEditor(event)?.viewPanel?.findNext()
    }

    override fun update(event: AnActionEvent) {
        super.update(event)
        val editor = findPdfFileEditor(event) ?: return
        event.presentation.isEnabled = if (editor.viewPanel is PdfFileEditorJcefPanel) {
            !editor.viewPanel.presentationModeController.isPresentationModeActive()
        }
        else true
    }
}
