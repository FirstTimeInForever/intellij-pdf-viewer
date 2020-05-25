package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager

abstract class PdfEditorAction: AnAction() {
    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = haveVisibleEditor(event)
        super.update(event)
    }

    open fun haveVisibleEditor(event: AnActionEvent): Boolean {
        return haveVisibleEditor(event) {
            it is PdfFileEditor
        }
    }

    fun haveVisibleEditor(event: AnActionEvent, predicate: (FileEditor) -> Boolean): Boolean {
        val project = event.project?: return false
        return FileEditorManager.getInstance(project).selectedEditors.any(predicate)
    }

    fun getEditor(event: AnActionEvent): PdfFileEditor? {
        val project = event.project?: return null
        val editor = FileEditorManager.getInstance(project).selectedEditor?: return null
        if (editor !is PdfFileEditor) {
            return null
        }
        return editor
    }
}
