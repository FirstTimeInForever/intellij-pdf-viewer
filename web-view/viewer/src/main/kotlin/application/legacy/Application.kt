package application.legacy

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.await
import pdfjs.lib.display.PdfDocumentProxy
import pdfjs.web.*
import pdfjs.web.event.utils.PdfApplicationEvents
import pdfjs.web.event.utils.PdfEventBus
import kotlin.js.json

class Application(private val options: ApplicationOptions): CoroutineScope {
  private val localization = GenericLocalization("en")

  private val eventBus = PdfEventBus()

  private val linkService = PdfLinkService(PdfLinkServiceOptions(eventBus))

  private val renderingQueue = PdfRenderingQueue()

  private val viewer = PdfViewer(PdfViewerOptions(
    container = options.viewerElement,
    eventBus,
    linkService,
    renderingQueue
  ))

  // private val thumbnailViewer = PdfThumbnailViewer(PdfThumbnailViewerOptions(
  //   container = options.thumbnailViewerElement,
  //   eventBus,
  //   linkService,
  //   renderingQueue,
  //   localization
  // ))

  init {
    linkComponents()
    eventBus.on(PdfApplicationEvents.PAGE_RENDERED, this::pageRendered)
  }

  private fun linkComponents() {
    viewer.setDocument(options.document)
    linkService.apply {
      setViewer(viewer)
      setDocument(options.document, null)
    }
    renderingQueue.apply {
      setViewer(viewer)
      // setThumbnailViewer(thumbnailViewer)
      asDynamic().isThumbnailViewEnabled = true
    }
    // thumbnailViewer.setDocument(options.document)
  }

  private fun pageRendered(page: dynamic) {
    val pageNumber = page.pageNumber as Int - 1
    val pageView = viewer.getPageView(pageNumber)
    // val thumbnailView = thumbnailViewer.getThumbnail(pageNumber)
    println("page rendered")
    console.log(pageView)
    // console.log(thumbnailView)
    // if (pageView != null && thumbnailView != null) {
    //   console.log(thumbnailView.renderingState)
    //   thumbnailView.setImage(pageView)
    // }
  }

  override val coroutineContext = Dispatchers.Main

  private suspend fun load(document: PdfDocumentProxy) {
    val downloadInfo = document.getDownloadInfo().await()
    val firstPagePromise = viewer.firstPagePromise.then<dynamic> {
      this.eventBus.dispatch(PdfApplicationEvents.DOCUMENT_LOADED, json("source" to this))
    }
    firstPagePromise.await()
    // initializePageLabels()
  }

  private suspend fun initializePageLabels() {
    println("Initializing page labels")
    val pageLabels = options.document.getPageLabels().await() ?: error("Failed fetching page labels")
    viewer.asDynamic().setPageLabels(pageLabels)
    // thumbnailViewer.setPageLabels(pageLabels)
    // thumbnailViewer.setDocument(options.document)
  }

  suspend fun start() {
    load(options.document)
  }

  fun stop() {

  }
}
