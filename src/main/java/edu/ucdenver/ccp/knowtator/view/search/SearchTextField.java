package edu.ucdenver.ccp.knowtator.view.search;

import edu.ucdenver.ccp.knowtator.model.collection.*;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.view.KnowtatorComponent;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchTextField extends JTextField implements KnowtatorCollectionListener<Span>, KnowtatorComponent, CaretListener {

    private final KnowtatorCollectionListener<TextSource> textSourceCollectionListener;
    private final KnowtatorCollectionListener<ConceptAnnotation> conceptAnnotationCollectionListener;
    private final KnowtatorView view;
    private Pattern pattern;
    private Matcher matcher;
    private int matchEnd;

    public SearchTextField(KnowtatorView view) {
        this.view = view;
        view.getKnowtatorTextPane().addCaretListener(this);

        pattern = Pattern.compile("");
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                makePattern();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                makePattern();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        textSourceCollectionListener = new KnowtatorCollectionListener<TextSource>() {
            @Override
            public void added(AddEvent<TextSource> event) {

            }

            @Override
            public void removed(RemoveEvent<TextSource> event) {

            }

            @Override
            public void changed(ChangeEvent<TextSource> event) {

            }

            @Override
            public void emptied() {

            }

            @Override
            public void firstAdded() {

            }


            @Override
            public void selected(SelectionChangeEvent<TextSource> event) {
                reactToTextSourceChange(event);
            }
        };

        conceptAnnotationCollectionListener = new KnowtatorCollectionListener<ConceptAnnotation>() {
            @Override
            public void added(AddEvent<ConceptAnnotation> event) {

            }

            @Override
            public void removed(RemoveEvent<ConceptAnnotation> event) {

            }

            @Override
            public void changed(ChangeEvent<ConceptAnnotation> event) {

            }


            @Override
            public void emptied() {

            }

            @Override
            public void firstAdded() {

            }


            @Override
            public void selected(SelectionChangeEvent<ConceptAnnotation> event) {
                reactToConceptAnnotationChange(event);
            }
        };

        view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
    }

    public int searchForward(TextSource textSource) {
        matcher.reset();
        int start = -1;
        matchEnd = 0;
        int startBound = view.getKnowtatorTextPane().getSelectionEnd();
        while (matcher.find()) {
            if (matcher.start() > startBound &&
                    (!view.getOnlyInAnnotationsCheckBox().isSelected() || !textSource.getConceptAnnotationCollection().getSpans(matcher.start()).isEmpty())) {
                start = matcher.start();
                matchEnd = matcher.end();
                break;
            }
        }
        if (matcher.hitEnd()) {
            matcher.reset();
            //noinspection ResultOfMethodCallIgnored
            matcher.find();
            if (!view.getOnlyInAnnotationsCheckBox().isSelected() || !textSource.getConceptAnnotationCollection().getSpans(matcher.start()).isEmpty()) {
                start = matcher.start();
                matchEnd = matcher.end();
            }
        }
        return start;
    }

    public int searchPrevious(TextSource textSource) {
        matcher.reset();
        int start = -1;
        matchEnd = 0;
        int endBound = view.getKnowtatorTextPane().getSelectionStart();
        while (matcher.find()) {
            if (matcher.start() < endBound || matcher.hitEnd() && (!view.getOnlyInAnnotationsCheckBox().isSelected() || !textSource.getConceptAnnotationCollection().getSpans(matcher.start()).isEmpty())) {
                start = matcher.start();
                matchEnd = matcher.end();
            } else if (start == -1) {
                endBound = view.getKnowtatorTextPane().getText().length();
            } else {
                break;
            }
        }
        return start;
    }

    public void makePattern() {
        pattern = Pattern.compile(getText(), (view.getRegexCheckBox().isSelected() ? 0 : Pattern.LITERAL) | (view.getCaseSensitiveCheckBox().isSelected() ? 0 : Pattern.CASE_INSENSITIVE));
        matcher.usePattern(pattern);
    }

    private void reactToConceptAnnotationChange(SelectionChangeEvent<ConceptAnnotation> event) {
        if (event.getOld() != null) {
            event.getOld().getSpanCollection().removeCollectionListener(this);
        }
        if (event.getNew() != null) {
            event.getNew().getSpanCollection().addCollectionListener(this);
        }
    }

    private void reactToTextSourceChange(SelectionChangeEvent<TextSource> event) {
        if (event.getOld() != null) {
            event.getOld().getConceptAnnotationCollection().removeCollectionListener(conceptAnnotationCollectionListener);
        }
        event.getNew().getConceptAnnotationCollection().addCollectionListener(conceptAnnotationCollectionListener);
        matcher = pattern.matcher(event.getNew().getContent());
    }

    @Override
    public void added(AddEvent<Span> event) {

    }

    @Override
    public void removed(RemoveEvent<Span> event) {

    }

    @Override
    public void changed(ChangeEvent<Span> event) {

    }

    @Override
    public void emptied() {

    }

    @Override
    public void firstAdded() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void reset() {
        view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
    }

    @Override
    public void selected(SelectionChangeEvent<Span> event) {
        if (event.getNew() != null) {
            setText(event.getNew().getSpannedText());
        }
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        if (!view.getRegexCheckBox().isSelected()) {
            setText(view.getKnowtatorTextPane().getSelectedText());
        }

    }

    public int getMatchEnd() {
        return matchEnd;
    }
}
