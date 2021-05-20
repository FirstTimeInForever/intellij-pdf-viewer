package com.firsttimeinforever.intellij.pdf.viewer.structureView

import com.firsttimeinforever.intellij.pdf.viewer.model.PdfOutlineNode
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor

object PdfStructureViewTreeBuilder {
  fun build(editor: PdfFileEditor): PdfStructureViewElement {
    val outline = editor.viewComponent.controller?.outline ?: return createEmptyLeaf(editor)
    return traverse(editor, outline)
  }

  fun buildLocally(editor: PdfFileEditor): PdfStructureViewElement {
    val targetFile = editor.file.toNioPath().toFile() ?: return createEmptyLeaf(editor)
    val root = PdfLocalOutlineBuilder.buildTree(targetFile) ?: return createEmptyLeaf(editor)
    return traverse(editor, root)
  }

  private fun createEmptyLeaf(editor: PdfFileEditor): PdfStructureViewElement {
    return PdfStructureViewLeafElement(editor, PdfOutlineNode.createRootNode())
  }

  private fun traverse(editor: PdfFileEditor, node: PdfOutlineNode): PdfStructureViewElement {
    return when {
      node.children.isEmpty() -> PdfStructureViewLeafElement(editor, node)
      else -> PdfStructureViewCompositeElement(
        editor,
        node,
        node.children.map { traverse(editor, it) }.toTypedArray()
      )
    }
  }
}
