package com.firsttimeinforever.intellij.pdf.viewer.actions.view

// abstract class SetPageSpreadStateActionBase(
//     private val targetState: PageSpreadState
// ): PdfPdfjsToggleActionAdapter(
//     isDisabledInPresentationMode = true,
//     isDisabledInIdePresentationMode = true
// ) {
//     override fun isSelected(event: AnActionEvent): Boolean {
//         val panel = getPanel(event) ?: return false
//         return panel.pageSpreadState == targetState
//     }
//
//     override fun setSelected(event: AnActionEvent, state: Boolean) {
//         val panel = getPanel(event) ?: return
//         panel.pageSpreadState = targetState
//     }
// }
//
// class SpreadEvenPagesAction: SetPageSpreadStateActionBase(PageSpreadState.EVEN)
//
// class SpreadNonePagesAction: SetPageSpreadStateActionBase(PageSpreadState.NONE)
//
// class SpreadOddPagesAction: SetPageSpreadStateActionBase(PageSpreadState.ODD)
