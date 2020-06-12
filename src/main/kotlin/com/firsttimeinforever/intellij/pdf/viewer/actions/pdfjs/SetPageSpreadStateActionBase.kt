package com.firsttimeinforever.intellij.pdf.viewer.actions.pdfjs

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.PageSpreadState
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction

abstract class SetPageSpreadStateActionBase(
    private val targetState: PageSpreadState
): ToggleAction() {
    private val baseImplementation = PdfEditorPdfjsActionBaseImpl(
        disabledInPresentationMode = true,
        disableInIdePresentationMode = true
    )

    override fun update(event: AnActionEvent) {
        super.update(event)
        baseImplementation.update(event)
    }

    override fun isSelected(event: AnActionEvent): Boolean {
        val panel = baseImplementation.getPanel(event) ?: return false
        return panel.pageSpreadState == targetState
    }

    override fun setSelected(event: AnActionEvent, state: Boolean) {
        val panel = baseImplementation.getPanel(event) ?: return
        panel.pageSpreadState = targetState
    }
}
