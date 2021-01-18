package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef

import kotlinx.serialization.Serializable

@Serializable
data class JcefPanelPreviewState(
    var pageNumber: Int = 0,
    var scale: Double = 1.0,
    var verticalScroll: Double = 0.0,
    var horizontalScroll: Double = 0.0,
    var pageSpreadState: PageSpreadState = PageSpreadState.NONE,
    var sidebarViewState: SidebarViewState = SidebarViewState()
)
