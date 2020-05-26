package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events

import kotlinx.serialization.Serializable

@Serializable
data class DocumentInfoDataObject(
    val fileName: String = "",
    val author: String = "",
    val creationDate: String = "",
    val creator: String = "",
    val fileSize: String = "",
    val linearized: String = "",
    val keywords: String = "",
    val modificationDate: String = "",
    val pageCount: Int = 0,
    val pageSize: String = "",
    val producer: String = "",
    val subject: String = "",
    val title: String = "",
    val version: String = ""
) {
}
