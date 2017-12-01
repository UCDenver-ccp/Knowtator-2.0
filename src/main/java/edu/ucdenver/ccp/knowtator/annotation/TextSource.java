package edu.ucdenver.ccp.knowtator.annotation;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class TextSource {
    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(TextSource.class);


    private AnnotationManager annotationManager;

    private String fileLocation;
    private String docID;
    private String content;

    TextSource(KnowtatorManager manager, BasicKnowtatorView view, String fileLocation, String docID) {
        this.fileLocation = fileLocation;
        this.docID = docID;
        this.annotationManager = new AnnotationManager(manager, view, this);
    }



    public String getDocID() {
        return docID;
    }
    public String getContent() {
        return content;
    }
    public AnnotationManager getAnnotationManager() {
        return annotationManager;
    }
    public String getFileLocation() {
        return fileLocation;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public void setView(BasicKnowtatorView view) {
        annotationManager.setView(view);
    }
}
