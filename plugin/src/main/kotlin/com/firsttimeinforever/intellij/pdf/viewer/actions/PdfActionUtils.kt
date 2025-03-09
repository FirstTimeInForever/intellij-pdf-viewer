package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.intellij.ide.DataManager
import com.intellij.ide.ui.customization.CustomActionsSchema
import com.intellij.openapi.actionSystem.*
import java.awt.Component
import javax.swing.JComponent

internal object PdfActionUtils {
  fun createActionToolbar(groupId: String, place: String, targetComponent: JComponent, horizontal: Boolean = true, customizable: Boolean = true): ActionToolbar {
    val group = if (customizable) CustomActionsSchema.getInstance().getCorrectedAction(groupId) else ActionManager.getInstance().getAction(groupId)
    checkNotNull(group)
    check(group is ActionGroup)
    return createActionToolbar(group, place, targetComponent, horizontal)
  }

  fun createActionToolbar(group: ActionGroup, place: String, targetComponent: JComponent, horizontal: Boolean = true, customizable: Boolean = true): ActionToolbar {
    val toolbar = ActionManager.getInstance().createActionToolbar(place, group, horizontal)
    toolbar.targetComponent = targetComponent
    return toolbar
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
