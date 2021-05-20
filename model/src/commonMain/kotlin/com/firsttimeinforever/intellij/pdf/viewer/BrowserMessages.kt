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
  data class DocumentInfoResponse(val info: DocumentInfo)

  @Serializable
  data class SynctexSyncEditor(val coordinates: SynctexViewCoordinates)

  @Serializable
  class AskForwardSearchData

  @Serializable
  data class DocumentOutline(val outlineNode: PdfOutlineNode)

  @Serializable
  data class SearchResponse(val result: SearchResult)
}
