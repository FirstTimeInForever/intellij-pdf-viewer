package com.firsttimeinforever.intellij.pdf.viewer.actions.debug

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.util.registry.Registry

internal abstract class PdfDebugAction(disableInIdePresentationMode: Boolean = false): PdfAction(disableInIdePresentationMode) {
    override fun update(event: AnActionEvent) {
        when {
            Registry.`is`("pdf.viewer.debug", false) -> super.update(event)
            else -> event.presentation.isEnabledAndVisible = false
        }
    }
}
