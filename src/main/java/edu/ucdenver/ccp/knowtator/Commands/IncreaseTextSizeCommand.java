package edu.ucdenver.ccp.knowtator.Commands;

import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorIcons;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorTextPane;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;

public class IncreaseTextSizeCommand extends DisposableAction {

    private KnowtatorView view;

    public IncreaseTextSizeCommand(KnowtatorView view) {
        super("Increase Text Size", KnowtatorIcons.getIcon(KnowtatorIcons.INCREASE_TEXT_SIZE_ICON));
        this.view = view;
        this.putValue(AbstractAction.SHORT_DESCRIPTION, "Increase the document text size");
    }

    @Override
    public void dispose() {

    }

    public void increaseTextSize() {
        KnowtatorTextPane textPane = view.getTextViewer().getSelectedTextPane();
        StyledDocument doc = textPane.getStyledDocument();
        MutableAttributeSet attrs = textPane.getInputAttributes();
        Font font = doc.getFont(attrs);
        StyleConstants.setFontSize(attrs, font.getSize() + 2);
        doc.setCharacterAttributes(0, doc.getLength() + 1, attrs, false);
        textPane.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        increaseTextSize();

    }
}
