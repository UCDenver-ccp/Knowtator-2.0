package edu.ucdenver.ccp.knowtator.io.xml;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.annotation.text.Annotation;
import edu.ucdenver.ccp.knowtator.annotation.text.AnnotationManager;
import edu.ucdenver.ccp.knowtator.annotation.text.AnnotationProperties;
import edu.ucdenver.ccp.knowtator.annotation.text.Span;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static edu.ucdenver.ccp.knowtator.annotation.text.AnnotationProperties.CLASS_ID;

public class XmlWriter {
    public static final Logger log = Logger.getLogger(KnowtatorManager.class);

    public static ArrayList<String> annotationToXML(Annotation annotation) {
        ArrayList<String> toWrite = new ArrayList<>();

        toWrite.add(String.format("  <%s>", XmlTags.ANNOTATION));
        toWrite.add(String.format("    <%s %s=\"%s\">%s</%s>", XmlTags.ANNOTATOR, XmlTags.ANNOTATOR_ID, annotation.getProperty(AnnotationProperties.ANNOTATOR_ID), annotation.getProperty(AnnotationProperties.ANNOTATOR), XmlTags.ANNOTATOR));
        for (Span span : annotation.getSpans()) {
            toWrite.add(String.format("    <%s %s=\"%s\" %s=\"%s\" />", XmlTags.SPAN, XmlTags.SPAN_START, span.getStart(), XmlTags.SPAN_END, span.getEnd()));
        }
        toWrite.add(String.format("    <%s %s=\"%s\">%s</%s>", XmlTags.MENTION_CLASS, XmlTags.MENTION_CLASS_ID, annotation.getProperty(CLASS_ID), annotation.getProperty(AnnotationProperties.CLASS_NAME), XmlTags.MENTION_CLASS));
        toWrite.add(String.format("  </%s>", XmlTags.CLASS_MENTION));
        toWrite.add(String.format("  </%s>", XmlTags.ANNOTATION));

        return toWrite;
    }

    public static void write(String fileName, AnnotationManager annotationManager) throws IOException, NoSuchFieldException {
        log.warn(String.format("Writing to: %s", fileName));

        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));

        bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        bw.newLine();

        annotationManager.getTextAnnotations().forEach((textSource, annotations) -> {
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
        bw.close();
    }
}
