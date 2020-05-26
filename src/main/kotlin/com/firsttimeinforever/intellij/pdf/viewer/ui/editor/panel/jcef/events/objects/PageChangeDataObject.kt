package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.objects

import kotlinx.serialization.Serializable

@Serializable
data class PageChangeDataObject(val pageNumber: Int) {
}
