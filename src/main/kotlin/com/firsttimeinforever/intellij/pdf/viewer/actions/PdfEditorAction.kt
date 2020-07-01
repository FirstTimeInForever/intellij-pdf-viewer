package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.intellij.ide.ui.UISettings
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager

abstract class PdfEditorAction(
    val disableInIdePresentationMode: Boolean = true
): AnAction() {
    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = haveVisibleEditor(event)
        if (UISettings.instance.presentationMode && disableInIdePresentationMode) {
            event.presentation.isEnabledAndVisible = false
        }
    }

    open fun haveVisibleEditor(event: AnActionEvent): Boolean {
        return haveVisibleEditor(event) {
            it is PdfFileEditor
        }
    }

    fun haveVisibleEditor(
        event: AnActionEvent,
        predicate: (FileEditor) -> Boolean
    ): Boolean {
        val project = event.project?: return false
        return FileEditorManager.getInstance(project).selectedEditors.any(predicate)
    }
}
