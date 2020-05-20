package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager

abstract class PdfEditorAction: AnAction() {
    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = getEditor(event) != null
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
