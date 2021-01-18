package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionToolbar

internal object ActionToolbarUtils {
    inline fun <reified Group: ActionGroup> createToolbarForGroup(groupId: String): ActionToolbar {
        val group = ActionManager.getInstance().getAction(groupId)
        check(group != null)
        check(group is Group)
        return ActionManager.getInstance().createActionToolbar("", group, true)
    }
}
