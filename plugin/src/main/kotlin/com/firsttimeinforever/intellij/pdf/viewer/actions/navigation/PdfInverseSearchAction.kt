package com.firsttimeinforever.intellij.pdf.viewer.actions.navigation

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfAction
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager

/**
 * This is not the actual inverse search action, which is done in SynctexSearchController.
 * However, we need to provide a way for the user to change the default shortcut.
 *
 * @author Thomas
 */
class PdfInverseSearchAction : AnAction() {
  override fun actionPerformed(e: AnActionEvent) {
    val shortcuts = shortcutSet.shortcuts.map { it.toString() }.toSet()
    // Send to browser which shortcut should be listened to
    FileEditorManager.getInstance(e.project ?: return).allEditors.filterIsInstance<PdfFileEditor>().forEach {
      PdfAction.findController(it)?.setInverseSearchShortcuts(shortcuts)
    }
  }
}
