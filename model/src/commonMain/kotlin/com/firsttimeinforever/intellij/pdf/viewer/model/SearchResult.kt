package com.firsttimeinforever.intellij.pdf.viewer.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(
  val currentMatch: Int = 0,
  val totalMatches: Int = 0
)
