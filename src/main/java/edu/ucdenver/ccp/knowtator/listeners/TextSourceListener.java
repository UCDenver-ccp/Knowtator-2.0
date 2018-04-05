package edu.ucdenver.ccp.knowtator.listeners;

import edu.ucdenver.ccp.knowtator.model.textsource.TextSource;

public interface TextSourceListener extends Listener {

    void textSourceAdded(TextSource textSource);

}
