package com.firsttimeinforever.intellij.pdf.viewer.application.tex

import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.ViewerAdapter
import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.types.Math
import com.firsttimeinforever.intellij.pdf.viewer.mpi.BrowserMessages
import com.firsttimeinforever.intellij.pdf.viewer.mpi.IdeMessages
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipe
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipeSupport.send
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipeSupport.subscribe
import com.firsttimeinforever.intellij.pdf.viewer.mpi.tex.SynctexPreciseLocation
import com.firsttimeinforever.intellij.pdf.viewer.mpi.tex.SynctexViewCoordinates
import kotlinx.browser.window
import org.w3c.dom.*
import org.w3c.dom.events.MouseEvent
import kotlin.js.json

/**
 * This is mostly a direct port of original @slideclimb code from TS to Kotlin.
 */
class SynctexSearchController(private val pipe: MessagePipe, private val viewer: ViewerAdapter) {
  // Array of all current drawing canvasses, so we can remove them when drawing something new.
  private val drawingCanvases = mutableListOf<HTMLElement>()

  private var isSynctexAvailable: Boolean = false
  private var forwardSearchData: SynctexPreciseLocation? = null

  private val viewerDocument: Document
    get() = viewer.viewerApp.asDynamic().appConfig.appContainer.ownerDocument as Document

  init {
    viewerDocument.addEventListener("click", {
      resetCanvas()
    })
    viewerDocument.addEventListener("click", {
      check(it is MouseEvent)
      actualListener(it)
    })
    pipe.subscribe<IdeMessages.SynctexAvailability> {
      isSynctexAvailable = it.isAvailable
    }
    pipe.subscribe<IdeMessages.SynctexForwardSearch> {
      // If the data is undefined, there is nothing to forward search. This can happen e.g. when
      // starting up the application, or when opening a document without forward searching to it.
      // if (it.location == undefined) return
      forwardSearchData = it.location
      console.log("Forward search to page ${it.location.page}")
      executeForwardSearch()
    }
  }

  fun finishInitialization() {
    if (forwardSearchData == null && isSynctexAvailable) {
      pipe.send(BrowserMessages.AskForwardSearchData())
    } else {
      executeForwardSearch()
    }
  }

  private fun calculateResolution(): Float {
    // Get PDF view resolution, assuming that currentScale is relative to a
    // fixed browser resolution of 96 dpi, and that synctex uses the big point (1/72th of an inch)
    return 72 / (viewer.viewerApp.pdfViewer.currentScale.toFloat() * 96)
  }

  private fun actualListener(event: MouseEvent) {
    // Ctrl + click (or Meta + click) -> Inverse search with SyncTeX
    if (!((event.ctrlKey && !isMacos()) || (event.metaKey && isMacos()))) {
      return
    }
    if (!isSynctexAvailable) {
      return
    }
    val scroll = viewer.viewerApp.pdfViewer.asDynamic().scroll
    val x = event.pageX + (scroll.lastX as Float)
    val y = event.pageY + (scroll.lastY as Float)

    // Get the page number
    val pageSizes = getPageCoordinates()
    var pageNumber = pageSizes.reversed().indexOfFirst { (left, top) -> top < y && left < x }
    pageNumber = when (pageNumber) {
      -1 -> pageSizes.size
      else -> pageSizes.size - pageNumber
    }
    val (pageLeft, pageTop) = pageSizes[pageNumber - 1]
    val res = calculateResolution()
    // Send a message to IntelliJ to sync to the tex file
    val viewCoordinates = SynctexViewCoordinates(
      pageNumber,
      // Transform the clicked (x, y) coordinate to a (x, y) coordinate on this page
      x = Math.round(res * (x - pageLeft)) as Int,
      y = Math.round(res * (y - pageTop)) as Int
    )
    pipe.send(BrowserMessages.SynctexSyncEditor(viewCoordinates))
  }

  /**
   * Reset the drawing canvases, i.e., remove all the canvases from the document and set the list of drawing canvases
   * to be the empty list.
   */
  private fun resetCanvas() {
    drawingCanvases.forEach { it.remove() }
    drawingCanvases.clear()
  }

  /**
   * Get the coordinates of the top-left corner of each page.
   */
  private fun getPageCoordinates(): List<Pair<Int, Int>> {
    val actualViewer = viewer.viewerApp.pdfViewer
    return actualViewer._pages.map { it.div.offsetLeft to it.div.offsetTop }
  }

  private fun createDrawingCanvas(document: Document, width: Int, height: Int): HTMLCanvasElement {
    val element = document.createElement("canvas") as HTMLCanvasElement
    element.width = width
    element.height = height
    return element.apply {
      style.position = "absolute"
      style.top = "0px"
      style.left = "0px"
    }
  }

  private fun createSelectionRectangle(): DOMRect {
    return forwardSearchData!!.let {
      val res = calculateResolution()
      DOMRect(
        x = (it.x / res),
        y = (it.y / res - it.height / res),
        width = (it.width / res),
        height = (it.height / res)
      )
    }
  }

  /**
   * Draw a box around the forward search result that is stored in this.forwardSearchData.
   *
   * We do NOT explicitly set the page data because then we cannot scroll to the rectangle if the page just changed.
   * However, we first scroll to the page manually to make sure that the page has been loaded before we request the
   * canvas to draw on.
   */
  private fun executeForwardSearch() {
    resetCanvas()
    val searchData = forwardSearchData ?: return
    // Scroll to the page before requesting the canvas, to ensure that the page has been loaded.
    val pages = getPages(viewerDocument)
    val page = pages[searchData.page - 1] as? HTMLElement
    checkNotNull(page)
    page.scrollIntoView()
    val canvas = page.querySelector("canvas") as HTMLCanvasElement
    // Create a new canvas to draw on, on top of the already existing canvas.
    val drawingCanvas = createDrawingCanvas(viewerDocument, canvas.width, canvas.height)
    canvas.parentElement!!.appendChild(drawingCanvas)
    // Add this new canvas to the list of drawing canvases, so we can easily delete it later.
    drawingCanvases.add(drawingCanvas)

    val rectangle = createSelectionRectangle()
    val context = drawingCanvas.getContext("2d") as CanvasRenderingContext2D
    with(context) {
      strokeStyle = "red"
      strokeRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height)
    }

    // Create a dummy element so we can scroll to the rectangle we have just drawn.
    scrollIntoViewByDummyElement(canvas.parentElement!!, rectangle.x,  rectangle.y)
  }

  private fun scrollIntoViewByDummyElement(targetElement: Element, left: Number, top: Number) {
    val dummyElement = viewerDocument.createElement("scrollDummy") as HTMLElement
    with(dummyElement) {
      style.position = "absolute"
      style.left = "${left}px"
      style.top = "${top}px"
    }
    targetElement.appendChild(dummyElement)
    // Center the rectangle/forward search result in the pdf view.
    dummyElement.scrollIntoView(json(
      "block" to "center",
      "inline" to "center"
    ))
    targetElement.removeChild(dummyElement)
  }

  /**
   * Draw a box around the forward search result that is stored in this.forwardSearchData.
   *
   * We do NOT explicitly set the page data because then we cannot scroll to the rectangle if the page just changed.
   * However, we first scroll to the page manually to make sure that the page has been loaded before we request the
   * canvas to draw on.
   * @private
   */
  // private executeForwardSearch(document: Document) {
  //   this.resetCanvas();
  //   const res = 72 / (this.delayedScaleValue * 96);
  //
  //   // Scroll to the page before requesting the canvas, to ensure that the page has been loaded.
  //   const pages = Array.from(AppComponent.getPages(document)).map(p => p as HTMLElement);
  //   const page = pages[this.forwardSearchData.page - 1];
  //   page.scrollIntoView();
  //   const canvas = page.querySelector("canvas") as HTMLCanvasElement;
  //
  //   // Create a new canvas to draw on, on top of the already existing canvas.
  //   const drawingCanvas = document.createElement("canvas");
  //   drawingCanvas.height = canvas.height;
  //   drawingCanvas.width = canvas.width;
  //   drawingCanvas.style.position = "absolute";
  //   drawingCanvas.style.top = "0px";
  //   drawingCanvas.style.left = "0px";
  //   canvas.parentElement.appendChild(drawingCanvas);
  //
  //   // Add this new canvas to the list of drawing canvases, so we can easily delete it later.
  //   this.drawingCanvases = this.drawingCanvases.concat(drawingCanvas);
  //
  //   const rectangle = {
  //     x: this.forwardSearchData.x / res,
  //     y: this.forwardSearchData.y / res - this.forwardSearchData.height / res,
  //     width: this.forwardSearchData.width / res,
  //     height: this.forwardSearchData.height / res
  //   };
  //
  //   const context = drawingCanvas.getContext("2d");
  //   context.strokeStyle = "red";
  //   context.strokeRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
  //
  //   // Create a dummy element so we can scroll to the rectangle we have just drawn.
  //   const scrollDummy = document.createElement("scrollDummy");
  //   scrollDummy.style.position = "absolute";
  //   scrollDummy.style.left = `${rectangle.x}px`;
  //   scrollDummy.style.top = `${rectangle.y}px`;
  //   canvas.parentElement.appendChild(scrollDummy);
  //   // Center the rectangle/forward search result in the pdf view.
  //   scrollDummy.scrollIntoView({block: "center", inline: "center"});
  //   canvas.parentElement.removeChild(scrollDummy);
  // }


  companion object {
    private fun getPages(document: Document): NodeList {
      return document.querySelectorAll(".page")
    }

    private fun isMacos(): Boolean {
      return window.navigator.platform.toLowerCase().contains("mac")
    }
  }
}
