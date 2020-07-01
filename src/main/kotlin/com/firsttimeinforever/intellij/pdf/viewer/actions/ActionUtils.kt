package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager

fun findPdfFileEditor(event: AnActionEvent): PdfFileEditor? {
    val project = event.project?: return null
    return FileEditorManager.getInstance(project).selectedEditor?.let {
        when (it) {
            is PdfFileEditor -> it
            else -> null
        }
    }
}
