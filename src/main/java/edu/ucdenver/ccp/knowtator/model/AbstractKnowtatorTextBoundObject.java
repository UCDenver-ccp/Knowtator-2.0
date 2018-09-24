package edu.ucdenver.ccp.knowtator.model;

import edu.ucdenver.ccp.knowtator.model.text.TextSource;

public abstract class AbstractKnowtatorTextBoundObject<K extends AbstractKnowtatorObject<K>> extends AbstractKnowtatorObject<K> implements KnowtatorTextBoundObject<K> {

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
