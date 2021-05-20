package com.firsttimeinforever.intellij.pdf.viewer.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchQuery(
  val text: String,
  val caseSensitive: Boolean = false,
  val wholeWord: Boolean = false,
  val regex: Boolean = false,
  val again: Boolean = false
)
