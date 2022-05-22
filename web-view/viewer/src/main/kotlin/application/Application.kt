package application

import application.ReactUtilities.appendChildren
import application.components.PageViewportProperties
import application.components.base.FlexGrid
import application.components.thumbnailViewer
import application.components.pageViewer
import csstype.*
import emotion.react.css
import pdfjs.lib.display.PdfDocumentProxy
import pdfjs.web.PdfLinkService
import pdfjs.web.PdfLinkServiceOptions
import pdfjs.web.event.utils.PdfEventBus
import react.*

external interface ApplicationProps: Props {
  var document: PdfDocumentProxy
}

private fun createLinkService(document: PdfDocumentProxy): PdfLinkService {
  return PdfLinkService(PdfLinkServiceOptions(PdfEventBus())).apply {
    setDocument(document, null)
  }
}

val application = FC<ApplicationProps> { props ->
  FlexGrid.row {
    normalizeStyles()
    css {
      height = 100.pct
    }
    val linkService by useState { createLinkService(props.document) }
    sidebar {
      thumbnailViewer {
        this.document = props.document
        this.linkService = linkService
        this.viewportProperties = PageViewportProperties(
          scale = 0.25f,
          rotation = 0f
        )
      }
    }
    mainContent {
      pageViewer {
        this.document = props.document
        this.linkService = linkService
        this.viewportProperties = PageViewportProperties(
          scale = 2f,
          rotation = 0f
        )
      }
    }
  }
}

private val sidebar = FC<PropsWithChildren> { props ->
  FlexGrid.column {
    css {
      backgroundColor = NamedColor.blue
      overflowY = Overflow.scroll
      padding = Padding(vertical = 0.px, horizontal = 12.px)
    }
    FlexGrid.column {
      css {
        justifyContent = JustifyContent.center
      }
      appendChildren(props)
    }
  }
}

private val mainContent = FC<PropsWithChildren> { props ->
  FlexGrid.fullColumn {
    css {
      backgroundColor = NamedColor.green
      overflowY = Overflow.scroll
      alignItems = AlignItems.center
    }
    appendChildren(props)
  }
}
