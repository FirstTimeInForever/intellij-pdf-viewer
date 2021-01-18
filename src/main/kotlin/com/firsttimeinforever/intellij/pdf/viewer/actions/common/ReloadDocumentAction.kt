package com.firsttimeinforever.intellij.pdf.viewer.actions.common

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfEditorAction
import com.firsttimeinforever.intellij.pdf.viewer.actions.findPdfFileEditor
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.PdfFileEditorJcefPanel
import com.intellij.openapi.actionSystem.AnActionEvent

class ReloadDocumentAction: PdfEditorAction() {
    override fun actionPerformed(event: AnActionEvent) {
        findPdfFileEditor(event)?.viewPanel?.reloadDocument()
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
