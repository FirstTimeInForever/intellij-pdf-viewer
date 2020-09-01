package com.firsttimeinforever.intellij.pdf.viewer.actions.pdfjs

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.PageSpreadState
import com.intellij.openapi.actionSystem.AnActionEvent

abstract class SetPageSpreadStateActionBase(
    private val targetState: PageSpreadState
): PdfEditorPdfjsToggleActionAdapter(
    isDisabledInPresentationMode = true,
    isDisabledInIdePresentationMode = true
) {
    override fun isSelected(event: AnActionEvent): Boolean {
        val panel = getPanel(event) ?: return false
        return panel.pageSpreadState == targetState
    }

    override fun setSelected(event: AnActionEvent, state: Boolean) {
        val panel = getPanel(event) ?: return
        panel.pageSpreadState = targetState
    }
}

class SpreadEvenPagesAction: SetPageSpreadStateActionBase(PageSpreadState.EVEN)

class SpreadNonePagesAction: SetPageSpreadStateActionBase(PageSpreadState.NONE)

class SpreadOddPagesAction: SetPageSpreadStateActionBase(PageSpreadState.ODD)
