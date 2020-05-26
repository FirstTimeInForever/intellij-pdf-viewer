package com.firsttimeinforever.intellij.pdf.viewer.actions.pdfjs

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent

class ToggleDocumentPresentationModeAction: PdfEditorPdfjsAction() {
    override val disableInIdePresentationMode: Boolean = false

    override fun actionPerformed(event: AnActionEvent) {
        getPanel(event)?.toggleFullscreenMode()
    }

    override fun update(event: AnActionEvent) {
        super.update(event)
        val panel = getPanel(event)?: return
        if (panel.isPresentationModeActive()) {
            event.presentation.text = ACTIVE_TEXT
            event.presentation.description = ACTIVE_DESCRIPTION
            event.presentation.icon = AllIcons.General.CollapseComponentHover
        }
        else {
            event.presentation.text = NORMAL_TEXT
            event.presentation.description = NORMAL_DESCRIPTION
            event.presentation.icon = AllIcons.General.ExpandComponentHover
        }
    }

    companion object {
        private const val NORMAL_TEXT = "Enter PDF Presentation Mode"
        private const val NORMAL_DESCRIPTION = "Enters document presentation mode"
        private const val ACTIVE_TEXT = "Exit PDF Presentation Mode"
        private const val ACTIVE_DESCRIPTION = "Exits document presentation mode"
    }
}
