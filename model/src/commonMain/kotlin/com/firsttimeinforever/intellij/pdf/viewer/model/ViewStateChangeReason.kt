package com.firsttimeinforever.intellij.pdf.viewer.model

import kotlinx.serialization.Serializable

@Serializable
enum class ViewStateChangeReason {
  UNSPECIFIED,
  INITIAL,
  PAGE_SPREAD_STATE,
  ZOOM,
  PAGE_NUMBER,
  SIDEBAR_VIEW_MODE,
  SCROLL_DIRECTION
}
