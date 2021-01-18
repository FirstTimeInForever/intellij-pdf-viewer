package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager

internal object ActionUtils {
    fun findPdfFileEditor(event: AnActionEvent): PdfFileEditor? {
        val project = event.project?: return null
        val editor = FileEditorManager.getInstance(project).selectedEditor ?: return null
        return when (editor) {
            is PdfFileEditor -> editor
            else -> null
        }
    }
}
