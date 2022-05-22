package application.components

import application.components.base.FlexGrid
import application.components.layers.canvasMainLayer
import application.useStateWithEffect
import csstype.px
import emotion.react.css
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import pdfjs.lib.display.PdfDocumentProxy
import pdfjs.lib.display.PdfPageProxy
import pdfjs.lib.display.pageNumbers
import pdfjs.web.PdfLinkService
import react.*
import kotlin.js.Promise

external interface ThumbnailViewerProps: Props {
  var document: PdfDocumentProxy
  var linkService: PdfLinkService
  var viewportProperties: PageViewportProperties
}

val thumbnailViewer = FC<ThumbnailViewerProps> { props ->
  val document = props.document
  for (pageNumber in document.pageNumbers) {
    val page = document.getPage(pageNumber)
    thumbnailView {
      this.page = page
      this.viewportProperties = props.viewportProperties
    }
  }
}

private external interface ThumbnailViewProps: Props {
  var page: Promise<PdfPageProxy>
  var viewportProperties: PageViewportProperties
}

@OptIn(DelicateCoroutinesApi::class)
private val thumbnailView = FC<ThumbnailViewProps> { props ->
  val pageState by GlobalScope.useStateWithEffect(null, props.page) {
    props.page.await()
  }
  val page = pageState ?: return@FC
  FlexGrid.row {
    css {
      firstChild {
        marginTop = 4.px
      }
      marginBottom = 4.px
    }
    canvasMainLayer {
      this.page = page
      this.viewportProperties = props.viewportProperties
    }
  }
}
