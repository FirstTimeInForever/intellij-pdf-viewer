package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction

abstract class PdfToggleAction(
    isDisabledInIdePresentationMode: Boolean = true
): ToggleAction() {
    protected open val base: PdfAction = StubAction(isDisabledInIdePresentationMode)

    open val disabledInIdePresentationMode
        get() = base.disableInIdePresentationMode

    override fun update(event: AnActionEvent) {
        super.update(event)
        base.update(event)
    }

    private inner class StubAction(isDisabledInIdePresentationMode: Boolean):
        PdfAction(isDisabledInIdePresentationMode)
    {
        override fun actionPerformed(event: AnActionEvent) {
            throw IllegalStateException("This method should not be called")
        }
    }
}
