package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef

import java.awt.BorderLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

class InfoEntryPanel(label: String, value: String): JPanel() {
    init {
        layout = BorderLayout()
        border = EmptyBorder(5, 10, 5, 10)
        add(JLabel("$label:  "), BorderLayout.WEST)
        if (value == "-" || value.isEmpty()) {
            add(JLabel("unspecified"), BorderLayout.EAST)
        } else {
            add(JLabel(value), BorderLayout.EAST)
        }
    }
}
