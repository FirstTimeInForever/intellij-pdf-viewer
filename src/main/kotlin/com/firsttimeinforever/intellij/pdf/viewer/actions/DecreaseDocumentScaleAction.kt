package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager

class DecreaseDocumentScaleAction: AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        event.project?.let {
            val editor = FileEditorManager.getInstance(it).selectedEditor
            if (editor !is PdfFileEditor) {
                return
            }
            editor.decreaseScale()
        }
    }
}
