package com.firsttimeinforever.intellij.pdf.viewer.actions.pdfjs

import com.firsttimeinforever.intellij.pdf.viewer.settings.PdfViewerSettings
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.util.registry.Registry

class ToggleInvertDocumentColorsAction: PdfEditorPdfjsToggleActionAdapter(
    isDisabledInPresentationMode = true
) {
    override fun isSelected(event: AnActionEvent) = PdfViewerSettings.instance.invertDocumentColors

    override fun setSelected(event: AnActionEvent, state: Boolean) {
        PdfViewerSettings.instance.invertDocumentColors = state
        PdfViewerSettings.instance.callChangeListeners()
    }

    override fun update(event: AnActionEvent) {
        super.update(event)
        if (!Registry.`is`("pdf.viewer.enableExperimentalFeatures")) {
            event.presentation.isEnabled = false
            event.presentation.isVisible = false
        }
    }
}
