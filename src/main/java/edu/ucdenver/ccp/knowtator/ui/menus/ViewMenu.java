package edu.ucdenver.ccp.knowtator.ui.menus;

import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
import edu.ucdenver.ccp.knowtator.ui.text.TextPane;

import javax.swing.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ViewMenu extends JMenu {

    private BasicKnowtatorView view;

    public ViewMenu(BasicKnowtatorView view) {
        super("View");
        this.view = view;

        add(increaseTextSizeCommand());
        add(decreaseTextSizeCommand());
        addSeparator();
        add(showForCurrentProfileCommand());
        addSeparator();
        add(showGraphViewerCommand());
    }

    private JMenuItem decreaseTextSizeCommand() {
        JMenuItem decreaseTextSize = new JMenuItem("Increase text size", KnowtatorIcons.getIcon(KnowtatorIcons.DECREASE_TEXT_SIZE_ICON));
        decreaseTextSize.addActionListener((ActionEvent e) -> {

            TextPane textPane = view.getTextViewer().getSelectedTextPane();
            StyledDocument doc = textPane.getStyledDocument();
            MutableAttributeSet attrs = textPane.getInputAttributes();
            Font font = doc.getFont(attrs);
            StyleConstants.setFontSize(attrs, font.getSize() - 2);
            doc.setCharacterAttributes(0, doc.getLength() + 1, attrs, false);
            textPane.repaint();

        });
        return decreaseTextSize;
    }

    private JMenuItem increaseTextSizeCommand() {
        JMenuItem increaseTextSize = new JMenuItem("Increase text size", KnowtatorIcons.getIcon(KnowtatorIcons.INCREASE_TEXT_SIZE_ICON));
        increaseTextSize.addActionListener((ActionEvent e) -> {
            TextPane textPane = view.getTextViewer().getSelectedTextPane();
            StyledDocument doc = textPane.getStyledDocument();
            MutableAttributeSet attrs = textPane.getInputAttributes();
            Font font = doc.getFont(attrs);
            StyleConstants.setFontSize(attrs, font.getSize() + 2);
            doc.setCharacterAttributes(0, doc.getLength() + 1, attrs, false);
            textPane.repaint();
        });

        return increaseTextSize;
    }

    private JCheckBoxMenuItem showForCurrentProfileCommand() {
        JCheckBoxMenuItem showForCurrentProfile = new JCheckBoxMenuItem("Show only annotations for current profile");
        showForCurrentProfile.addActionListener(e -> {
            if(showForCurrentProfile.getState()) view.profileFilterEvent(true);
            else view.profileFilterEvent(false);
        });

        return  showForCurrentProfile;
    }

    private JMenuItem showGraphViewerCommand() {
        JMenuItem menuItem = new JMenuItem("Show graph viewer");
        menuItem.addActionListener(e -> view.getTextViewer().getSelectedTextPane().getGraphDialog().setVisible(true));

        return menuItem;
    }
}
