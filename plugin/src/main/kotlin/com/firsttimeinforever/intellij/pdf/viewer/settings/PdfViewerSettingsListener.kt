package com.firsttimeinforever.intellij.pdf.viewer.settings

fun interface PdfViewerSettingsListener {
    fun settingsChanged(settings: PdfViewerSettings)
}
