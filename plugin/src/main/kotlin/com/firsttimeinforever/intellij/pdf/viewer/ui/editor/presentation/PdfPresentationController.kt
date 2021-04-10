package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.presentation

class PdfPresentationController {
  fun enter() {
    TODO()
  }

  fun exit() {
    TODO()
  }

  val isPresentationModeActive: Boolean
    get() = false

  fun togglePresentationMode() {
    if (isPresentationModeActive) {
      exit()
    } else {
      enter()
    }
  }
}
