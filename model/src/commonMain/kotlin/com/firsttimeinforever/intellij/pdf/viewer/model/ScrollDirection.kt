package com.firsttimeinforever.intellij.pdf.viewer.model

import kotlinx.serialization.Serializable

/**
 * Order of values matters!
 */
@Serializable
enum class ScrollDirection {
  VERTICAL,
  HORIZONTAL
}
