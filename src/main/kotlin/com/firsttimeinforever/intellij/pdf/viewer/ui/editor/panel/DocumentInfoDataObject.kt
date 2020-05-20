package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel

import kotlinx.serialization.Serializable

@Serializable
data class DocumentInfoDataObject(
    val fileName: String = "-",
    val author: String = "-",
    val creationDate: String = "-",
    val creator: String = "-",
    val fileSize: String = "-",
    val linearized: String = "-",
    val keywords: List<String> = emptyList(),
    val modificationDate: String = "-",
    val pageCount: Int = -1,
    val pageSize: String = "-",
    val producer: String = "-",
    val subject: String = "-",
    val title: String = "-",
    val version: String = "-"
) {
}
