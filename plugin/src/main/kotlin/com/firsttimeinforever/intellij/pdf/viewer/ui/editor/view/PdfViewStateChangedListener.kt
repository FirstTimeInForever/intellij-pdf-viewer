package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view

import com.firsttimeinforever.intellij.pdf.viewer.model.ViewState
import com.firsttimeinforever.intellij.pdf.viewer.model.ViewStateChangeReason
import com.intellij.util.messages.Topic

fun interface PdfViewStateChangedListener {
  fun viewStateChanged(
    viewController: PdfJcefPreviewController,
    viewState: ViewState,
    reason: ViewStateChangeReason
  )

  companion object {
    val TOPIC: Topic<PdfViewStateChangedListener> = Topic.create(
      "PdfViewStateChangedListener",
      PdfViewStateChangedListener::class.java
    )
  }
}
