package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.fileEditor.FileEditorManager

class PdfActionGroup : DefaultActionGroup()

class PdfLeftToolbarActionGroup : DefaultActionGroup()

class PdfToolbarSearchActionGroup : DefaultActionGroup()

class PdfRightToolbarActionGroup : DefaultActionGroup()

class PdfSidebarViewModeActionGroup : DefaultActionGroup() {
  override fun isPopup(): Boolean = true

  override fun update(event: AnActionEvent) {
    event.presentation.isEnabled = false
    val project = event.project ?: return
    val editor = FileEditorManager.getInstance(project).selectedEditor ?: return
    // TODO: Should be disabled in presentation mode
    event.presentation.isEnabled = editor is PdfFileEditor && editor.viewComponent.controller != null
  }
}
