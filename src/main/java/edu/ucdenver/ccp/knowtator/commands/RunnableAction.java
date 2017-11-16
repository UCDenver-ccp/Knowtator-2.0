package edu.ucdenver.ccp.knowtator.commands;

import javax.swing.*;
import java.awt.event.ActionEvent;

public abstract class RunnableAction extends AbstractAction {

    public RunnableAction(String name) {
        super(name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public abstract void run();
}
