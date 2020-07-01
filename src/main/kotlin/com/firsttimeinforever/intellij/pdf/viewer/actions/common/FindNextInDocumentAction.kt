package com.firsttimeinforever.intellij.pdf.viewer.actions.common

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfEditorAction
import com.firsttimeinforever.intellij.pdf.viewer.actions.findPdfFileEditor
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.PdfFileEditorJcefPanel
import com.intellij.openapi.actionSystem.AnActionEvent

class FindNextInDocumentAction: PdfEditorAction() {
    override fun actionPerformed(event: AnActionEvent) {
        findPdfFileEditor(event)?.findNext()
    }

    override fun update(event: AnActionEvent) {
        super.update(event)
        findPdfFileEditor(event)?.also {
            when (it.viewPanel) {
                is PdfFileEditorJcefPanel -> {
                    event.presentation.isEnabled =
                        !it.viewPanel.presentationModeController.isPresentationModeActive()
                }
                else -> {
                    event.presentation.isEnabled = true
                }
            }
        }
    }
}
