package edu.ucdenver.ccp.knowtator.annotation.annotator;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class AnnotatorManager {

    public static final Logger log = Logger.getLogger(KnowtatorManager.class);
    public Annotator currentAnnotator;

    public Map<String, Annotator> annotatorMap;
    public KnowtatorManager manager;


    public AnnotatorManager(KnowtatorManager manager) {
        this.manager = manager;
        annotatorMap = new HashMap<>();
    }

    public Annotator addNewAnnotator(String annotatorName, String annotatorID) {

        if (annotatorMap.containsKey(annotatorName)) {
            return annotatorMap.get(annotatorName);
        }

        Annotator newAnnotator = new Annotator(manager, annotatorName, annotatorID);
        annotatorMap.put(annotatorName, newAnnotator);

        currentAnnotator = newAnnotator;

        return newAnnotator;
    }

    public Annotator getCurrentAnnotator() {
        return currentAnnotator;
    }

    public String[] getAnnotatorNames() {
        return annotatorMap.keySet().toArray(new String[annotatorMap.keySet().size()]);
    }

    public void switchAnnotator(String annotatorName) {
        if (annotatorName != null) {
            currentAnnotator = annotatorMap.get(annotatorName);
        }

    }

    public void removeAnnotator(String annotatorName) {
        if (annotatorName != null) {
            annotatorMap.remove(annotatorName);
        }
    }

    public Map<String, Annotator> getAnnotators() {
        return annotatorMap;
    }
}
