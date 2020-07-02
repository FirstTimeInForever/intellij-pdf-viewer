package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.objects

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.SidebarViewMode
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.SidebarViewState
import kotlinx.serialization.Serializable

@Serializable
data class SidebarViewStateChangeDataObject(
    val mode: String,
    val hidden: Boolean
) {
    val state
        get() = SidebarViewState(SidebarViewMode.from(mode), hidden)

    companion object {
        fun from(state: SidebarViewState) =
            SidebarViewStateChangeDataObject(
                state.mode.displayName,
                state.hidden
            )
    }
}
