package edu.ucdenver.cpbs.mechanic.Commands;

import edu.ucdenver.cpbs.mechanic.ui.MechAnICIcons;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICTextViewer;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;

public class IncreaseTextSizeCommand extends DisposableAction {

    private final JTabbedPane tabbedPane;

    public IncreaseTextSizeCommand(JTabbedPane tabbedPane) {
        super("Increase Text Size", MechAnICIcons.getIcon(MechAnICIcons.INCREASE_TEXT_SIZE_ICON));
        this.putValue(AbstractAction.SHORT_DESCRIPTION, "Increase the document text size");
        this.tabbedPane = tabbedPane;
    }

    @Override
    public void dispose() {

    }

    private void increaseTextSize() {
        MechAnICTextViewer textViewer = (MechAnICTextViewer)((JScrollPane)tabbedPane.getSelectedComponent()).getViewport().getView();
        StyledDocument doc = textViewer.getStyledDocument();
        MutableAttributeSet attrs = textViewer.getInputAttributes();
        Font font = doc.getFont(attrs);
        StyleConstants.setFontSize(attrs, font.getSize() + 2);
        doc.setCharacterAttributes(0, doc.getLength() + 1, attrs, false);
        textViewer.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        increaseTextSize();

    }
}
