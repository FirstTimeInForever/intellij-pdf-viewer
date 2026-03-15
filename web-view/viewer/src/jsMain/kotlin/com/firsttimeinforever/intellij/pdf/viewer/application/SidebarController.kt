package com.firsttimeinforever.intellij.pdf.viewer.application

import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.ViewerAdapter
import com.firsttimeinforever.intellij.pdf.viewer.model.SidebarViewMode
import kotlin.js.Promise

class SidebarController(private val viewer: ViewerAdapter) {
  @ExperimentalStdlibApi
  fun getAvailableViewModes(): Promise<Set<SidebarViewMode>> {
    return viewer.viewerApp.pdfDocument.getOutline().then { outline ->
      buildSet {
        add(SidebarViewMode.THUMBNAILS)
        if (viewer.viewerApp.pdfAttachmentViewer.attachments != null) {
          add(SidebarViewMode.ATTACHMENTS)
        }
        if (viewer.viewerApp.pdfOutlineViewer._outline != undefined || outline != null) {
          add(SidebarViewMode.OUTLINE)
        }
      }
    }
  }

  fun switchViewMode(viewMode: SidebarViewMode) {
    console.log("Setting viewMode to: $viewMode")
    viewer.viewerApp.viewsManager.switchView(viewMode.ordinal, forceOpen = true)
  }

  val currentViewMode: SidebarViewMode
    get() {
      val viewValue = viewer.viewerApp.viewsManager.visibleView as Int
      console.log("Got viewMode value: ${SidebarViewMode.entries[viewValue]}")
      return SidebarViewMode.entries[viewValue]
    }
}
