package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.PdfFileEditorJcefPanel
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.fileEditor.FileEditorManager

class PdfFileEditorActionGroup: DefaultActionGroup() {}

class PdfEditorLeftToolbarActionGroup: DefaultActionGroup() {}

class PdfEditorToolbarSearchActionGroup: DefaultActionGroup() {}

class PdfEditorRightToolbarActionGroup: DefaultActionGroup() {}

class PdfEditorSidebarViewModeActionGroup: DefaultActionGroup() {
    override fun isPopup(): Boolean = true

    override fun update(event: AnActionEvent) {
        event.presentation.isEnabled = false
        event.project?.let { project ->
            FileEditorManager.getInstance(project).selectedEditor?.also {
                if (it is PdfFileEditor && it.viewPanel is PdfFileEditorJcefPanel) {
                    event.presentation.isEnabled =
                        !it.viewPanel.presentationModeController.isPresentationModeActive()
                }
            }
        }
    }
}
