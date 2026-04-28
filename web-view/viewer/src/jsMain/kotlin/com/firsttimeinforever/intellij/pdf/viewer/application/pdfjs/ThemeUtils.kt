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
      setProperty(Internals.StyleVariables.sidebarBackgroundColor, viewTheme.background)
      setProperty(Internals.StyleVariables.iconsBackgroundColor, viewTheme.icons)
      setProperty(Internals.StyleVariables.treeItemColor, viewTheme.foreground)
      setProperty(Internals.StyleVariables.treeItemHoverColor, viewTheme.foreground)
      setProperty(Internals.StyleVariables.treeItemSelectedColor, viewTheme.foreground)
    }

    // Set CSS variables on #viewsManager element to support dark mode in PDF.js 5
    // The .sidebar and .treeView classes redefine these variables, so we need to set them at the parent level
    val viewsManagerElement = window.document.getElementById("viewsManager") as? HTMLElement
    if (viewsManagerElement != null) {
      with(viewsManagerElement.style) {
        setProperty(Internals.StyleVariables.sidebarBackgroundColor, viewTheme.background)
        setProperty(Internals.StyleVariables.sidebarNarrowBackgroundColor, viewTheme.background)
        setProperty(Internals.StyleVariables.treeItemColor, viewTheme.foreground)
        setProperty(Internals.StyleVariables.treeItemHoverColor, viewTheme.foreground)
        setProperty(Internals.StyleVariables.treeItemSelectedColor, viewTheme.foreground)
        setProperty(Internals.StyleVariables.textColor, viewTheme.foreground)
        // Use semi-transparent dark overlay for backgrounds - works with both light and dark foreground colors
        setProperty(Internals.StyleVariables.treeItemBackgroundColor, "rgba(0, 0, 0, 0.1)")
        setProperty(Internals.StyleVariables.treeItemSelectedBackgroundColor, "rgba(0, 0, 0, 0.2)")
      }
    }

    // Directly apply text colors to tree view links to bypass CSS variable scoping issues in PDF.js 5
    val treeViewElements = window.document.querySelectorAll(".treeView").asList()
    for (treeViewElement in treeViewElements) {
      val treeView = treeViewElement as? HTMLElement
      if (treeView != null) {
        with(treeView.style) {
          setProperty(Internals.StyleVariables.treeItemColor, viewTheme.foreground)
          setProperty(Internals.StyleVariables.treeItemHoverColor, viewTheme.foreground)
          setProperty(Internals.StyleVariables.treeItemSelectedColor, viewTheme.foreground)
        }
      }
    }

    // Apply text color to sidebar title (Pages/Document outline) to make them visible in dark mode
    val labelElements = window.document.querySelectorAll(".viewsManagerLabel").asList()
    for (labelElement in labelElements) {
      val label = labelElement as? HTMLElement
      if (label != null) {
        with(label.style) {
          setProperty(Internals.StyleVariables.textColor, viewTheme.foreground)
        }
      }
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
