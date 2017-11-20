package edu.ucdenver.ccp.knowtator.annotation;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TextSourceManager {
    private Set<TextSource> textSources;

    private KnowtatorManager manager;
    private BasicKnowtatorView view;

    public TextSourceManager(KnowtatorManager manager) {
        this.manager = manager;
        textSources = new HashSet<>();
    }

    public TextSource addTextSource(String docID, String content) {
        for (TextSource t : textSources) {
            if (Objects.equals(t.getDocID(), docID)) {
                t.setView(view);
                return t;
            }
        }
        TextSource newTextSource = new TextSource(view, docID, content);
        textSources.add(newTextSource);
        if (view != null) view.textSourceAddedEvent(newTextSource);
        return newTextSource;
    }

    public Set<TextSource> getTextSources() {
        return textSources;
    }

    public void setView(BasicKnowtatorView view) {
        this.view = view;
        textSources.forEach(textSource -> textSource.setView(view));
    }
}
