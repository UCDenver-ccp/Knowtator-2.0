package edu.ucdenver.ccp.knowtator.Commands;

import edu.ucdenver.ccp.knowtator.ui.KnowtatorIcons;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorTextPane;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DecreaseTextSizeCommand extends DisposableAction {

    private final JTabbedPane tabbedPane;

    public DecreaseTextSizeCommand(JTabbedPane tabbedPane) {
        super("Decrease Text Size", KnowtatorIcons.getIcon(KnowtatorIcons.DECREASE_TEXT_SIZE_ICON));
        this.putValue(AbstractAction.SHORT_DESCRIPTION, "Decrease the document text size");
        this.tabbedPane = tabbedPane;
    }

    @Override
    public void dispose() {

    }

    private void decreaseTextSize() {
        KnowtatorTextPane textViewer = (KnowtatorTextPane)((JScrollPane)tabbedPane.getSelectedComponent()).getViewport().getView();
        StyledDocument doc = textViewer.getStyledDocument();
        MutableAttributeSet attrs = textViewer.getInputAttributes();
        Font font = doc.getFont(attrs);
        StyleConstants.setFontSize(attrs, font.getSize() - 2);
        doc.setCharacterAttributes(0, doc.getLength() + 1, attrs, false);
        textViewer.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        decreaseTextSize();

    }
}
