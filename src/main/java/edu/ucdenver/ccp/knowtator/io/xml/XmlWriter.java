package edu.ucdenver.ccp.knowtator.io.xml;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.annotation.Span;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

class XmlWriter {
    private static final Logger log = Logger.getLogger(KnowtatorManager.class);

    private static void spanToXml(BufferedWriter bw, Span span) {
        try {
            bw.write(String.format("\t\t<%s %s=\"%s\" %s=\"%s\" />", XmlTags.SPAN, XmlTags.SPAN_START, span.getStart(), XmlTags.SPAN_END, span.getEnd()));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void annotationToXML(BufferedWriter bw, Annotation annotation) {
        try {
            bw.write(String.format("\t<%s>", XmlTags.ANNOTATION));
            bw.newLine();
            bw.write(String.format("\t\t<%s>%s</%s>", XmlTags.ANNOTATOR, annotation.getAnnotator().getProfileID(), XmlTags.ANNOTATOR));
            bw.newLine();
            annotation.getSpans().forEach(span -> spanToXml(bw, span));
            bw.write(String.format("\t\t<%s>%s</%s>", XmlTags.CLASS_NAME, annotation.getClassName(), XmlTags.CLASS_NAME));
            bw.newLine();

            if (annotation.getClassID() != null) {
                bw.write(String.format("\t\t<%s>%s</%s>", XmlTags.CLASS_ID, annotation.getClassID(), XmlTags.CLASS_ID));
                bw.newLine();
            }
            bw.write(String.format("\t</%s>", XmlTags.ANNOTATION));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void write(KnowtatorManager manager, String fileName) throws IOException, NoSuchFieldException {
        log.warn(String.format("Writing to: %s", fileName));

        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));

        bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        bw.newLine();
        bw.write(String.format("<%s>", XmlTags.KNOWTATOR_PROJECT));
        bw.newLine();

        writeDocuments(manager, bw);
        bw.newLine();
        writeProfiles(manager, bw);
        bw.newLine();
        writeConfig(manager, bw);

        bw.write(String.format("</%s>", XmlTags.KNOWTATOR_PROJECT));
        bw.flush();
        bw.close();
    }

    private static void writeConfig(KnowtatorManager manager, BufferedWriter bw) {
        try {
            bw.write(String.format("<%s>", XmlTags.CONFIG));
            bw.newLine();
            bw.write(String.format("\t<%s>%s</%s>", XmlTags.AUTO_LOAD_ONTOLOGIES, manager.getConfigProperties().getAutoLoadOntologies(), XmlTags.AUTO_LOAD_ONTOLOGIES));
            bw.newLine();
            bw.write(String.format("\t<%s>%s</%s>", XmlTags.FORMAT, manager.getConfigProperties().getFormat(), XmlTags.FORMAT));
            bw.newLine();
            bw.write(String.format("\t<%s>${user.home}${file.separator}KnowtatorProjects${file.separator}</%s>", XmlTags.DEFAULT_SAVE_LOCATION, XmlTags.DEFAULT_SAVE_LOCATION));
            bw.newLine();
            bw.write(String.format("</%s>", XmlTags.CONFIG));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeDocuments(KnowtatorManager manager, BufferedWriter bw) {
        manager.getTextSourceManager().getTextSources().forEach(textSource -> {
        try {
                bw.write(String.format("<%s %s=\"%s\">", XmlTags.DOCUMENT, XmlTags.DOCUMENT_ID, textSource));
                bw.newLine();
                bw.write(String.format("            \t<%s>%s</%s>", XmlTags.TEXT, textSource.getContent(), XmlTags.TEXT));
                bw.newLine();
                textSource.getAnnotations().forEach(annotation -> annotationToXML(bw, annotation));
                bw.write(String.format("</%s>", XmlTags.DOCUMENT));
                bw.newLine();
            } catch(IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void highlighterToXml(BufferedWriter bw, String className, Color color) {
        try {
            bw.write(String.format("\t<%s>", XmlTags.HIGHLIGHTER));
            bw.newLine();
            bw.write(String.format("\t\t<%s>%s</%s>", XmlTags.CLASS_NAME, className, XmlTags.CLASS_NAME));
            bw.newLine();
            bw.write(String.format("\t\t<%s>%s</%s>", XmlTags.COLOR, String.format("#%06x", color.getRGB() & 0x00FFFFFF), XmlTags.COLOR));
            bw.newLine();
            bw.write(String.format("\t</%s>", XmlTags.HIGHLIGHTER));
            bw.newLine();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeProfiles(KnowtatorManager manager, BufferedWriter bw) {
        manager.getProfileManager().getProfiles().values().forEach(profile -> {
            try {
                bw.write(String.format("<%s %s=\"%s\">",
                        XmlTags.PROFILE,
                        XmlTags.PROFILE_ID, profile.getProfileID()
                ));
                bw.newLine();

                profile.getColors().forEach((className, color) -> highlighterToXml(bw, className, color));

                bw.write(String.format("</%s>", XmlTags.PROFILE));
                bw.newLine();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}