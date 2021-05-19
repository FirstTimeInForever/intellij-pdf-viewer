package com.firsttimeinforever.intellij.pdf.viewer.model

import kotlinx.serialization.Serializable

@Serializable
data class ViewState(
  val page: Int = 0,
  val zoom: ZoomState = ZoomState(),
  val sidebarViewMode: SidebarViewMode = SidebarViewMode.NONE,
  val pageSpreadState: PageSpreadState = PageSpreadState.NONE,
  val scrollDirection: ScrollDirection = ScrollDirection.VERTICAL
)
