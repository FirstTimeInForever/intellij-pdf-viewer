package com.firsttimeinforever.intellij.pdf.viewer.mpi.model

import kotlinx.serialization.Serializable

@Serializable
data class ViewProperties(
  val pagesCount: Int = 0,
  val availableSidebarViewModes: Set<SidebarViewMode> = setOf(
    SidebarViewMode.NONE,
    SidebarViewMode.THUMBNAILS
  )
)
