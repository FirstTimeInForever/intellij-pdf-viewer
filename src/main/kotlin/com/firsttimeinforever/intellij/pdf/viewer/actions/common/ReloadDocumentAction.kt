package com.firsttimeinforever.intellij.pdf.viewer.actions.common

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfEditorAction
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.PdfFileEditorJcefPanel
import com.intellij.openapi.actionSystem.AnActionEvent

class ReloadDocumentAction: PdfEditorAction() {
    override fun actionPerformed(event: AnActionEvent) {
        getEditor(event)?.reloadDocument()
    }

    override fun update(event: AnActionEvent) {
        super.update(event)
        val editor = getEditor(event)?: return
        when (editor.viewPanel) {
            is PdfFileEditorJcefPanel -> {
                event.presentation.isEnabled = !editor.viewPanel.isPresentationModeActive()
            }
            else -> {
                event.presentation.isEnabled = true
            }
        }
    }
}
