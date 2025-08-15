package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction

/**
 * Ensure [PdfToggleAction.update] is called before your code.
 */
abstract class PdfToggleAction(viewModeAwareness: ViewModeAwareness = ViewModeAwareness.IDE_ONLY) : ToggleAction() {
  protected open val base: PdfAction = StubAction(viewModeAwareness)

  override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

  override fun update(event: AnActionEvent) {
    super.update(event)
    base.update(event)
  }

  private class StubAction(viewModeAwareness: ViewModeAwareness) : PdfAction(viewModeAwareness) {
    override fun actionPerformed(event: AnActionEvent) {
      throw IllegalStateException("This method should not be called")
    }
  }
}
