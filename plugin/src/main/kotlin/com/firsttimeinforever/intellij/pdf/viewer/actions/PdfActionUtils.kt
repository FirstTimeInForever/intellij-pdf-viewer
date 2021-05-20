package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.*
import java.awt.Component

internal object PdfActionUtils {
  fun createActionToolbar(groupId: String, horizontal: Boolean = true): ActionToolbar {
    val group = ActionManager.getInstance().getAction(groupId)
    checkNotNull(group)
    check(group is ActionGroup)
    return createActionToolbar(group, horizontal)
  }

  fun createActionToolbar(group: ActionGroup, horizontal: Boolean = true): ActionToolbar {
    return ActionManager.getInstance().createActionToolbar("", group, horizontal)
  }

  fun performAction(action: AnAction, component: Component) {
    val context = DataManager.getInstance().getDataContext(component)
    action.actionPerformed(
      AnActionEvent(
        null,
        context,
        ActionPlaces.UNKNOWN,
        Presentation(),
        ActionManager.getInstance(),
        0
      )
    )
  }

  fun performAction(actionId: String, component: Component) {
    val manager = ActionManager.getInstance()
    val action = manager.getAction(actionId)
    checkNotNull(action)
    performAction(action, component)
  }
}
