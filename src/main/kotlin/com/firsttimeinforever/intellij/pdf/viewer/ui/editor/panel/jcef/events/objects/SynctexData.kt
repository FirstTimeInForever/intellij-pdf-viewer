package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.objects

import kotlinx.serialization.Serializable

/**
 * The information in this class comes from the PDF viewer and is passed to SyncTeX to obtain the corresponding (tex)
 * source file and column number.
 *
 * @property page The page of the pdf file.
 * @property x The x coordinate in the pdf file.
 * @property y The y coordinate in the pdf file.
 */
@Serializable
data class SynctexInverseDataObject(
    val page: Int,
    val x: Int,
    val y: Int
)

/**
 * The information in this object comes from SyncTeX and is passed to the PDF viewer to show the rectangle.
 *
 * @property page The
 */
@Serializable
data class SynctexFowardDataObject(
    val page: Int,
    val x: Double,
    val y: Double,
    val width: Double,
    val height: Double,
)
