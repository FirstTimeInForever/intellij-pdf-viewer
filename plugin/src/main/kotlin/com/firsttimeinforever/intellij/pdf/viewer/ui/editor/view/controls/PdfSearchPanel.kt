package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view.controls

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfActionUtils
import com.firsttimeinforever.intellij.pdf.viewer.model.SearchQuery
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view.PdfEditorViewComponent
import com.firsttimeinforever.intellij.pdf.viewer.utility.DocumentListenerAdapter
import com.intellij.find.FindInProjectSettings
import com.intellij.find.SearchTextArea
import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.ActionUtil
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.DumbAwareToggleAction
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import net.miginfocom.swing.MigLayout
import java.awt.event.KeyEvent
import javax.swing.Icon
import javax.swing.JPanel
import javax.swing.KeyStroke
import javax.swing.event.DocumentEvent

class PdfSearchPanel(private val viewComponent: PdfEditorViewComponent): JPanel(), Disposable {
  private val searchTextArea = SearchTextArea(JBTextArea(1, 16), true)
  private val resultsLabel = JBLabel("", UIUtil.ComponentStyle.SMALL)
  private var lastQuery: SearchQuery? = null

  private val caseSensitiveAction = MySwitchStateToggleAction(
    "Case Sensitive",
    AllIcons.Actions.MatchCase,
    AllIcons.Actions.MatchCaseHovered,
    AllIcons.Actions.MatchCaseSelected
  )

  private val wholeWordsAction = MySwitchStateToggleAction(
    "Words",
    AllIcons.Actions.Words,
    AllIcons.Actions.WordsHovered,
    AllIcons.Actions.WordsSelected
  )

  private val regexAction = MySwitchStateToggleAction(
    "Regex",
    AllIcons.Actions.Regex,
    AllIcons.Actions.RegexHovered,
    AllIcons.Actions.RegexSelected
  )

  private val escapeAction = DumbAwareAction.create {
    setEnabledState(false)
  }

  private val findForwardAction = ActionManager.getInstance().getAction("pdf.viewer.FindForwardAction")!!
  private val findBackwardAction = ActionManager.getInstance().getAction("pdf.viewer.FindBackwardAction")!!
  private val findForwardOnEnterAction = DumbAwareAction.create { ActionUtil.performAction(findForwardAction, it) }
  private val findBackwardOnShiftEnterAction = DumbAwareAction.create { ActionUtil.performAction(findBackwardAction, it) }

  init {
    layout = MigLayout("flowx, gap 8, ins 0, fillx, hidemode 3")
    border = JBUI.Borders.customLineTop(JBUI.CurrentTheme.EditorTabs.borderColor())
    searchTextArea.border = JBUI.Borders.compound(
      JBUI.Borders.customLineRight(JBUI.CurrentTheme.EditorTabs.borderColor()),
      JBUI.Borders.empty(3, 1, 2, 1)
    )
    searchTextArea.setExtraActions(caseSensitiveAction, wholeWordsAction, regexAction)
    add(searchTextArea)
    add(resultsLabel)

    val actionToolbar = PdfActionUtils.createActionToolbar(
      DefaultActionGroup(findForwardAction, findBackwardAction),
      ActionPlaces.EDITOR_TOOLBAR,
      this
    )
    add(actionToolbar.component, "pushx")

    searchTextArea.textArea.document.addDocumentListener(object : DocumentListenerAdapter() {
      override fun insertUpdate(event: DocumentEvent) {
        when {
          searchText.isNotEmpty() -> PdfActionUtils.performAction(findForwardAction, searchTextArea.textArea)
          else -> {
            viewComponent.controller?.releaseSearchHighlighting()
            updateResults(0, 0)
          }
        }
      }

      override fun removeUpdate(event: DocumentEvent) {
        insertUpdate(event)
      }
    })

    setEnabledState(false)
  }

  private fun registerActions() {
    searchTextArea.textArea.let {
      findForwardAction.registerCustomShortcutSet(it, this)
      findBackwardAction.registerCustomShortcutSet(it, this)
      findForwardOnEnterAction.registerCustomShortcutSet(CommonShortcuts.ENTER, it, this)
      findBackwardOnShiftEnterAction.registerCustomShortcutSet(CustomShortcutSet(
        KeyStroke.getKeyStroke(
          KeyEvent.VK_ENTER,
          KeyEvent.SHIFT_DOWN_MASK,
          true
        )
      ), it, this)
    }
  }

  private fun unregisterActions() {
    searchTextArea.textArea.let {
      findForwardAction.unregisterCustomShortcutSet(it)
      findBackwardAction.unregisterCustomShortcutSet(it)
      findForwardOnEnterAction.unregisterCustomShortcutSet(it)
      findBackwardOnShiftEnterAction.unregisterCustomShortcutSet(it)
    }
  }

  private fun onPanelShow() {
    val escape = ActionManager.getInstance().getAction("EditorEscape")?.shortcutSet ?: CommonShortcuts.ESCAPE
    escapeAction.registerCustomShortcutSet(escape, viewComponent, this)
    IdeFocusManager.getGlobalInstance().requestFocus(searchTextArea.textArea, true)
    updateResults(0, 0)
    registerActions()
    if (searchText.isNotEmpty()) {
      PdfActionUtils.performAction(findForwardAction, searchTextArea.textArea)
    }
  }

  private fun onPanelHide() {
    recordRecentSearch(searchText)
    escapeAction.unregisterCustomShortcutSet(viewComponent)
    unregisterActions()
    viewComponent.controller?.releaseSearchHighlighting()
  }

  fun setEnabledState(state: Boolean) {
    isVisible = state
    isEnabled = state
    when (state) {
      true -> onPanelShow()
      else -> onPanelHide()
    }
  }

  fun updateResults(current: Int, total: Int) {
    when {
      searchText.isEmpty() || total == 0 -> {
        resultsLabel.text = "0 results"
        resultsLabel.foreground = SimpleTextAttributes.ERROR_ATTRIBUTES.fgColor
      }
      else -> {
        resultsLabel.text = "$current/$total"
        resultsLabel.foreground = SimpleTextAttributes.REGULAR_ATTRIBUTES.fgColor
      }
    }
  }

  val searchText: String
    get() = searchTextArea.textArea.text

  val searchQuery: SearchQuery
    get() {
      var current = SearchQuery(
        searchText,
        caseSensitiveAction.holdState,
        wholeWordsAction.holdState,
        regexAction.holdState
      )
      // FIXME: Refactor
      if (lastQuery?.copy(again = false) == current) {
        current = current.copy(again = true)
      }
      lastQuery = current
      return current
    }

  private fun recordRecentSearch(value: String) {
    val settings = FindInProjectSettings.getInstance(viewComponent.project) ?: return
    settings.addStringToFind(value)
  }

  private inner class MySwitchStateToggleAction(
    message: String,
    icon: Icon,
    hoveredIcon: Icon,
    selectedIcon: Icon
  ): DumbAwareToggleAction(message, null, icon) {
    var holdState = false

    init {
      templatePresentation.hoveredIcon = hoveredIcon
      templatePresentation.selectedIcon = selectedIcon
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT

    override fun isSelected(event: AnActionEvent): Boolean {
      return holdState
    }

    override fun setSelected(event: AnActionEvent, state: Boolean) {
      holdState = state
      PdfActionUtils.performAction(findForwardAction, this@PdfSearchPanel)
    }
  }

  override fun dispose() = Unit
}
