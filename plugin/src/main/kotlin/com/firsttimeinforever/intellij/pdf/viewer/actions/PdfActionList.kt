package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerActionsBundle
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.popup.JBPopupFactory

class PdfActionList: PdfAction() {
  override fun actionPerformed(event: AnActionEvent) {
    val group = ActionManager.getInstance().getAction("pdf.viewer.ViewerActionsList") as ActionGroup
    JBPopupFactory.getInstance().createActionGroupPopup(
      PdfViewerActionsBundle.message("action.pdf.viewer.ActionList.text"),
      group,
      event.dataContext,
      JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
      true
    ).showInFocusCenter()
  }
}
