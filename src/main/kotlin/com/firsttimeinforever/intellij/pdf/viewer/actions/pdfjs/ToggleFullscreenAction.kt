package com.firsttimeinforever.intellij.pdf.viewer.actions.pdfjs

import com.intellij.ide.actions.TogglePresentationModeAction
import com.intellij.ide.ui.UISettings
import com.intellij.openapi.actionSystem.AnActionEvent

class ToggleFullscreenAction: PdfEditorPdfjsAction(
    disabledInIdePresentationMode = false
) {
    override fun actionPerformed(event: AnActionEvent) {
        TogglePresentationModeAction.setPresentationMode(
            event.project,
            !UISettings.instance.presentationMode
        )
    }
}
