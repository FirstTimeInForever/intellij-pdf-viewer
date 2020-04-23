package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel

import kotlinx.serialization.Serializable

@Serializable
data class PageChangeEventDataObject(val pageNumber: Int) {
}
