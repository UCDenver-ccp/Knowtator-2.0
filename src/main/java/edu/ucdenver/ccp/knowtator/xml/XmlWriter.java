package edu.ucdenver.ccp.knowtator.xml;

import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.TextAnnotation.TextAnnotation;
import edu.ucdenver.ccp.knowtator.TextAnnotation.TextAnnotationManager;
import edu.ucdenver.ccp.knowtator.TextAnnotation.TextSpan;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class XmlWriter {
    public static final Logger log = Logger.getLogger(KnowtatorView.class);

    public static ArrayList<String> annotationToXML(TextAnnotation textAnnotation) {
        ArrayList<String> toWrite = new ArrayList<>();

        toWrite.add(String.format("  <%s>", XmlTags.TAG_ANNOTATION));
        toWrite.add(String.format("    <%s %s=\"%s\">%s</%s>", XmlTags.TAG_ANNOTATOR, XmlTags.TAG_ANNOTATOR_ID, textAnnotation.getAnnotatorID(), textAnnotation.getAnnotatorName(), XmlTags.TAG_ANNOTATOR));
        for (TextSpan textSpan : textAnnotation.getTextSpans()) {
            toWrite.add(String.format("    <%s %s=\"%s\" %s=\"%s\" />", XmlTags.TAG_SPAN, XmlTags.TAG_SPAN_START, textSpan.getStart(), XmlTags.TAG_SPAN_END, textSpan.getEnd()));
        }
        toWrite.add(String.format("    <%s %s=\"%s\">%s</%s>", XmlTags.TAG_MENTION_CLASS, XmlTags.TAG_MENTION_CLASS_ID, textAnnotation.getClassID(), textAnnotation.getClassName(), XmlTags.TAG_MENTION_CLASS));
        toWrite.add(String.format("  </%s>", XmlTags.TAG_CLASS_MENTION));
        toWrite.add(String.format("  </%s>", XmlTags.TAG_ANNOTATION));

        return toWrite;
    }

    public static void write(FileWriter fw, TextAnnotationManager textAnnotationManager) throws IOException, NoSuchFieldException {
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        bw.newLine();

        textAnnotationManager.getTextAnnotations().forEach((textSource, annotations) -> {
            try {
                bw.write(String.format("<%s %s=\"%s\">", XmlTags.TAG_ANNOTATIONS, XmlTags.TAG_TEXTSOURCE, textSource));
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
                bw.write(String.format("</%s>", XmlTags.TAG_ANNOTATIONS));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });

        bw.flush();
    }
}
