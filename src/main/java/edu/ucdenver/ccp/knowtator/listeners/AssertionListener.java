package edu.ucdenver.ccp.knowtator.listeners;

import edu.ucdenver.ccp.knowtator.annotation.Assertion;

public interface AssertionListener extends Listener {
    void assertionAdded(Assertion assertion);
}
