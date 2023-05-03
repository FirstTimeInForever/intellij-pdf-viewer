package com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs

import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.types.PdfViewerApplication
import com.firsttimeinforever.intellij.pdf.viewer.model.*
import kotlin.js.json

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
        else -> ZoomMode.CUSTOM
      },
      value = viewerApp.pdfViewer.currentScale.toDouble() * 100,
      leftOffset = viewerApp.pdfViewer._location.left,
      topOffset = viewerApp.pdfViewer._location.top
    )

  // Basically direct implementations from the old code

  fun increaseScale() {
    viewerApp.asDynamic().toolbar.items.zoomIn.click()
  }

  fun decreaseScale() {
    viewerApp.asDynamic().toolbar.items.zoomOut.click()
  }

  fun rotateClockwise() {
    viewerApp.asDynamic().appConfig.secondaryToolbar.pageRotateCwButton.click()
  }

  fun rotateCounterClockwise() {
    viewerApp.asDynamic().appConfig.secondaryToolbar.pageRotateCcwButton.click()
  }

  fun goToNextPage() {
    if (currentPageNumber < pagesCount) {
      currentPageNumber += 1
    }
  }

  fun goToPreviousPage() {
    if (currentPageNumber > 1) {
      currentPageNumber -= 1
    }
  }

  fun findNext(text: String) {
    val findBar = viewerApp.asDynamic().appConfig.findBar
    findBar.findField.value = text
    findBar.findNextButton.click()
  }

  fun findPrevious(text: String) {
    val findBar = viewerApp.asDynamic().appConfig.findBar
    findBar.findField.value = text
    findBar.findPreviousButton.click()
  }

  val currentScrollDirection: ScrollDirection
    get() = ScrollDirection.values()[viewerApp.pdfViewer.scrollMode]

  fun setVerticalScroll() {
    val toolbar = viewerApp.asDynamic().appConfig.secondaryToolbar
    toolbar.scrollVerticalButton.click()
  }

  fun setHorizontalScroll() {
    val toolbar = viewerApp.asDynamic().appConfig.secondaryToolbar
    toolbar.scrollHorizontalButton.click()
  }

  fun releaseSearchHighlighting() {
    viewerApp.eventBus.dispatch("findbarclose", undefined)
  }

  fun find(query: SearchQuery, direction: SearchDirection) {
    viewerApp.eventBus.dispatch("find", json(
      "source" to viewerApp.asDynamic().findBar,
      "type" to (if (query.again) "again" else undefined),
      "query" to query.text,
      "entireWord" to query.wholeWord,
      "caseSensitive" to query.caseSensitive,
      "findPrevious" to (direction == SearchDirection.BACKWARD),
      "highlightAll" to true
    ))
    // this.eventBus.dispatch("find", {
    //   source: this,
    //   type,
    //   query: this.findField.value,
    //   phraseSearch: true,
    //   caseSensitive: this.caseSensitive.checked,
    //   entireWord: this.entireWord.checked,
    //   highlightAll: this.highlightAll.checked,
    //   findPrevious: findPrev,
    // });
  }
}
