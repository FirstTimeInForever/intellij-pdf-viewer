package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction

/**
 * Ensure [PdfToggleAction.update] is called before your code.
 */
abstract class PdfToggleAction : ToggleAction() {
  protected open val base: PdfAction = StubAction()

  override fun update(event: AnActionEvent) {
    super.update(event)
    base.update(event)
  }

  private inner class StubAction : PdfAction() {
    override fun actionPerformed(event: AnActionEvent) {
      throw IllegalStateException("This method should not be called")
    }
  }
}
