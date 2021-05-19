package com.firsttimeinforever.intellij.pdf.viewer.tex

import kotlinx.serialization.Serializable

/**
 * The information in this object comes from SyncTeX and is passed to the PDF viewer to show the rectangle.
 */
@Serializable
data class SynctexPreciseLocation(
  val page: Int,
  val x: Double,
  val y: Double,
  val width: Double,
  val height: Double,
)
