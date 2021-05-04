package com.firsttimeinforever.intellij.pdf.viewer.application

import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.Internals
import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.ThemeUtils
import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.ViewerAdapter
import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.ViewerEvents
import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.types.Object
import com.firsttimeinforever.intellij.pdf.viewer.mpi.BrowserMessages
import com.firsttimeinforever.intellij.pdf.viewer.mpi.IdeMessages
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipeSupport.send
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipeSupport.subscribe
import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.*
import kotlinx.browser.window
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromDynamic
import org.w3c.dom.Document
import org.w3c.dom.HTMLElement
import kotlin.js.Promise
import kotlin.js.json

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@ExperimentalSerializationApi
@OptIn(ExperimentalStdlibApi::class)
class Application(private val viewer: ViewerAdapter) {
  private val pipe = BrowserMessagePipe()
  private val sidebarController = SidebarController(viewer)

  val documentInfo by lazy { collectDocumentInfo() }

  // FIXME: This is incorrect
  val fileName by lazy { viewer.viewerApp.baseUrl.split('/').last() }

  init {
    pipe.subscribe<IdeMessages.SidebarViewModeSetRequest> {
      sidebarController.switchViewMode(it.mode)
      notifyViewStateChanged(ViewStateChangeReason.SIDEBAR_VIEW_MODE)
    }
    pipe.subscribe<IdeMessages.GotoPage> {
      console.log(it)
      when (it.direction) {
        PageGotoDirection.FORWARD -> viewer.currentPageNumber += 1
        PageGotoDirection.BACKWARD -> viewer.currentPageNumber -= 1
      }
    }
    pipe.subscribe<IdeMessages.PageSpreadStateSetRequest> {
      console.log(it)
      if (it.state != viewer.pageSpreadState) {
        viewer.pageSpreadState = it.state
        notifyViewStateChanged(ViewStateChangeReason.PAGE_SPREAD_STATE)
      }
    }
    pipe.subscribe<IdeMessages.DocumentInfoRequest> {
      console.log("Sending document info: $documentInfo")
      pipe.send(BrowserMessages.DocumentInfoResponse(documentInfo))
    }
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
    pipe.subscribe<IdeMessages.Search> {
      console.log("Executing search query: $it")
      when (it.direction) {
        SearchDirection.FORWARD -> viewer.findNext(it.text)
        SearchDirection.BACKWARD -> viewer.findPrevious(it.text)
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
      updateTheme(it.theme)
    }
    ensureDocumentPropertiesReady()
  }

  private fun updateTheme(viewTheme: ViewTheme) {
    val appConfig = viewer.viewerApp.asDynamic().appConfig
    // appConfig.appContainer.style.background = viewTheme.background
    val document = appConfig.appContainer.ownerDocument as Document
    ThemeUtils.updateColors(document, viewTheme)
    // ThemeUtils.attachStylesheet(document, ThemeUtils.generateStylesheet(viewTheme))
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
      console.error(error)
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

  private fun notifyViewStateChanged(reason: ViewStateChangeReason = ViewStateChangeReason.UNSPECIFIED) {
    pipe.send(BrowserMessages.ViewStateChanged(collectViewState(), reason))
  }

  fun run() {
    collectViewProperties().then {
      pipe.send(BrowserMessages.InitialViewProperties(it))
    }
    notifyViewStateChanged(ViewStateChangeReason.INITIAL)
    viewer.addEventListener(ViewerEvents.PAGE_CHANGING) {
      notifyViewStateChanged(ViewStateChangeReason.PAGE_NUMBER)
    }
    viewer.addEventListener(ViewerEvents.ZOOM_IN, ::zoomChangeListener)
    viewer.addEventListener(ViewerEvents.ZOOM_OUT, ::zoomChangeListener)
    viewer.addEventListener(ViewerEvents.ZOOM_RESET, ::zoomChangeListener)
    // viewer.viewerApp.initializedPromise.then { start() }
  }
}

