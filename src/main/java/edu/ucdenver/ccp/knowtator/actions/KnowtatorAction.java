package edu.ucdenver.ccp.knowtator.actions;

abstract class KnowtatorAction {

    private String presentationName;

    KnowtatorAction(String presentationName) {

        this.presentationName = presentationName;
    }

    String getPresentationName() {
        return presentationName;
    }

    abstract void execute();
}
