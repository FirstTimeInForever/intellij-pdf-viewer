package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.PdfFileEditorJcefPanel
import com.intellij.openapi.actionSystem.AnActionEvent

class TogglePdfjsToolbarAction: PdfEditorAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val editor = getEditor(event)?: return
        when (editor.viewPanel) {
            is PdfFileEditorJcefPanel -> editor.viewPanel.togglePdfjsToolbar()
            else -> showUnsupportedActionNotification(event)
        }
    }
}
