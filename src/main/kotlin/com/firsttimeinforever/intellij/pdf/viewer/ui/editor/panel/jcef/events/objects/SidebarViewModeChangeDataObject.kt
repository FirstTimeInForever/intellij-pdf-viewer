package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.objects

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.SidebarViewMode
import kotlinx.serialization.Serializable

@Serializable
data class SidebarViewModeChangeDataObject(val mode: String) {
    companion object {
        fun from(mode: SidebarViewMode) =
            SidebarViewModeChangeDataObject(mode.displayName)
    }
}
