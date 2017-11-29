package edu.ucdenver.ccp.knowtator.annotation;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
import org.apache.commons.io.FilenameUtils;

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

    public void addTextSource(String fileLocation) {
        String docID = FilenameUtils.getBaseName(fileLocation);
        for (TextSource t : textSources) {
            if (Objects.equals(t.getDocID(), docID)) {
                t.setView(view);
                return;
            }
        }
        TextSource newTextSource = new TextSource(manager, view, fileLocation, docID);
        textSources.add(newTextSource);
        if (view != null) view.textSourceAddedEvent(newTextSource);
    }

    public TextSource addTextSource(String docID, String content) {
        for (TextSource t : textSources) {
            if (Objects.equals(t.getDocID(), docID)) {
                t.setView(view);
                return t;
            }
        }
        TextSource newTextSource = new TextSource(manager, view, null, docID);
        newTextSource.setContent(content);
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

    public void remove(TextSource textSource) {
        textSources.remove(textSource);
    }
}
