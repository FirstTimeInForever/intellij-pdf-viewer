package com.firsttimeinforever.intellij.pdf.viewer.structureView

import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.PdfOutlineNode
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.intellij.icons.AllIcons
import com.intellij.ide.util.treeView.smartTree.TreeElement
import javax.swing.Icon

class PdfStructureViewLeafElement(editor: PdfFileEditor, node: PdfOutlineNode): PdfStructureViewElement(editor, node) {
  override fun getChildren(): Array<TreeElement> = TreeElement.EMPTY_ARRAY
}
