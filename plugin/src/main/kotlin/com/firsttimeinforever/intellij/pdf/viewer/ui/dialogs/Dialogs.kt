package com.firsttimeinforever.intellij.pdf.viewer.ui.dialogs

import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.DocumentInfo
import com.intellij.openapi.ui.DialogBuilder

internal object Dialogs {
  fun showDocumentInfoDialog(info: DocumentInfo) {
    DialogBuilder().centerPanel(DocumentInfoDialogPanel(info)).showModal(true)
  }
}
