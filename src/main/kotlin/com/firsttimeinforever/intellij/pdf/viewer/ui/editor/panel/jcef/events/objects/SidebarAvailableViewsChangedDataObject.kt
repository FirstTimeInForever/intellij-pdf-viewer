package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.objects

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.SidebarViewMode
import kotlinx.serialization.Serializable

@Serializable
data class SidebarAvailableViewModes(
    val thumbnails: Boolean = true,
    val bookmarks: Boolean = false,
    val attachments: Boolean = false
) {
    fun isViewModeAvailable(view: SidebarViewMode): Boolean {
        return when (view) {
            SidebarViewMode.ATTACHMENTS -> attachments
            SidebarViewMode.BOOKMARKS -> bookmarks
            SidebarViewMode.THUMBNAILS -> thumbnails
        }
    }
}

typealias SidebarAvailableViewModesChangedDataObject = SidebarAvailableViewModes
