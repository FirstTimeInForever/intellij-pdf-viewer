package com.firsttimeinforever.intellij.pdf.viewer.actions.debug

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.util.registry.Registry
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
internal abstract class PdfDebugAction : PdfAction() {
  override fun update(event: AnActionEvent) {
    when {
      Registry.`is`("pdf.viewer.debug", false) -> super.update(event)
      else -> event.presentation.isEnabledAndVisible = false
    }
  }
}
