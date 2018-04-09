package edu.ucdenver.ccp.knowtator.model.textsource;

import edu.ucdenver.ccp.knowtator.listeners.TextSourcesListener;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class TextSources {
    private TreeSet<TextSource> textSources;
    private List<TextSourcesListener> listeners;

    TextSources() {
        textSources = new TreeSet<>(TextSource::compare);
        listeners = new ArrayList<>();
    }

    public void addListener(TextSourcesListener listener) {
        listeners.add(listener);
    }

    public void add(TextSource textSource) {
        textSources.add(textSource);
        for (TextSourcesListener listener : listeners) {
            listener.textSourcesChanged(textSource, true);
        }
    }

    public void remove(TextSource textSource) {
        textSources.remove(textSource);
        for (TextSourcesListener listener : listeners) {
            listener.textSourcesChanged(textSource, false);
        }
    }

    public TreeSet<TextSource> getTextSources() {
        return textSources;
    }
}
