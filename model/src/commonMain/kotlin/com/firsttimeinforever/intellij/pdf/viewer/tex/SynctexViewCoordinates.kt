package com.firsttimeinforever.intellij.pdf.viewer.tex

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
data class SynctexViewCoordinates(
  val page: Int,
  val x: Int,
  val y: Int
)
