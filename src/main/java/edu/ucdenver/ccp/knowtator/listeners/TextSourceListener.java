package edu.ucdenver.ccp.knowtator.listeners;

import edu.ucdenver.ccp.knowtator.annotation.TextSource;

public interface TextSourceListener extends Listener {

    void textSourceAdded(TextSource textSource);

}
