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

public class DecreaseTextSizeCommand extends DisposableAction {

    private KnowtatorView view;

    public DecreaseTextSizeCommand(KnowtatorView view) {
        super("Decrease Text Size", KnowtatorIcons.getIcon(KnowtatorIcons.DECREASE_TEXT_SIZE_ICON));
        this.view = view;
        this.putValue(AbstractAction.SHORT_DESCRIPTION, "Decrease the document text size");
    }

    @Override
    public void dispose() {

    }

    public void decreaseTextSize() {
        KnowtatorTextPane textPane = view.getTextViewer().getSelectedTextPane();
        StyledDocument doc = textPane.getStyledDocument();
        MutableAttributeSet attrs = textPane.getInputAttributes();
        Font font = doc.getFont(attrs);
        StyleConstants.setFontSize(attrs, font.getSize() - 2);
        doc.setCharacterAttributes(0, doc.getLength() + 1, attrs, false);
        textPane.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        decreaseTextSize();

    }
}
