package com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs

import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.ViewTheme
import kotlinx.browser.window
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.css.StyleSheet

object ThemeUtils {
  // https://github.com/allefeld/atom-pdfjs-viewer/issues/4#issuecomment-622942606
  fun generateStylesheet(viewTheme: ViewTheme): String {
    // language=CSS
    return """
    .outlineItemToggler.outlineItemsHidden::after {
      background-color: ${viewTheme.icons};
    }
    .outlineItemToggler::after {
      background-color: ${viewTheme.icons};
    }
    .outlineItem > a {
      color: ${viewTheme.foreground};
    }
    #toolbarSidebar {
      background-color: ${viewTheme.background};
    }
    .page, .thumbnailImage {
      filter: invert(${viewTheme.colorInvertIntensity}%);
    }
    """
  }

  fun attachStylesheet(document: Document, content: String): Element {
    val head = document.head
    checkNotNull(head)
    val element = document.createElement("style")
    element.textContent = content
    head.append(element)
    return element
  }

  fun updateColors(document: Document, viewTheme: ViewTheme) {
    val documentElement = window.document.documentElement as HTMLElement
    with(documentElement.style) {
      setProperty(Internals.StyleVariables.mainColor, viewTheme.foreground)
      setProperty(Internals.StyleVariables.bodyBackgroundColor, viewTheme.background)
      setProperty(Internals.StyleVariables.sidebarBackgroundColor, viewTheme.background)
      setProperty(Internals.StyleVariables.iconsBackgroundColor, viewTheme.icons)
      setProperty(Internals.StyleVariables.treeItemColor, viewTheme.foreground)
      setProperty(Internals.StyleVariables.treeItemHoverColor, viewTheme.foreground)
    }
  }
}
