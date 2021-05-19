package com.firsttimeinforever.intellij.pdf.viewer.structureView

import com.firsttimeinforever.intellij.pdf.viewer.model.PdfOutlineNode
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode
import java.io.File

/**
 * Uses PdfBox library to extract contents of the [document] outline and builds a tree
 * out of [PdfOutlineNode].
 */
class PdfLocalOutlineBuilder(private val document: PDDocument, private val root: PDOutlineNode) {
  fun build(): PdfOutlineNode? {
    return when {
      root.hasChildren() -> root.children().mapNotNull(::traverse).takeIf { it.isNotEmpty() }?.let(PdfOutlineNode::createRootNode)
      else -> PdfOutlineNode.createRootNode()
    }
  }

  private fun traverse(node: PDOutlineItem): PdfOutlineNode? {
    if (!node.hasChildren()) {
      return createLeafElement(node)
    }
    val children = mutableListOf<PdfOutlineNode>()
    var current = node.firstChild
    while (current != null) {
      val child = traverse(current)
      if (child != null) {
        children.add(child)
      }
      current = current.nextSibling
    }
    return when {
      children.isNotEmpty() -> createCompositeElement(node, children)
      else -> createLeafElement(node)
    }
  }

  private fun createCompositeElement(item: PDOutlineItem, children: List<PdfOutlineNode>): PdfOutlineNode? {
    val title = item.title ?: return null
    return PdfOutlineNode(title, children, resolvePageNumber(item))
  }

  private fun createLeafElement(item: PDOutlineItem): PdfOutlineNode? {
    val title = item.title ?: return null
    return PdfOutlineNode(name = title, page = resolvePageNumber(item))
  }

  private fun resolvePageNumber(item: PDOutlineItem): Int? {
    val destination = item.destination ?: (item.action as? PDActionGoTo)?.destination
    return destination.let {
      when (it) {
        is PDPageDestination -> it.retrievePageNumber().takeIf { page -> page != -1 }
        is PDNamedDestination -> document.documentCatalog.findNamedDestinationPage(it)?.retrievePageNumber()
        else -> null
      }
    }
  }

  companion object {
    fun buildTree(file: File): PdfOutlineNode? {
      val document = Loader.loadPDF(file) ?: error("Failed to load pdf file!")
      return document.use {
        val outline = it.documentCatalog.documentOutline ?: return null
        PdfLocalOutlineBuilder(it, outline).build()
      }
    }
  }
}
