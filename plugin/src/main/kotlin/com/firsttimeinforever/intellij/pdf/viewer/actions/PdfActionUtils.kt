package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.*
import java.awt.Component

internal object PdfActionUtils {
    fun createActionToolbar(groupId: String, horizontal: Boolean = true): ActionToolbar {
        val group = ActionManager.getInstance().getAction(groupId)
        checkNotNull(group)
        check(group is ActionGroup)
        return ActionManager.getInstance().createActionToolbar("", group, horizontal)
    }

    fun performAction(actionId: String, component: Component) {
        val manager = ActionManager.getInstance()
        val action = manager.getAction(actionId)
        val context = DataManager.getInstance().getDataContext(component)
        action.actionPerformed(AnActionEvent(
            null,
            context,
            ActionPlaces.UNKNOWN,
            Presentation(),
            manager,
            0
        ))
    }
}
