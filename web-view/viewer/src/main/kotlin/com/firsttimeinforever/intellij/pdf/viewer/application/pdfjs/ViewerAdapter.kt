package com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs

import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.types.PdfViewerApplication
import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.PageSpreadState
import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.ZoomMode
import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.ZoomState

class ViewerAdapter(val viewerApp: PdfViewerApplication) {
  fun addEventListener(event: String, listener: (dynamic) -> Unit) {
    viewerApp.eventBus.on(event, listener)
  }

  val pagesCount: Int
    get() = viewerApp.pdfViewer.pagesCount

  var currentPageNumber: Int
    get() = viewerApp.pdfViewer.currentPageNumber
    set(value) {
      require(value in 1..pagesCount)
      viewerApp.pdfViewer.currentPageNumber = value
    }

  /*
  const SpreadMode = {
    UNKNOWN: -1,
    NONE: 0, // Default value.
    ODD: 1,
    EVEN: 2,
  };
   */
  var pageSpreadState: PageSpreadState
    get() = PageSpreadState.values()[viewerApp.pdfViewer.spreadMode]
    set(value) {
      viewerApp.pdfViewer.spreadMode = value.ordinal
    }

  val zoomState: ZoomState
    get() = ZoomState(
      mode = when (viewerApp.pdfViewer.currentScaleValue) {
        "auto" -> ZoomMode.AUTO
        "page-fit" -> ZoomMode.PAGE_FIT
        "page-width" -> ZoomMode.PAGE_WIDTH
        "page-height" -> ZoomMode.PAGE_HEIGHT
        "custom" -> ZoomMode.CUSTOM
        else -> throw MappingException()
      },
      value = viewerApp.pdfViewer.currentScale.toDouble() * 100,
      leftOffset = viewerApp.pdfViewer._location.left,
      topOffset = viewerApp.pdfViewer._location.top
    )
}
