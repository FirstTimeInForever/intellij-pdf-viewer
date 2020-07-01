package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.fileEditor.FileEditor

abstract class PdfEditorToggleActionAdapter(
    isDisabledInIdePresentationMode: Boolean = true
): ToggleAction() {
    protected open val base: PdfEditorAction = object: PdfEditorAction(
        isDisabledInIdePresentationMode
    ) {
        override fun actionPerformed(event: AnActionEvent) {
            throw IllegalAccessException("This method should not be called")
        }
    }

    open val disabledInIdePresentationMode
        get() = base.disableInIdePresentationMode

    override fun update(event: AnActionEvent) {
        super.update(event)
        base.update(event)
    }

    open fun haveVisibleEditor(event: AnActionEvent): Boolean =
        base.haveVisibleEditor(event)

    fun haveVisibleEditor(
        event: AnActionEvent,
        predicate: (FileEditor) -> Boolean
    ): Boolean =
        base.haveVisibleEditor(event, predicate)
}
