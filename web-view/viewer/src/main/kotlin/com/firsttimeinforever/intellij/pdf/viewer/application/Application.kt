package com.firsttimeinforever.intellij.pdf.viewer.application

import com.firsttimeinforever.intellij.pdf.viewer.BrowserMessages
import com.firsttimeinforever.intellij.pdf.viewer.IdeMessages
import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.ThemeUtils
import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.ViewerAdapter
import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.ViewerEvents
import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.types.InternalOutline
import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.types.Object
import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.types.PdfFindControllerEvents
import com.firsttimeinforever.intellij.pdf.viewer.application.tex.SynctexSearchController
import com.firsttimeinforever.intellij.pdf.viewer.application.utility.CommonBrowserUtilities.addEventListener
import com.firsttimeinforever.intellij.pdf.viewer.model.*
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipeSupport.send
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipeSupport.subscribe
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromDynamic
import org.w3c.dom.Document
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import kotlin.js.Promise
import kotlin.js.json

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@ExperimentalSerializationApi
@OptIn(ExperimentalStdlibApi::class)
class Application(private val viewer: ViewerAdapter) {
  private val pipe = BrowserMessagePipe()
  private val sidebarController = SidebarController(viewer)

  private val synctexSearchController = SynctexSearchController(pipe, viewer)

  // @Suppress("MemberVisibilityCanBePrivate")
  // val documentInfo by lazy { collectDocumentInfo() }

  // FIXME: This is incorrect
  @Suppress("MemberVisibilityCanBePrivate")
  val fileName by lazy { viewer.viewerApp.baseUrl.split('/').last() }

  init {
    pipe.subscribe<IdeMessages.SidebarViewModeSetRequest> {
      sidebarController.switchViewMode(it.mode)
      notifyViewStateChanged(ViewStateChangeReason.SIDEBAR_VIEW_MODE)
    }
    pipe.subscribe<IdeMessages.GotoPage> {
      console.log(it)
      when (it.direction) {
        PageGotoDirection.FORWARD -> viewer.goToNextPage()
        PageGotoDirection.BACKWARD -> viewer.goToPreviousPage()
      }
    }
    pipe.subscribe<IdeMessages.PageSpreadStateSetRequest> {
      console.log(it)
      if (it.state != viewer.pageSpreadState) {
        viewer.pageSpreadState = it.state
        notifyViewStateChanged(ViewStateChangeReason.PAGE_SPREAD_STATE)
      }
    }
    // pipe.subscribe<IdeMessages.DocumentInfoRequest> {
    //   console.log("Sending document info: $documentInfo")
    //   pipe.send(BrowserMessages.DocumentInfoResponse(documentInfo))
    // }
    pipe.subscribe<IdeMessages.ChangeScaleStepped> {
      console.log("Executing change scale query: $it")
      when {
        it.increase -> viewer.increaseScale()
        else -> viewer.decreaseScale()
      }
      // Already handled in zoomChangeListener
      // notifyViewStateChanged(ViewStateChangeReason.ZOOM)
    }
    // TODO: Should rotation state be included into ViewState?
    pipe.subscribe<IdeMessages.RotatePages> {
      console.log("Executing rotate query: $it")
      when {
        it.clockwise -> viewer.rotateClockwise()
        else -> viewer.rotateCounterClockwise()
      }
    }
    pipe.subscribe<IdeMessages.SetScrollDirection> {
      when (it.direction) {
        ScrollDirection.VERTICAL -> viewer.setVerticalScroll()
        ScrollDirection.HORIZONTAL -> viewer.setHorizontalScroll()
      }
      notifyViewStateChanged(ViewStateChangeReason.SCROLL_DIRECTION)
    }
    pipe.subscribe<IdeMessages.UpdateThemeColors> {
      console.log("Received theme update request $it")
      ThemeUtils.updateColors(it.theme)
    }
    pipe.subscribe<IdeMessages.NavigateTo> {
      viewer.viewerApp.pdfLinkService.navigateTo(it.destination)
    }
    pipe.subscribe<IdeMessages.NavigateHistory> {
      when (it.direction) {
        HistoryNavigationDirection.FORWARD -> viewer.viewerApp.pdfHistory.forward()
        HistoryNavigationDirection.BACK -> viewer.viewerApp.pdfHistory.back()
      }
    }
    pipe.subscribe<IdeMessages.ExitPresentationMode> {
      // ignored promise
      document.exitFullscreen().catch {
        console.warn(it)
      }
    }
    // ensureDocumentPropertiesReady()
    sendOutline()
    synctexSearchController.finishInitialization()
    setupSearch()
    pipe.subscribe<IdeMessages.BeforeReload> {
      pipe.send(BrowserMessages.BeforeReloadViewState(collectViewState()))
    }
  }

  private fun sendSearchResult(currentMatch: Int, totalMatches: Int) {
    pipe.send(BrowserMessages.SearchResponse(SearchResult(currentMatch, totalMatches)))
  }

  private fun setupSearch() {
    with(viewer.viewerApp.eventBus) {
      on(PdfFindControllerEvents.UPDATE_FIND_MATCHES_COUNT) {
        sendSearchResult(it.matchesCount.current as Int, it.matchesCount.total as Int)
      }
      on(PdfFindControllerEvents.UPDATE_FIND_CONTROL_STATE) {
        sendSearchResult(it.matchesCount.current as Int, it.matchesCount.total as Int)
      }
    }
    pipe.subscribe<IdeMessages.Search> {
      val text = it.query.text
      if (text.isNotEmpty() && text.isNotBlank()) {
        console.log("Executing search query: $it")
        viewer.find(it.query, it.direction)
      }
    }
    pipe.subscribe<IdeMessages.ReleaseSearchHighlighting> {
      console.log("Releasing search highlighting")
      // Push only last search position onto history stack
      viewer.viewerApp.pdfHistory.pushCurrentPosition()
      viewer.releaseSearchHighlighting()
    }
  }

  private fun sendOutline() {
    viewer.viewerApp.pdfDocument.getOutline().then { outline ->
      if (outline != null) {
        val nodes = Json.decodeFromDynamic<Array<InternalOutline>>(outline)
        val root = PdfOutlineNode.createRootNode(nodes.map(::traverseOutline))
        pipe.send(BrowserMessages.DocumentOutline(root))
      }
    }
  }

  private fun traverseOutline(node: InternalOutline): PdfOutlineNode {
    return when {
      node.items.isNotEmpty() -> PdfOutlineNode(
        page = -2,
        name = node.title,
        children = node.items.map(::traverseOutline),
        navigationReference = node.dest
      )
      else -> PdfOutlineNode(
        page = -2,
        name = node.title,
        navigationReference = node.dest
      )
    }
  }

  // FIXME: Reimplement document info collection without open/close hack and
  //        with correct awaiting of _dataAvailableCapability promise
  private fun collectDocumentInfo(): DocumentInfo {
    // FIXME: Add types
    val result = json()
    val info = viewer.viewerApp.pdfDocumentProperties.fieldData
    check(info != undefined) { "fieldData was undefined" }
    Object.assign(result, info)
    info["fileName"] = fileName
    return Json.decodeFromDynamic(result)
  }

  private fun ensureDocumentPropertiesReady() {
    try {
      viewer.viewerApp.pdfDocumentProperties.pdfDocumentProperties.open()
      viewer.viewerApp.pdfDocumentProperties.pdfDocumentProperties.close()
    } catch (error: Throwable) {
      console.warn(error)
    }
  }

  private fun collectViewProperties(): Promise<ViewProperties> {
    return sidebarController.getAvailableViewModes().then {
      ViewProperties(viewer.pagesCount, it)
    }
  }

  private fun collectViewState(): ViewState {
    return ViewState(
      viewer.currentPageNumber,
      viewer.zoomState.copy(mode = ZoomMode.CUSTOM),
      sidebarController.currentViewMode,
      viewer.pageSpreadState,
      viewer.currentScrollDirection
    )
  }

  @Suppress("UNUSED_PARAMETER")
  private fun zoomChangeListener(event: dynamic) {
    notifyViewStateChanged(ViewStateChangeReason.ZOOM)
  }

  @Suppress("UNUSED_PARAMETER")
  private fun pageChangeListener(event: dynamic) {
    notifyViewStateChanged(ViewStateChangeReason.PAGE_NUMBER)
  }

  private fun notifyViewStateChanged(reason: ViewStateChangeReason = ViewStateChangeReason.UNSPECIFIED) {
    pipe.send(BrowserMessages.ViewStateChanged(collectViewState(), reason))
  }

  private fun rebindStandardEvents() {
    viewer.viewerApp.asDynamic().unbindWindowEvents()
    val boundEvents = viewer.viewerApp.asDynamic()._boundEvents
    boundEvents.windowResize = {
      viewer.viewerApp.eventBus.dispatch("resize", json("source" to window))
    }
    boundEvents.windowHashChange = {
      viewer.viewerApp.eventBus.dispatch("hashchange", json(
        "source" to window,
        "hash" to document.location?.hash?.substring(1)
      ))
    }
    boundEvents.windowBeforePrint = {
      viewer.viewerApp.eventBus.dispatch("beforeprint", json("source" to window))
    }
    boundEvents.windowAfterPrint = {
      viewer.viewerApp.eventBus.dispatch("afterprint", json("source" to window))
    }
    window.addEventListener("resize", boundEvents.windowResize.unsafeCast<EventListener>())
    window.addEventListener("hashchange", boundEvents.windowHashChange.unsafeCast<EventListener>())
    window.addEventListener("beforeprint", boundEvents.windowBeforePrint.unsafeCast<EventListener>())
    window.addEventListener("afterprint", boundEvents.windowAfterPrint.unsafeCast<EventListener>())
    window.addEventListener("wheel", options = json("passive" to false)) { event: MouseEvent ->
      if (event.altKey || event.ctrlKey || event.metaKey) {
        event.preventDefault()
      }
    }

    window.addEventListener("keydown") { event: KeyboardEvent ->
      console.log(event)
      if (event.altKey && event.ctrlKey && event.key.lowercase() == "p") {
        viewer.viewerApp.requestPresentationMode()
      }

      // Go to the next page.
      if (event.key.lowercase() == "arrowright" && !event.altKey && !event.ctrlKey && !event.shiftKey) {
        // Only move to the next page if the entire width of the page is in view.
        if (viewer.zoomState.value < 100) {
          viewer.goToNextPage()
        }
      }

      // Go to the previous page.
      if (event.key.lowercase() == "arrowleft" && !event.altKey && !event.ctrlKey && !event.shiftKey) {
        // Only move to the next page if the entire width of the page is in view.
        if (viewer.zoomState.value < 100) {
          viewer.goToPreviousPage()
        }
      }

      // Zoom in.
      if ((event.key.lowercase() == "=" || event.key.lowercase() == "+") && event.ctrlKey && !event.altKey && !event.shiftKey) {
        viewer.increaseScale()
      }

      // Zoom out.
      if (event.key.lowercase() == "-" && event.ctrlKey && !event.altKey && !event.shiftKey) {
        viewer.decreaseScale()
      }

      // Reset zoom.
      if (event.key.lowercase() == "0" && event.ctrlKey && !event.altKey && !event.shiftKey) {
        viewer.viewerApp.pdfViewer.currentScaleValue = "auto"
      }
    }
  }

  fun run() {
    viewer.viewerApp.initializedPromise.then {
      rebindStandardEvents()
      collectViewProperties().then {
        pipe.send(BrowserMessages.InitialViewProperties(it))
        notifyViewStateChanged(ViewStateChangeReason.INITIAL)
      }
      with(viewer) {
        addEventListener(ViewerEvents.PAGE_CHANGING, ::pageChangeListener)
        addEventListener(ViewerEvents.ZOOM_IN, ::zoomChangeListener)
        addEventListener(ViewerEvents.ZOOM_OUT, ::zoomChangeListener)
        addEventListener(ViewerEvents.ZOOM_RESET, ::zoomChangeListener)
      }
    }
  }
}
