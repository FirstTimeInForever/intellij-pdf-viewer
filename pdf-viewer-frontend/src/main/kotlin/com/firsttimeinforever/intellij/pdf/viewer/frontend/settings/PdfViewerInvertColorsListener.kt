package com.firsttimeinforever.intellij.pdf.viewer.frontend.settings

import com.firsttimeinforever.intellij.pdf.viewer.common.settings.PdfViewerSettings

import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.ui.ColorUtil

class PdfViewerInvertColorsListener : EditorColorsListener {
  override fun globalSchemeChange(scheme: EditorColorsScheme?) {
    if (PdfViewerSettings.instance.invertColorsWithTheme) {
      val backGround = scheme?.defaultBackground ?: return
      PdfViewerSettings.instance.invertDocumentColors = ColorUtil.isDark(backGround)
      PdfViewerSettings.instance.notifyListeners()
    }
  }
}
