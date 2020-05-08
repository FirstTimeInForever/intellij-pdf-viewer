package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionToolbar
import java.awt.Dimension

internal inline fun <reified Group: ActionGroup> createToolbarForGroup(groupId: String): ActionToolbar {
    val group = ActionManager.getInstance().getAction(groupId)
    check(group != null)
    check(group is Group)
    return ActionManager.getInstance().createActionToolbar("", group, true)
}

internal fun setActionToolbarSize(toolbar: ActionToolbar, dim: Dimension) {
    toolbar.component.maximumSize = dim
    toolbar.component.size = dim
    toolbar.component.preferredSize = dim
}
