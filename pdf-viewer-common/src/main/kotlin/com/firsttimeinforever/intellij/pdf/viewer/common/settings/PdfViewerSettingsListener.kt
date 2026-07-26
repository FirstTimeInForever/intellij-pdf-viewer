package com.firsttimeinforever.intellij.pdf.viewer.common.settings

fun interface PdfViewerSettingsListener {
  fun settingsChanged(settings: PdfViewerSettings)
}
