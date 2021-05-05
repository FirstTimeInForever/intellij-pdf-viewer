package com.firsttimeinforever.intellij.pdf.viewer.mpi.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class PdfOutlineNode(
  val name: String,
  val children: List<PdfOutlineNode> = emptyList(),
  val page: Int? = null,
  val navigationReference: String = ""
) {
  val isRoot: Boolean
    get() = page == null && name == ROOT_NAME

  companion object {
    fun createRootNode(children: List<PdfOutlineNode> = emptyList(), navigationReference: String = ""): PdfOutlineNode {
      return PdfOutlineNode(ROOT_NAME, children, null, navigationReference)
    }

    @Transient
    const val ROOT_NAME = "__##root"
  }
}
