package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.PdfEditorPanelPreviewProperties
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.objects.SidebarAvailableViewModes

data class JcefPanelPreviewProperties(
    override val pagesCount: Int = 0,
    // TODO: Replace with Set<SidebarViewMode>
    val sidebarAvailableViewModes: SidebarAvailableViewModes = SidebarAvailableViewModes()
): PdfEditorPanelPreviewProperties
