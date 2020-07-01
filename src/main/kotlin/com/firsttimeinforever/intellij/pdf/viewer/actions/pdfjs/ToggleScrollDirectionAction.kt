package com.firsttimeinforever.intellij.pdf.viewer.actions.pdfjs

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent

class ToggleScrollDirectionAction: PdfEditorPdfjsAction(
    disabledInPresentationMode = true
) {
    override fun update(event: AnActionEvent) {
        super.update(event)
        val panel = getPanel(event)?: return
        if (panel.isCurrentScrollDirectionHorizontal) {
            event.presentation.text = HORIZONTAL_TEXT
            event.presentation.description = HORIZONTAL_DESCRIPTION
            event.presentation.icon = AllIcons.Actions.SplitVertically
        }
        else {
            event.presentation.text = VERTICAL_TEXT
            event.presentation.description = VERTICAL_DESCRIPTION
            event.presentation.icon = AllIcons.Actions.SplitHorizontally
        }
    }

    override fun actionPerformed(event: AnActionEvent) {
        getPanel(event)?.toggleScrollDirection()
    }

    companion object {
        const val VERTICAL_TEXT = "Set Vertical Scrolling"
        const val HORIZONTAL_TEXT = "Set Horizontal Scrolling"
        const val VERTICAL_DESCRIPTION = "Sets vertical scrolling for document pages"
        const val HORIZONTAL_DESCRIPTION = "Sets horizontal scrolling for document pages"
    }
}
