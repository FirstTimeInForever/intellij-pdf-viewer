package com.firsttimeinforever.intellij.pdf.viewer.model

import kotlinx.serialization.Serializable

@Serializable
enum class ZoomMode {
  CUSTOM,
  PAGE_WIDTH,
  PAGE_HEIGHT,
  PAGE_FIT,
  AUTO
}
