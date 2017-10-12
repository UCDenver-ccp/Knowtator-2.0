package edu.ucdenver.ccp.knowtator.xml;

import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.TextAnnotation.TextAnnotation;
import edu.ucdenver.ccp.knowtator.TextAnnotation.TextAnnotationManager;
import edu.ucdenver.ccp.knowtator.TextAnnotation.TextAnnotationProperties;
import edu.ucdenver.ccp.knowtator.TextAnnotation.TextSpan;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static edu.ucdenver.ccp.knowtator.TextAnnotation.TextAnnotationProperties.CLASS_ID;

public class XmlWriter {
    public static final Logger log = Logger.getLogger(KnowtatorView.class);

    public static ArrayList<String> annotationToXML(TextAnnotation textAnnotation) {
        ArrayList<String> toWrite = new ArrayList<>();

        toWrite.add(String.format("  <%s>", XmlTags.ANNOTATION));
        toWrite.add(String.format("    <%s %s=\"%s\">%s</%s>", XmlTags.ANNOTATOR, XmlTags.ANNOTATOR_ID, textAnnotation.getProperty(TextAnnotationProperties.ANNOTATOR_ID), textAnnotation.getProperty(TextAnnotationProperties.ANNOTATOR), XmlTags.ANNOTATOR));
        for (TextSpan textSpan : textAnnotation.getTextSpans()) {
            toWrite.add(String.format("    <%s %s=\"%s\" %s=\"%s\" />", XmlTags.SPAN, XmlTags.SPAN_START, textSpan.getStart(), XmlTags.SPAN_END, textSpan.getEnd()));
        }
        toWrite.add(String.format("    <%s %s=\"%s\">%s</%s>", XmlTags.MENTION_CLASS, XmlTags.MENTION_CLASS_ID, textAnnotation.getProperty(CLASS_ID), textAnnotation.getProperty(TextAnnotationProperties.CLASS), XmlTags.MENTION_CLASS));
        toWrite.add(String.format("  </%s>", XmlTags.CLASS_MENTION));
        toWrite.add(String.format("  </%s>", XmlTags.ANNOTATION));

        return toWrite;
    }

    public static void write(FileWriter fw, TextAnnotationManager textAnnotationManager) throws IOException, NoSuchFieldException {
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        bw.newLine();

        textAnnotationManager.getTextAnnotations().forEach((textSource, annotations) -> {
            try {
                bw.write(String.format("<%s %s=\"%s\">", XmlTags.ANNOTATIONS, XmlTags.TEXTSOURCE, textSource));
                bw.newLine();

                annotations.forEach(textAnnotation -> {
                    try {
                        for (String tag : annotationToXML(textAnnotation)) {
                            bw.write(tag);
                            bw.newLine();
                        }
                    } catch (IOException e) {
                        log.error("IOException");
                        e.printStackTrace();
                    }
                });
                bw.write(String.format("</%s>", XmlTags.ANNOTATIONS));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });

        bw.flush();
    }
}
