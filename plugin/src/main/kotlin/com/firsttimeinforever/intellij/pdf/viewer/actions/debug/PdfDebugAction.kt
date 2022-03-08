package com.firsttimeinforever.intellij.pdf.viewer.actions.debug

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfAction
import com.firsttimeinforever.intellij.pdf.viewer.settings.PdfViewerSettings
import com.intellij.openapi.actionSystem.AnActionEvent
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
internal abstract class PdfDebugAction : PdfAction() {
  override fun update(event: AnActionEvent) {
    when {
      PdfViewerSettings.isDebugMode -> super.update(event)
      else -> event.presentation.isEnabledAndVisible = false
    }
  }
}
