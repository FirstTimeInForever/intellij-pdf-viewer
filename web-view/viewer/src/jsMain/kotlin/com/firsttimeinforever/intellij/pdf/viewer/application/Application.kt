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
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import kotlin.js.Promise
import kotlin.js.json
import kotlin.math.max
import kotlin.math.min

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
    pipe.subscribe<IdeMessages.ScrollDown> {
      viewer.scrollDown(it.pixels)
    }
    pipe.subscribe<IdeMessages.ScrollUp> {
      viewer.scrollUp(it.pixels)
    }
    pipe.subscribe<IdeMessages.UpdateThemeColors> {
      console.log("Received theme update request $it")
      ThemeUtils.updateColors(it.theme)
    }
    pipe.subscribe<IdeMessages.NavigateTo> {
      viewer.viewerApp.pdfLinkService.goToDestination(JSON.parse(it.destination.toString()))
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
    pipe.subscribe<IdeMessages.SetZoomMode> {
      viewer.viewerApp.pdfViewer.currentScaleValue = it.zoomMode
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
    // -----------------------------------------------------------------------
    // Core fix for "Scrolling on mac has high delay" (GH #51, #155).
    //
    // pdf.js 5.x's bindWindowEvents() registers a `{ passive: false }` wheel
    // listener (onWheel) for ctrl/cmd+wheel zoom and pinch-to-zoom. Because
    // the listener is non-passive, the browser CANNOT scroll on the compositor
    // thread: it must wait for the main-thread JS handler to finish and
    // confirm it will not call preventDefault(). On macOS trackpads this
    // introduces severe latency because the OS emits wheel events at 60-120Hz
    // and every single one is forced through the slow main-thread round trip.
    //
    // The original fix for #51 called the old `unbindWindowEvents()` +
    // manually re-registered only the needed listeners. That broke when
    // pdf.js removed the `_unboundEvents` API (v4.2.67+). In pdf.js 5.x the
    // same `unbindWindowEvents()` still exists but is now backed by an
    // AbortController — calling it aborts all listeners registered through
    // that signal in one shot.
    //
    // Strategy:
    //   1. Call PDFViewerApplication.unbindWindowEvents() to remove ALL
    //      window-level listeners pdf.js registered (wheel, keydown, keyup,
    //      click, resize, hashchange, beforeprint, afterprint, scroll, ...).
    //   2. Re-register the non-wheel listeners we still need. They are
    //      registered without `passive: false` so the browser is free to
    //      process input on the compositor thread.
    //   3. Do NOT re-register onWheel. Our own capture-phase wheel handler
    //      (installWheelDiagnostics) handles zoom for modifier-wheel and
    //      pinch events; plain scroll is left entirely to the browser's
    //      native scroll machinery.
    // -----------------------------------------------------------------------

    val app = viewer.viewerApp.asDynamic()
    val eventBus = viewer.viewerApp.eventBus

    val unbindFn = app.unbindWindowEvents
    val hasUnbind = unbindFn != undefined && unbindFn != null
    diag("[pdf-diag] rebindStandardEvents: unbindWindowEvents exists=$hasUnbind")
    try {
      app.unbindWindowEvents()
      diag("[pdf-diag] rebindStandardEvents: unbindWindowEvents() called OK")
    } catch (e: Throwable) {
      diag("[pdf-diag] rebindStandardEvents: unbindWindowEvents() FAILED: ${e.message}")
    }
    flushDiag()

    // Re-register the listeners pdf.js needs (minus wheel).
    // These are lightweight dispatchers that forward DOM events to pdf.js's
    // internal EventBus; none of them need to be non-passive.
    window.addEventListener("resize", {
      eventBus.dispatch("resize", json("source" to window))
    })
    window.addEventListener("hashchange", {
      eventBus.dispatch("hashchange", json(
        "source" to window,
        "hash" to document.location?.hash?.substring(1)
      ))
    })
    window.addEventListener("beforeprint", {
      eventBus.dispatch("beforeprint", json("source" to window))
    })
    window.addEventListener("afterprint", {
      eventBus.dispatch("afterprint", json("source" to window))
    })

    installWheelDiagnostics()
    installGestureDiagnostics()
    installScrollDiagnostics()
    installPageRenderDiagnostics()
    installLongTaskDiagnostics()
    window.setInterval({ flushDiag() }, 500)

    window.addEventListener("keydown") { event: KeyboardEvent ->
      console.log(event)
      if (event.altKey && event.ctrlKey && event.key.lowercase() == "p") {
        viewer.viewerApp.requestPresentationMode()
      }

      if (event.key.lowercase() == "arrowright" && !event.altKey && !event.ctrlKey && !event.shiftKey) {
        if (viewer.zoomState.value < 100) {
          viewer.goToNextPage()
        }
      }

      if (event.key.lowercase() == "arrowleft" && !event.altKey && !event.ctrlKey && !event.shiftKey) {
        if (viewer.zoomState.value < 100) {
          viewer.goToPreviousPage()
        }
      }

      if ((event.key.lowercase() == "=" || event.key.lowercase() == "+") && event.ctrlKey && !event.altKey && !event.shiftKey) {
        viewer.increaseScale()
      }

      if (event.key.lowercase() == "-" && event.ctrlKey && !event.altKey && !event.shiftKey) {
        viewer.decreaseScale()
      }

      if (event.key.lowercase() == "0" && event.ctrlKey && !event.altKey && !event.shiftKey) {
        viewer.viewerApp.pdfViewer.currentScaleValue = "auto"
      }

      if (event.key == "j" && !event.altKey && !event.ctrlKey && !event.shiftKey) {
        viewer.scrollDown()
      }

      if (event.key == "k" && !event.altKey && !event.ctrlKey && !event.shiftKey) {
        viewer.scrollUp()
      }
    }
  }

  // ---------------------------------------------------------------------------
  // Wheel / scroll / gesture diagnostics
  //
  // Instrumentation for https://github.com/FirstTimeInForever/intellij-pdf-viewer
  // issue "Scrolling on mac has high delay".
  //
  // Logs do NOT go through `console.log`: the JBCefBrowser console-message
  // forwarder is only installed when Registry flag `pdf.viewer.debug` is on,
  // which means plain users would never see anything. Instead, we batch log
  // lines in a JS-side ring buffer and flush them over the existing
  // MessagePipe as BrowserMessages.DiagnosticLog, where the host-side
  // com.intellij.openapi.diagnostic.Logger writes them into idea.log via the
  // official JetBrains logging API.
  //
  // Batching is intentional: per-event IPC (mojo + JSON round trip) would add
  // ~sub-ms to every wheel handler invocation, which could become the main-
  // thread backlog we are trying to measure. We buffer up to [flushEvery]
  // entries and flush in one go; the flush itself happens on the same main
  // thread but only once every N events. All log lines are prefixed with
  // `[pdf-diag]` so they are trivial to grep.
  //
  // Each wheel log records:
  //   #N             monotonic event counter
  //   t              performance.now() when our capture handler started (ms)
  //   evT            event.timeStamp as reported by Chromium (ms, same origin)
  //   lag            t - evT; how long Chromium waited before dispatching the
  //                  event to us. If this keeps growing, the main thread is
  //                  backlogged and scroll input queues up. This is the single
  //                  most important number for the macOS trackpad lag issue.
  //   gap            t - lastT; time between successive wheel dispatches. On a
  //                  healthy macOS trackpad the OS emits events at ~60-120Hz,
  //                  so gap should be ~8-16ms. Large spikes indicate the main
  //                  thread is busy.
  //   hdl            handler execution time (capture-phase). Should be sub-ms.
  //   flags          ctrl/meta/alt/shift/ispinch/delta* for classification.
  //   decision       what we did with the event.
  // ---------------------------------------------------------------------------

  private var wheelCounter: Int = 0
  private var lastWheelT: Double = 0.0
  private var wheelMaxLag: Double = 0.0
  private var wheelMaxGap: Double = 0.0
  private var wheelMaxHandler: Double = 0.0

  private val diagBuffer: MutableList<String> = mutableListOf()
  private val flushEvery: Int = 60

  private fun fmt(v: Double): String = v.asDynamic().toFixed(2) as String

  /**
   * Append one diagnostic line and flush if the buffer reached [flushEvery].
   * This is the ONLY call site that touches the IPC pipe from the diagnostic
   * handlers so the per-event cost stays at "append to an array" ~O(1).
   */
  private fun diag(line: String) {
    diagBuffer.add(line)
    if (diagBuffer.size >= flushEvery) {
      flushDiag()
    }
  }

  private fun flushDiag() {
    if (diagBuffer.isEmpty()) return
    val snapshot = diagBuffer.toList()
    diagBuffer.clear()
    pipe.send(BrowserMessages.DiagnosticLog(level = "INFO", lines = snapshot))
  }

  private fun installWheelDiagnostics() {
    // CRITICAL: This listener MUST be passive: true.
    //
    // When a non-passive wheel listener exists, Chromium's compositor thread
    // cannot scroll until the main-thread JS handler completes AND returns
    // (to know whether preventDefault() was called). In JCEF this round-trip
    // goes through Swing → Chromium IPC → renderer → JS → back, adding
    // significant latency to EVERY wheel event — even when the handler takes
    // 0ms. The log showed hdl=0ms but scroll gaps of 500-2700ms, proving
    // the bottleneck was this round-trip overhead, not our handler code.
    //
    // With passive: true, the compositor scrolls immediately without waiting,
    // and our handler runs asynchronously for logging and zoom side-effects.
    // We cannot call preventDefault() from a passive listener, but in JCEF
    // there is no browser-chrome zoom for ctrl+wheel, so we don't need to.
    window.addEventListener(
      "wheel",
      options = json("capture" to true, "passive" to true)
    ) { event: MouseEvent ->
      val t = window.performance.now()
      val evT = event.asDynamic().timeStamp.unsafeCast<Double>()
      val lag = t - evT
      val gap = if (lastWheelT > 0.0) t - lastWheelT else 0.0
      lastWheelT = t
      wheelCounter += 1

      val dyn = event.asDynamic()
      val dX = dyn.deltaX.unsafeCast<Double>()
      val dY = dyn.deltaY.unsafeCast<Double>()
      val dZ = dyn.deltaZ.unsafeCast<Double>()
      val dMode = dyn.deltaMode.unsafeCast<Int>()
      val ctrl = event.ctrlKey
      val meta = event.metaKey
      val alt = event.altKey
      val shift = event.shiftKey

      val maybePinch = ctrl && dMode == 0 && dX == 0.0 && dZ == 0.0
      val isInPresentationMode = viewer.viewerApp.pdfViewer.asDynamic().isInPresentationMode == true
      val isModifier = ctrl || meta || alt

      val decision: String = when {
        isInPresentationMode -> "PASS-PRESENTATION"
        isModifier -> {
          // Fire zoom as a side-effect. No preventDefault() needed in JCEF.
          val step = max(-3.0, min(3.0, dY))
          if (step < 0.0) viewer.increaseScale()
          else if (step > 0.0) viewer.decreaseScale()
          if (maybePinch) "ZOOM-PINCH(dY=$dY)" else "ZOOM-KEY(dY=$dY)"
        }
        else -> {
          "PASSIVE-SCROLL"
        }
      }

      val exit = window.performance.now()
      val hdl = exit - t
      if (lag > wheelMaxLag) wheelMaxLag = lag
      if (gap > wheelMaxGap) wheelMaxGap = gap
      if (hdl > wheelMaxHandler) wheelMaxHandler = hdl

      diag(
        "[pdf-diag] wheel #${wheelCounter} t=${fmt(t)} evT=${fmt(evT)} " +
          "lag=${fmt(lag)}ms gap=${fmt(gap)}ms hdl=${fmt(hdl)}ms " +
          "ctrl=$ctrl meta=$meta alt=$alt shift=$shift pinch=$maybePinch " +
          "dX=$dX dY=$dY dZ=$dZ dMode=$dMode -> $decision"
      )

      if (wheelCounter % flushEvery == 0) {
        diag(
          "[pdf-diag] wheel-summary count=$wheelCounter " +
            "maxLag=${fmt(wheelMaxLag)}ms maxGap=${fmt(wheelMaxGap)}ms " +
            "maxHdl=${fmt(wheelMaxHandler)}ms"
        )
      }
    }
  }

  private fun installGestureDiagnostics() {
    // macOS Safari/WebKit emits legacy `gesture*` events for trackpad pinch.
    // Chromium/JCEF generally emits wheel+ctrlKey instead, but some JBR builds
    // on macOS forward the native gesture recognizer. Log both to confirm
    // which path actually fires during a pinch attempt.
    val probe: (String) -> Unit = { type ->
      window.addEventListener(type) { event: Event ->
        val dyn = event.asDynamic()
        val scale = dyn.scale
        val rotation = dyn.rotation
        diag(
          "[pdf-diag] gesture=$type t=${fmt(window.performance.now())} " +
            "scale=$scale rotation=$rotation"
        )
        // Gestures are rare enough (one per pinch) that flushing immediately
        // keeps the log in-order relative to the surrounding wheel stream.
        flushDiag()
      }
    }
    probe("gesturestart")
    probe("gesturechange")
    probe("gestureend")
  }

  private var scrollCounter: Int = 0
  private var lastScrollT: Double = 0.0

  private fun installScrollDiagnostics() {
    // Scroll listener on the main container to cross-correlate browser-side
    // scroll timing with the wheel-event stream. This is registered as
    // `passive: true` so we never gate actual scrolling.
    val container = viewer.viewerApp.pdfViewer.asDynamic().container
    if (container == undefined || container == null) return
    container.addEventListener("scroll", { _: Event ->
      val t = window.performance.now()
      val gap = if (lastScrollT > 0.0) t - lastScrollT else 0.0
      lastScrollT = t
      scrollCounter += 1
      if (scrollCounter % 30 == 0 || gap > 50.0) {
        diag(
          "[pdf-diag] scroll #$scrollCounter t=${fmt(t)} gap=${fmt(gap)}ms " +
            "scrollTop=${container.scrollTop}"
        )
      }
    }, json("capture" to false, "passive" to true))
  }

  private fun installPageRenderDiagnostics() {
    // pdf.js fires "pagerendered" on the eventBus whenever a page canvas
    // finishes painting. This is the heavy synchronous operation that most
    // likely causes the scroll gaps (500-2700ms pauses in the scroll stream
    // while wheel events keep arriving at 16ms). By logging start/end times
    // of each page render we can confirm the correlation.
    viewer.viewerApp.eventBus.on("pagerendered") { event: dynamic ->
      val t = window.performance.now()
      val pageNum = event.pageNumber
      val cssUnits = event.cssUnits
      val timestamp = event.timestamp
      diag(
        "[pdf-diag] pagerendered page=$pageNum t=${fmt(t)} " +
          "renderTime=${timestamp}ms cssUnits=$cssUnits"
      )
      flushDiag()
    }
    viewer.viewerApp.eventBus.on("pagerender") { event: dynamic ->
      val t = window.performance.now()
      val pageNum = event.pageNumber
      diag("[pdf-diag] pagerender-start page=$pageNum t=${fmt(t)}")
    }
  }

  private fun installLongTaskDiagnostics() {
    // Use PerformanceObserver to detect long tasks (>50ms) on the main thread.
    // These are the most likely cause of scroll stuttering. Available in
    // Chromium 58+ (JCEF always qualifies).
    js("""
      (function(diagFn) {
        if (typeof PerformanceObserver === 'undefined') return;
        try {
          var obs = new PerformanceObserver(function(list) {
            var entries = list.getEntries();
            for (var i = 0; i < entries.length; i++) {
              var e = entries[i];
              diagFn('[pdf-diag] long-task duration=' + e.duration.toFixed(2) +
                     'ms startTime=' + e.startTime.toFixed(2) +
                     'ms name=' + e.name);
            }
          });
          obs.observe({ type: 'long-animation-frame', buffered: true });
        } catch(e1) {
          try {
            var obs2 = new PerformanceObserver(function(list) {
              var entries = list.getEntries();
              for (var i = 0; i < entries.length; i++) {
                var e = entries[i];
                diagFn('[pdf-diag] long-task duration=' + e.duration.toFixed(2) +
                       'ms startTime=' + e.startTime.toFixed(2) +
                       'ms name=' + e.name);
              }
            });
            obs2.observe({ type: 'longtask', buffered: true });
          } catch(e2) {}
        }
      })
    """)(::diag)
  }

  fun run() {
    viewer.viewerApp.initializedPromise.then {
      rebindStandardEvents()
      collectViewProperties().then {
        pipe.send(BrowserMessages.InitialViewProperties(it))
        notifyViewStateChanged(ViewStateChangeReason.INITIAL)
      }
      pipe.send(BrowserMessages.AskForwardSearchData())
      with(viewer) {
        addEventListener(ViewerEvents.PAGE_CHANGING, ::pageChangeListener)
        addEventListener(ViewerEvents.ZOOM_IN, ::zoomChangeListener)
        addEventListener(ViewerEvents.ZOOM_OUT, ::zoomChangeListener)
        addEventListener(ViewerEvents.ZOOM_RESET, ::zoomChangeListener)
      }
    }
  }
}
