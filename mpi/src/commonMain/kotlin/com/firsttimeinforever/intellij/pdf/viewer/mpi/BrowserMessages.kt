package com.firsttimeinforever.intellij.pdf.viewer.mpi

import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.DocumentInfo
import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.ViewProperties
import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.ViewState
import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.ViewStateChangeReason
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
}
