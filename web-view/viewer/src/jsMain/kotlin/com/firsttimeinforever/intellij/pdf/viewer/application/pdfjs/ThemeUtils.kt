package com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs

import com.firsttimeinforever.intellij.pdf.viewer.model.ViewTheme
import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import org.w3c.dom.asList
import org.w3c.dom.css.CSSStyleRule
import org.w3c.dom.css.CSSStyleSheet
import org.w3c.dom.css.get

object ThemeUtils {
  fun updateColors(viewTheme: ViewTheme) {
    val documentElement = window.document.documentElement as HTMLElement
    with(documentElement.style) {
      setProperty(Internals.StyleVariables.mainColor, viewTheme.foreground)
      setProperty(Internals.StyleVariables.bodyBackgroundColor, viewTheme.background)
      setProperty(Internals.StyleVariables.sidebarNarrowBackgroundColor, viewTheme.background)
      setProperty(Internals.StyleVariables.iconsBackgroundColor, viewTheme.icons)
      setProperty(Internals.StyleVariables.treeItemColor, viewTheme.foreground)
      setProperty(Internals.StyleVariables.treeItemHoverColor, viewTheme.foreground)
      setProperty(Internals.StyleVariables.treeItemSelectedColor, viewTheme.foreground)
    }

    val css = window.document.styleSheets[0] as CSSStyleSheet

    //language=CSS
    css.addOrReplaceRule(
      selectorText = ".page, .thumbnailImage",
      body = "filter: invert(${viewTheme.colorInvertIntensity}%);"
    )
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
