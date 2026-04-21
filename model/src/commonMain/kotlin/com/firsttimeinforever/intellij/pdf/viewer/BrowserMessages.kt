package com.firsttimeinforever.intellij.pdf.viewer

import com.firsttimeinforever.intellij.pdf.viewer.model.*
import com.firsttimeinforever.intellij.pdf.viewer.tex.SynctexViewCoordinates
import kotlinx.serialization.Serializable

object BrowserMessages {
  @Serializable
  data class InitialViewProperties(val properties: ViewProperties)

  @Serializable
  data class ViewStateChanged(
    val state: ViewState,
    val reason: ViewStateChangeReason
  )

  @Serializable
  data class BeforeReloadViewState(val state: ViewState)

  @Serializable
  data class DocumentInfoResponse(val info: DocumentInfo)

  @Serializable
  data class SynctexSyncEditor(val coordinates: SynctexViewCoordinates)

  @Serializable
  class AskForwardSearchData

  @Serializable
  data class DocumentOutline(val outlineNode: PdfOutlineNode)

  @Serializable
  data class SearchResponse(val result: SearchResult)

  /**
   * Structured diagnostic log batch forwarded from the web viewer (JS) to the
   * host (JVM) side. Each element in [lines] is emitted on the JVM logger at
   * [level] so it lands in `idea.log` through the IntelliJ
   * [com.intellij.openapi.diagnostic.Logger] API - instead of relying on
   * `console.log` plus [com.intellij.ui.jcef.JBCefBrowser] console-message
   * forwarding (which is gated behind the `pdf.viewer.debug` Registry flag).
   *
   * Batching avoids per-event IPC so it never becomes the very main-thread
   * backlog we are trying to measure. See Application.installWheelDiagnostics.
   */
  @Serializable
  data class DiagnosticLog(val level: String, val lines: List<String>)
}
