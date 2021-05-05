package com.firsttimeinforever.intellij.pdf.viewer.structureView

import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.PdfOutlineNode
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.intellij.ide.util.treeView.smartTree.TreeElement

class PdfStructureViewCompositeElement(
  editor: PdfFileEditor,
  node: PdfOutlineNode,
  private val children: Array<TreeElement>
): PdfStructureViewElement(editor, node) {
  override fun getChildren(): Array<TreeElement> = children
}
