package com.firsttimeinforever.intellij.pdf.viewer.model

import kotlinx.serialization.Serializable

@Serializable
enum class PageGotoDirection {
  FORWARD,
  BACKWARD
}
