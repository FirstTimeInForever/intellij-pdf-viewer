package com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs

import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.SidebarViewMode

internal object Internals {
    // TODO: Move to ViewerAdapter
    // FIXME: Replace with correct values
    fun SidebarViewMode.mapToInternalValue(): Int {
        return when (this) {
            SidebarViewMode.NONE -> 0
            SidebarViewMode.THUMBNAILS -> 1
            SidebarViewMode.ATTACHMENTS -> 2
            SidebarViewMode.BOOKMARKS -> 3
        }
    }

    // --treeitem-expanded-icon: url(images/treeitem-expanded.png);
    // --treeitem-collapsed-icon: url(images/treeitem-collapsed.png);
    object StyleVariables {
        const val mainColor = "--main-color"
        const val bodyBackgroundColor = "--body-bg-color"
        const val sidebarBackgroundColor = "--sidebar-bg-color"
    }
}
