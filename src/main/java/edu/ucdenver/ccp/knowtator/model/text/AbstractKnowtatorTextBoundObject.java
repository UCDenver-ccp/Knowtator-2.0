package edu.ucdenver.ccp.knowtator.model.text;

import edu.ucdenver.ccp.knowtator.model.AbstractKnowtatorObject;

public abstract class AbstractKnowtatorTextBoundObject<K extends AbstractKnowtatorObject<K>> extends AbstractKnowtatorObject<K> implements KnowtatorTextBoundObjectInterface<K> {

    protected TextSource textSource;

    public AbstractKnowtatorTextBoundObject(TextSource textSource, String id) {
        super(id);
        this.textSource = textSource;
    }

    @Override
    public TextSource getTextSource() {
        return textSource;
    }

}
