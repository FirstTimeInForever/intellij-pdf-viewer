package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef

import kotlinx.serialization.Serializable

@Serializable
data class SidebarViewState(
    val mode: SidebarViewMode = SidebarViewMode.THUMBNAILS,
    val hidden: Boolean = true
)
