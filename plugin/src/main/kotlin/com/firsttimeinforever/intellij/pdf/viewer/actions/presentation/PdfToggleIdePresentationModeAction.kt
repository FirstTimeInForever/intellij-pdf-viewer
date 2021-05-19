package com.firsttimeinforever.intellij.pdf.viewer.actions.presentation

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfDumbAwareAction
import com.firsttimeinforever.intellij.pdf.viewer.actions.ViewModeAwareness
import com.intellij.ide.actions.TogglePresentationModeAction
import com.intellij.ide.ui.UISettings
import com.intellij.openapi.actionSystem.AnActionEvent

class PdfToggleIdePresentationModeAction: PdfDumbAwareAction(ViewModeAwareness.BOTH) {
  override fun actionPerformed(event: AnActionEvent) {
    TogglePresentationModeAction.setPresentationMode(event.project, !UISettings.instance.presentationMode)
  }
}
