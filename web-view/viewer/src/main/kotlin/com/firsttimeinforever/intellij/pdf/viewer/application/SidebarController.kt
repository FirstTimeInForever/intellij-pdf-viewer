package com.firsttimeinforever.intellij.pdf.viewer.application

import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.ViewerAdapter
import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.SidebarViewMode
import kotlin.js.Promise

class SidebarController(private val viewer: ViewerAdapter) {
  @ExperimentalStdlibApi
  fun getAvailableViewModes(): Promise<Set<SidebarViewMode>> {
    return viewer.viewerApp.pdfDocument.getOutline().then { outline: dynamic ->
      buildSet {
        add(SidebarViewMode.THUMBNAILS)
        if (viewer.viewerApp.pdfAttachmentViewer.attachments != null) {
          add(SidebarViewMode.ATTACHMENTS)
        }
        if (viewer.viewerApp.pdfOutlineViewer.outline != null || outline != null) {
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
      console.log(viewer.viewerApp.pdfSidebar)
      val viewValue = viewer.viewerApp.pdfSidebar.visibleView as Int
      console.log("Got viewMode value: $viewValue")
      return SidebarViewMode.values()[viewValue]
    }
}
