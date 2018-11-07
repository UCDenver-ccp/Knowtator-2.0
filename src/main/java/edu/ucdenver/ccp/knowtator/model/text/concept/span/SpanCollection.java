package edu.ucdenver.ccp.knowtator.model.text.concept.span;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SpanCollection extends KnowtatorCollection<Span> implements BratStandoffIO, KnowtatorXMLIO {

    public SpanCollection(KnowtatorController controller) {
        super(controller);
    }

    @Override
    public void remove(Span span) {
        super.remove(span);
    }

    @Override
    public void readFromBratStandoff(File file, Map<Character, List<String[]>> annotationMap, String content) {

    }

    @Override
    public void writeToBratStandoff(Writer writer, Map<String, Map<String, String>> annotationConfig, Map<String, Map<String, String>> visualConfig) throws IOException {
        Iterator<Span> spanIterator = iterator();
        StringBuilder spannedText = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            Span span = spanIterator.next();
            span.writeToBratStandoff(writer, annotationConfig, visualConfig);
            String[] spanLines = span.getSpannedText().split("\n");
            for (int j = 0; j < spanLines.length; j++) {
                spannedText.append(spanLines[j]);
                if (j != spanLines.length - 1) {
                    spannedText.append(" ");
                }
            }
            if (i != size() - 1) {
                writer.append(";");
                spannedText.append(" ");
            }
        }
        writer.append(String.format("\t%s\n", spannedText.toString()));
    }

    @Override
    public void writeToKnowtatorXML(Document dom, Element parent) {
        forEach(span -> span.writeToKnowtatorXML(dom, parent));
    }

    @Override
    public void readFromKnowtatorXML(File file, Element parent) {

    }

    @Override
    public void readFromOldKnowtatorXML(File file, Element parent) {

    }
}
