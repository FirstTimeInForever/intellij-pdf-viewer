package com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs

import com.firsttimeinforever.intellij.pdf.viewer.model.ViewTheme
import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import org.w3c.dom.asList
import org.w3c.dom.css.CSSImportRule
import org.w3c.dom.css.CSSStyleRule
import org.w3c.dom.css.CSSStyleSheet
import org.w3c.dom.css.get

object ThemeUtils {
  fun updateColors(viewTheme: ViewTheme) {
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
    val pdfViewerCss = css.cssRules.asList().filterIsInstance<CSSImportRule>().find { it.href == "pdf_viewer.css" }?.styleSheet

    // Remove the shadow of the borders independent of if the colors are inverted, so it is consistent when switching between inverted and regular colors.
    removeBorderShadow(css, pdfViewerCss)

    //language=CSS
    css.addOrReplaceRule(
      selectorText = ".page, .thumbnailImage",
      body = "filter: invert(${viewTheme.colorInvertIntensity}%);"
    )
  }

  /**
   * Remove the shadow around the pages because the shadow looks really strange when inverted.
   *
   * @param viewerCss A reference to viewer.css, which contains global styling for the pdf viewer.
   * @param pdfViewerCss A reference to pdf_viewer.css, which contains styling of the pages view.
   */
  private fun removeBorderShadow(viewerCss: CSSStyleSheet, pdfViewerCss: CSSStyleSheet?) {
    val pageViewerRule = pdfViewerCss?.cssRules?.asList()?.filterIsInstance<CSSStyleRule>()
      ?.find { it.selectorText == ".pdfViewer .page" }
    pageViewerRule?.style?.removeProperty("-o-border-image")
    pageViewerRule?.style?.removeProperty("border-image")

    val thumbnailRule = viewerCss.cssRules.asList().filterIsInstance<CSSStyleRule>().find { it.selectorText == ".thumbnailImage" }
    thumbnailRule?.style?.removeProperty("box-shadow")
  }

  /**
   * Remove the rule if it already exists, then add the rule.
   */
  private fun CSSStyleSheet.addOrReplaceRule(selectorText: String, body: String) {
    val existingIndex = cssRules.asList().filterIsInstance<CSSStyleRule>().indexOfFirst { it.selectorText == selectorText }
    if (existingIndex >= 0) {
      deleteRule(existingIndex)
    }

    insertRule("$selectorText { $body }", cssRules.length)
  }
}
