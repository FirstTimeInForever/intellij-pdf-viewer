package com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.types

import kotlinx.serialization.Serializable

@Serializable
data class InternalOutline(
  val dest: String,
  val title: String,
  val items: Array<InternalOutline>
)
