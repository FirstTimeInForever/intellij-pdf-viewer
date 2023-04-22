package com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs

import com.firsttimeinforever.intellij.pdf.viewer.model.ViewTheme
import kotlinx.browser.window
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.asList
import org.w3c.dom.css.CSSImportRule
import org.w3c.dom.css.CSSStyleRule
import org.w3c.dom.css.CSSStyleSheet
import org.w3c.dom.css.get

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

    val css = window.document.styleSheets[0] as CSSStyleSheet
    val pdfViewerCss =
      css.cssRules.asList().filterIsInstance<CSSImportRule>().find { it.href == "pdf_viewer.css" }?.styleSheet

    val pageViewerRule = pdfViewerCss?.cssRules?.asList()?.filterIsInstance<CSSStyleRule>()
      ?.find { it.selectorText == ".pdfViewer .page" } as? CSSStyleRule
    pageViewerRule?.style?.removeProperty("-o-border-image")
    pageViewerRule?.style?.removeProperty("border-image")

    val thumbnailRule = css.cssRules.asList().filterIsInstance<CSSStyleRule>().find { it.selectorText == ".thumbnailImage" }
    thumbnailRule?.style?.removeProperty("box-shadow")

    //language=CSS
    val rule =
      """
      .page, .thumbnailImage {
        filter: invert(${viewTheme.colorInvertIntensity}%);
      }
      """.trimIndent()
    pdfViewerCss?.insertRule(rule, pdfViewerCss.cssRules.length)
  }
}
