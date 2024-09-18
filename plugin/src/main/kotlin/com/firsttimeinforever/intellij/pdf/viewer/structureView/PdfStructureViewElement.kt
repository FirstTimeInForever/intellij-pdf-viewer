package com.firsttimeinforever.intellij.pdf.viewer.structureView

import com.firsttimeinforever.intellij.pdf.viewer.model.PdfOutlineNode
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.navigation.ItemPresentation
import kotlinx.serialization.json.JsonPrimitive
import javax.swing.Icon

abstract class PdfStructureViewElement(val editor: PdfFileEditor, val node: PdfOutlineNode): StructureViewTreeElement, ItemPresentation {
  override fun getValue(): Any = node

  override fun navigate(requestFocus: Boolean) {
    editor.viewComponent.controller?.navigate(node.navigationReference)
  }

  override fun canNavigate(): Boolean {
    return node.navigationReference != JsonPrimitive("") &&
      editor.viewComponent.controller?.canNavigate() == true
  }

  override fun canNavigateToSource(): Boolean = false

  override fun getPresentation(): ItemPresentation = this

  override fun getPresentableText(): String = node.name

  override fun getIcon(unused: Boolean): Icon? = null
}
