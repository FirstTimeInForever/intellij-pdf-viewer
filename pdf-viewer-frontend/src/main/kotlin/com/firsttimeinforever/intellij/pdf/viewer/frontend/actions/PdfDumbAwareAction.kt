package com.firsttimeinforever.intellij.pdf.viewer.frontend.actions

import com.intellij.openapi.project.DumbAware

abstract class PdfDumbAwareAction(viewModeAwareness: ViewModeAwareness = ViewModeAwareness.IDE): PdfAction(viewModeAwareness), DumbAware
