package edu.ucdenver.ccp.knowtator.ui.menus;

import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class KnowtatorToolBar extends JToolBar {

    private final BasicKnowtatorView view;

    public KnowtatorToolBar(BasicKnowtatorView view) {
        this.view = view;

        add(previousSpanCommand());
        add(growSelectionStartCommand());
        add(shrinkSelectionStartCommand());
        add(shrinkSelectionEndCommand());
        add(growSelectionEndCommand());
        add(nextSpanCommand());
    }

    private JButton shrinkSelectionStartCommand() {
        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.RIGHT_ICON));
        button.setToolTipText("Shrink selection start");
        button.addActionListener((ActionEvent e) -> view.getTextViewer().getSelectedTextPane().shrinkSelectionStart());
        return button;
    }

    private JButton shrinkSelectionEndCommand() {
        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.LEFT_ICON));
        button.setToolTipText("Shrink selection end");
        button.addActionListener((ActionEvent e) -> view.getTextViewer().getSelectedTextPane().shrinkSelectionEnd());
        return button;
    }

    private JButton growSelectionStartCommand() {
        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.LEFT_ICON));
        button.setToolTipText("Grow selection start");
        button.addActionListener((ActionEvent e) -> view.getTextViewer().getSelectedTextPane().growSelectionStart());
        return button;
    }

    private JButton growSelectionEndCommand() {
        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.RIGHT_ICON));
        button.setToolTipText("Grow selection end");
        button.addActionListener((ActionEvent e) -> view.getTextViewer().getSelectedTextPane().growSelectionEnd());
        return button;
    }

    private JButton nextSpanCommand() {
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.NEXT_ICON));
        command.setToolTipText("Next span");
        command.addActionListener((ActionEvent e) -> view.getTextViewer().getSelectedTextPane().nextSpan());

        return command;
    }

    private JButton previousSpanCommand() {
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.PREVIOUS_ICON));
        command.setToolTipText("Previous span");
        command.addActionListener((ActionEvent e) ->view.getTextViewer().getSelectedTextPane().previousSpan());

        return command;
    }

}

