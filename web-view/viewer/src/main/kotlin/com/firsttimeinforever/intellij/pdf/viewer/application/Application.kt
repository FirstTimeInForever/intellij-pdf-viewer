package com.firsttimeinforever.intellij.pdf.viewer.application

import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.Internals
import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.ViewerAdapter
import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.ViewerEvents
import com.firsttimeinforever.intellij.pdf.viewer.mpi.BrowserMessages
import com.firsttimeinforever.intellij.pdf.viewer.mpi.IdeMessages
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipeSupport.send
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipeSupport.subscribe
import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.*
import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import kotlin.js.Promise

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalStdlibApi::class)
class Application(private val viewer: ViewerAdapter) {
  private val pipe = BrowserMessagePipe()
  private val sidebarController = SidebarController(viewer)

  init {
    pipe.subscribe<IdeMessages.SidebarViewModeSetRequest> {
      sidebarController.switchViewMode(it.mode)
      pipe.send(
        BrowserMessages.ViewStateChanged(
          collectViewState(),
          ViewStateChangeReason.SIDEBAR_VIEW_MODE
        )
      )
    }
    pipe.subscribe<IdeMessages.LafChanged> {
      console.log(it)
      updateColors(it.background, it.foreground)
    }
    pipe.subscribe<IdeMessages.GotoPage> {
      console.log(it)
      when (it.direction) {
        PageGotoDirection.FORWARD -> viewer.currentPageNumber += 1
        PageGotoDirection.BACKWARD -> viewer.currentPageNumber -= 1
      }
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
      viewer.pageSpreadState
    )
  }

  @Suppress("UNUSED_PARAMETER")
  private fun zoomChangeListener(event: dynamic) {
    pipe.send(
      BrowserMessages.ViewStateChanged(
        collectViewState(),
        ViewStateChangeReason.ZOOM
      )
    )
  }

  @Suppress("CAST_NEVER_SUCCEEDS")
  private fun updateColors(background: String, foreground: String) {
    val root = window.document.documentElement as HTMLElement
    with(root.style) {
      setProperty(Internals.StyleVariables.bodyBackgroundColor, background)
      setProperty(Internals.StyleVariables.sidebarBackgroundColor, background)
      setProperty(Internals.StyleVariables.mainColor, foreground)
    }
  }

  fun run() {
    collectViewProperties().then {
      pipe.send(BrowserMessages.InitialViewProperties(it))
    }
    pipe.send(
      BrowserMessages.ViewStateChanged(
        collectViewState(),
        ViewStateChangeReason.INITIAL
      )
    )
    viewer.addEventListener(ViewerEvents.PAGE_CHANGING) {
      pipe.send(
        BrowserMessages.ViewStateChanged(
          collectViewState(),
          ViewStateChangeReason.PAGE_NUMBER
        )
      )
    }
    viewer.addEventListener(ViewerEvents.ZOOM_IN, ::zoomChangeListener)
    viewer.addEventListener(ViewerEvents.ZOOM_OUT, ::zoomChangeListener)
    viewer.addEventListener(ViewerEvents.ZOOM_RESET, ::zoomChangeListener)
    // viewer.viewerApp.initializedPromise.then { start() }
  }
}

