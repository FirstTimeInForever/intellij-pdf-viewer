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
    if (currentViewMode == SidebarViewMode.NONE) {
      viewer.viewerApp.pdfSidebar.open()
    }
    viewer.viewerApp.pdfSidebar.switchView(viewMode.ordinal)
  }

  val currentViewMode: SidebarViewMode
    get() {
      // console.log(viewer.viewerApp.pdfSidebar)
      val viewValue = viewer.viewerApp.pdfSidebar.visibleView as Int
      console.log("Got viewMode value: $viewValue")
      return SidebarViewMode.entries[viewValue]
    }
}
