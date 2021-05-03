package com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs

import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.SidebarViewMode

internal object Internals {
  /*
  const SidebarView = {
    UNKNOWN: -1,
    NONE: 0,
    THUMBS: 1, // Default value.
    OUTLINE: 2,
    ATTACHMENTS: 3,
    LAYERS: 4,
  };
   */
  // TODO: Move to ViewerAdapter
  // FIXME: Replace with correct values
  fun SidebarViewMode.mapToInternalValue(): Int {
    return when (this) {
      SidebarViewMode.NONE -> 0
      SidebarViewMode.THUMBNAILS -> 1
      SidebarViewMode.OUTLINE -> 2
      SidebarViewMode.ATTACHMENTS -> 3
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
