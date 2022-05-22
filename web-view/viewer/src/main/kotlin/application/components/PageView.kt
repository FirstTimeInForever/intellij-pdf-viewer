package application.components

import application.components.base.FlexGrid
import application.components.layers.annotationLayer
import application.components.layers.canvasMainLayer
import application.components.layers.svgMainLayer
import application.components.layers.text.textLayer
import application.useStateWithEffect
import csstype.Position
import csstype.px
import emotion.react.css
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import pdfjs.lib.display.PdfDocumentProxy
import pdfjs.lib.display.PdfPageProxy
import pdfjs.web.PdfLinkService
import react.FC
import react.Props
import kotlin.js.Promise

enum class PageRendererType {
  CANVAS,
  SVG
}

external interface PageViewProps: Props {
  var document: PdfDocumentProxy
  var page: Promise<PdfPageProxy>
  var linkService: PdfLinkService
  var rendererType: PageRendererType
  var viewportProperties: PageViewportProperties
}

@OptIn(DelicateCoroutinesApi::class)
val pageView = FC<PageViewProps> { props ->
  val pageState by GlobalScope.useStateWithEffect(props.document, props.page, props.linkService) {
    props.page.await()
  }
  val page = pageState ?: return@FC
  FlexGrid.row {
    css {
      firstChild {
        marginTop = 6.px
      }
      marginBottom = 6.px
      position = Position.relative
    }
    when (props.rendererType) {
      PageRendererType.CANVAS -> canvasMainLayer {
        this.page = page
        this.viewportProperties = props.viewportProperties
      }
      PageRendererType.SVG -> svgMainLayer {
        this.page = page
        this.viewportProperties = props.viewportProperties
      }
    }
    textLayer {
      this.page = page
      this.viewportProperties = props.viewportProperties
    }
    annotationLayer {
      this.page = page
      this.viewportProperties = props.viewportProperties
      this.linkService = props.linkService
    }
  }
}
