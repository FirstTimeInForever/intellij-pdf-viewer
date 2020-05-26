package com.firsttimeinforever.intellij.pdf.viewer.actions.pdfjs

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.util.registry.Registry

class OpenDevtoolsAction: PdfEditorPdfjsAction() {
    override fun actionPerformed(event: AnActionEvent) {
        getPanel(event)?.openDevtools()
    }

    override fun update(event: AnActionEvent) {
        if (Registry.`is`("pdf.viewer.debug", false)) {
            super.update(event)
        }
        else {
            event.presentation.isEnabledAndVisible = false
        }
    }
}
