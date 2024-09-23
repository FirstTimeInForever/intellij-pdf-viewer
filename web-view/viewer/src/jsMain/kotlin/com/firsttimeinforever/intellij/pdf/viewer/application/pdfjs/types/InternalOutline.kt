package com.firsttimeinforever.intellij.pdf.viewer.application.pdfjs.types

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class InternalOutline(
  val dest: JsonElement,
  val title: String,
  val items: Array<InternalOutline>
)
