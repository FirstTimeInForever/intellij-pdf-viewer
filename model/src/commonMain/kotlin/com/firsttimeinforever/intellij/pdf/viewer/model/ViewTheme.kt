package com.firsttimeinforever.intellij.pdf.viewer.model

import kotlinx.serialization.Serializable

@Serializable
data class ViewTheme(
  val background: String,
  val foreground: String,
  val icons: String,
  val colorInvertIntensity: Int
)
