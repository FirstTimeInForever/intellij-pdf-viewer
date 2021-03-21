package com.firsttimeinforever.intellij.pdf.viewer.application

import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.ViewerAdapter
import com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.ViewerEvents
import com.firsttimeinforever.intellij.pdf.viewer.mpi.BrowserMessages
import com.firsttimeinforever.intellij.pdf.viewer.mpi.IdeMessages
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipeSupport.send
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipeSupport.subscribe
import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.ViewProperties
import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.ViewState
import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.ViewStateChangeReason
import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.ZoomMode
import kotlin.js.Promise

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalStdlibApi::class)
class Application(private val viewer: ViewerAdapter) {
    private val pipe = BrowserMessagePipe()
    private val sidebarController = SidebarController(viewer)

    init {
        pipe.subscribe<IdeMessages.SidebarViewModeSetRequest> {
            sidebarController.switchViewMode(it.mode)
            pipe.send(BrowserMessages.ViewStateChanged(
                collectViewState(),
                ViewStateChangeReason.SIDEBAR_VIEW_MODE
            ))
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
        pipe.send(BrowserMessages.ViewStateChanged(
            collectViewState(),
            ViewStateChangeReason.ZOOM
        ))
    }

    fun run() {
        collectViewProperties().then {
            pipe.send(BrowserMessages.InitialViewProperties(it))
        }
        pipe.send(BrowserMessages.ViewStateChanged(
            collectViewState(),
            ViewStateChangeReason.INITIAL
        ))
        viewer.addEventListener(ViewerEvents.PAGE_CHANGING) {
            pipe.send(BrowserMessages.ViewStateChanged(
                collectViewState(),
                ViewStateChangeReason.PAGE_NUMBER
            ))
        }
        viewer.addEventListener(ViewerEvents.ZOOM_IN, ::zoomChangeListener)
        viewer.addEventListener(ViewerEvents.ZOOM_OUT, ::zoomChangeListener)
        viewer.addEventListener(ViewerEvents.ZOOM_RESET, ::zoomChangeListener)
        // viewer.viewerApp.initializedPromise.then { start() }
    }
}

