package edu.ucdenver.ccp.knowtator.io.xml;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.annotation.CompositionalAnnotation;
import edu.ucdenver.ccp.knowtator.annotation.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.annotation.IdentityChainAnnotation;
import edu.ucdenver.ccp.knowtator.annotation.Span;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

class XmlWriter {
    private static final Logger log = Logger.getLogger(KnowtatorManager.class);

    private static void writeSpan(BufferedWriter bw, Span span) {
        try {
            bw.write(String.format("\t\t<%s %s=\"%s\" %s=\"%s\" />", XmlTags.SPAN, XmlTags.SPAN_START, span.getStart(), XmlTags.SPAN_END, span.getEnd()));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void writeConceptAnnotation(BufferedWriter bw, ConceptAnnotation annotation) {
        try {
            bw.write(String.format("\t<%s>", XmlTags.CONCEPT_ANNOTATION));
            bw.newLine();
            bw.write(String.format("\t\t<%s>%s</%s>", XmlTags.ANNOTATION_ID, annotation.getID(), XmlTags.ANNOTATION_ID));
            bw.newLine();
            bw.write(String.format("\t\t<%s>%s</%s>", XmlTags.ANNOTATOR, annotation.getAnnotator().getProfileID(), XmlTags.ANNOTATOR));
            bw.newLine();
            annotation.getSpans().forEach(span -> writeSpan(bw, span));
            bw.write(String.format("\t\t<%s>%s</%s>", XmlTags.CLASS_NAME, annotation.getClassName(), XmlTags.CLASS_NAME));
            bw.newLine();

            if (annotation.getClassID() != null) {
                bw.write(String.format("\t\t<%s>%s</%s>", XmlTags.CLASS_ID, annotation.getClassID(), XmlTags.CLASS_ID));
                bw.newLine();
            }
            bw.write(String.format("\t</%s>", XmlTags.CONCEPT_ANNOTATION));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void write(KnowtatorManager manager, String fileName) throws IOException {
        log.warn(String.format("Writing to: %s", fileName));

        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));

        bw.write(XmlTags.XML_HEADER);
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
        manager.getConfigProperties().getTextSourceFilters().forEach(textSource -> {
            try {
                bw.write(String.format("<%s %s=\"%s\">", XmlTags.DOCUMENT, XmlTags.DOCUMENT_ID, textSource.getDocID()));
                bw.newLine();
                bw.write(String.format("\t<%s>", XmlTags.TEXT));
                bw.write(textSource.getContent()
                        .replace("&", "&amp;")
                        .replace("<", "&lt;")
                        .replace(">", "&gt;")
                        .replace("\"", "&quot;")
                        .replace("\'", "&apos;")
                );
                bw.write(String.format("</%s>", XmlTags.TEXT));
                bw.newLine();
                if (manager.getConfigProperties().getSaveConceptAnnotations()) textSource.getAnnotationManager().getAnnotations(manager.getConfigProperties().getProfileFilters())
                        .forEach(annotation -> {
                            if (annotation instanceof IdentityChainAnnotation) writeIdentityChainAnnotation(bw, (IdentityChainAnnotation) annotation);
                            else if (annotation instanceof ConceptAnnotation) writeConceptAnnotation(bw, (ConceptAnnotation) annotation);
                            else if (annotation instanceof CompositionalAnnotation) writeCompositionalAnnotation(bw, (CompositionalAnnotation) annotation);
                        });
                bw.write(String.format("</%s>", XmlTags.DOCUMENT));
                bw.newLine();
            } catch(IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void writeIdentityChainAnnotation(BufferedWriter bw, IdentityChainAnnotation annotation) {
        try {
            bw.write(String.format("\t<%s>", XmlTags.IDENTITY_ANNOTATION));
            bw.newLine();
            bw.write(String.format("\t\t<%s>%s</%s>", XmlTags.ANNOTATION_ID, annotation.getID(), XmlTags.ANNOTATION_ID));
            bw.newLine();
            bw.write(String.format("\t\t<%s>%s</%s>", XmlTags.ANNOTATOR, annotation.getAnnotator().getProfileID(), XmlTags.ANNOTATOR));
            bw.newLine();
            annotation.getSpans().forEach(span -> writeSpan(bw, span));
            annotation.getCoreferringAnnotations().forEach(coreference -> writeCoreferrence(bw, coreference));
            bw.write(String.format("\t</%s>", XmlTags.IDENTITY_ANNOTATION));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeCoreferrence(BufferedWriter bw, String coreference) {
        try {
            bw.write(String.format("\t\t<%s>%s</%s>", XmlTags.COREFERRENCE, coreference, XmlTags.COREFERRENCE));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeHighlighter(BufferedWriter bw, String className, Color color) {
        try {
            bw.write(String.format("\t<%s>", XmlTags.HIGHLIGHTER));
            bw.newLine();
            bw.write(String.format("\t\t<%s>%s</%s>", XmlTags.CLASS_ID, className, XmlTags.CLASS_ID));
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
        manager.getConfigProperties().getProfileFilters().forEach(profile -> {
            try {
                bw.write(String.format("<%s %s=\"%s\">",
                        XmlTags.PROFILE,
                        XmlTags.PROFILE_ID, profile.getProfileID()
                ));
                bw.newLine();

                profile.getColors().forEach((className, color) -> writeHighlighter(bw, className, color));

                bw.write(String.format("</%s>", XmlTags.PROFILE));
                bw.newLine();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void writeCompositionalAnnotation(BufferedWriter bw, CompositionalAnnotation compositionalAnnotation) {
        try {
            bw.write(String.format("\t<%s>", XmlTags.COMPOSITIONAL_ANNOTATION));
            bw.newLine();
            bw.write(String.format("\t\t<%s>%s</%s>", XmlTags.ANNOTATION_ID, compositionalAnnotation.getID(), XmlTags.ANNOTATION_ID));
            bw.newLine();
            bw.write(String.format("\t\t<%s>%s</%s>", XmlTags.COMPOSITIONAL_ANNOTATION_GRAPH_TITLE, compositionalAnnotation.getGraphTitle(), XmlTags.COMPOSITIONAL_ANNOTATION_GRAPH_TITLE));
            bw.newLine();
            bw.write(String.format("\t\t<%s>%s</%s>", XmlTags.ANNOTATOR, compositionalAnnotation.getAnnotator().getProfileID(), XmlTags.ANNOTATOR));
            bw.newLine();
            bw.write(String.format("\t\t<%s>%s</%s>", XmlTags.COMPOSITIONAL_ANNOTATION_SOURCE, compositionalAnnotation.getSourceAnnotationID(), XmlTags.COMPOSITIONAL_ANNOTATION_SOURCE));
            bw.newLine();
            bw.write(String.format("\t\t<%s>%s</%s>", XmlTags.COMPOSITIONAL_ANNOTATION_TARGET, compositionalAnnotation.getTargetAnnotationID(), XmlTags.COMPOSITIONAL_ANNOTATION_TARGET));
            bw.newLine();
            bw.write(String.format("\t\t<%s>%s</%s>", XmlTags.COMPOSITIONAL_ANNOTATION_RELATIONSHIP, compositionalAnnotation.getRelationship(), XmlTags.COMPOSITIONAL_ANNOTATION_RELATIONSHIP));
            bw.newLine();
            bw.write(String.format("\t</%s>", XmlTags.COMPOSITIONAL_ANNOTATION));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
