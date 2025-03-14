package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerActionsBundle
import com.intellij.ide.ui.customization.CustomizableActionGroupProvider

class PdfCustomizableActionGroupProvider : CustomizableActionGroupProvider() {
  override fun registerGroups(registrar: CustomizableActionGroupRegistrar) {
    registrar.addCustomizableActionGroup("pdf.viewer.LeftToolbarActionGroup", PdfViewerActionsBundle.message("group.pdf.viewer.LeftToolbarActionGroup.text"))
    registrar.addCustomizableActionGroup("pdf.viewer.RightToolbarActionGroup", PdfViewerActionsBundle.message("group.pdf.viewer.RightToolbarActionGroup.text"))
  }
}
