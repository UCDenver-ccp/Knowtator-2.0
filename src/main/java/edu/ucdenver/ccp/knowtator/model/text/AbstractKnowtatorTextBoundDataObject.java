package edu.ucdenver.ccp.knowtator.model.text;

import edu.ucdenver.ccp.knowtator.model.AbstractKnowtatorDataObject;

public abstract class AbstractKnowtatorTextBoundDataObject<K extends AbstractKnowtatorDataObject<K>> extends AbstractKnowtatorDataObject<K> implements KnowtatorTextBoundDataObjectInterface<K> {

    protected TextSource textSource;

    protected AbstractKnowtatorTextBoundDataObject(TextSource textSource, String id) {
        super(id);
        this.textSource = textSource;
    }

    @Override
    public TextSource getTextSource() {
        return textSource;
    }

}
