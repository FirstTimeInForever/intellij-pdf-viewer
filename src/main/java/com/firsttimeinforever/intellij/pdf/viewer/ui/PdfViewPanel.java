package com.firsttimeinforever.intellij.pdf.viewer.ui;

import com.intellij.openapi.Disposable;

import javax.swing.*;

public class PdfViewPanel implements Disposable {
    private JPanel viewPannel;

    public JComponent getComponent() {
        return viewPannel;
    }

    @Override
    public void dispose() {
        // Do nothing
    }
}
