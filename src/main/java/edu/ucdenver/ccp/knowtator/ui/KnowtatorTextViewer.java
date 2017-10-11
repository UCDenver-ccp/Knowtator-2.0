package edu.ucdenver.ccp.knowtator.ui;

import edu.ucdenver.ccp.knowtator.KnowtatorView;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("PackageAccessibility")
public class KnowtatorTextViewer extends JTabbedPane {
    public static final Logger log = Logger.getLogger(KnowtatorView.class);
    public KnowtatorView view;

    public KnowtatorTextViewer(KnowtatorView view) {
        super();
        this.view = view;
    }

    public void addNewDocument(String fileName, Boolean fromResources) {
        log.warn(String.format("Name: %s", FilenameUtils.getBaseName(fileName)));
        KnowtatorTextPane textPane = new KnowtatorTextPane(view);
        textPane.setName(FilenameUtils.getBaseName(fileName));
        view.getTextAnnotationManager().addTextSource(FilenameUtils.getBaseName(fileName));

        JScrollPane sp = new JScrollPane(textPane);
        if (getTabCount() == 1 && getTitleAt(0).equals("Untitled")) {
            setComponentAt(0, sp);
        } else {
            add(sp);
        }
        setTitleAt(getTabCount() - 1, FilenameUtils.getBaseName(fileName));

        try {
            if (fromResources) {
                log.warn("Loading article from resources");
                textPane.read(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fileName))), fileName);
            } else {
                textPane.read(new BufferedReader(new InputStreamReader(new FileInputStream(fileName))), fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDocuments(List<String> articleFileNames, List<String> annotationFileNames, Boolean fromResources) {
        for(String articleFileName: articleFileNames) {
            addNewDocument(articleFileName, fromResources);
        }

        for (String annotationFileName: annotationFileNames) {
            try {
                if (fromResources) {
                    log.warn("Loading annotations from resources");
                    view.getXmlUtil().read(getClass().getResourceAsStream(annotationFileName));
                } else {
                    view.getXmlUtil().read(new FileInputStream(annotationFileName));
                }
            } catch (ParserConfigurationException | IOException | SAXException e) {
                e.printStackTrace();
            }
        }
    }

    public KnowtatorTextPane getTextPaneByName(String name) {
        for (int i = 0; i < view.getTextViewer().getTabCount(); i++) {
            if (Objects.equals(view.getTextViewer().getTitleAt(i), name)) {
                return (KnowtatorTextPane) ((JScrollPane) view.getTextViewer().getComponent(i)).getViewport().getView();
            }
        }
        return null;
    }

    public ArrayList<KnowtatorTextPane> getAllTextPanes() {
        ArrayList<KnowtatorTextPane> textPanes = new ArrayList<>();

        for (Component component: getComponents()) {
            textPanes.add((KnowtatorTextPane) ((JScrollPane) component).getViewport().getView());
        }

        return textPanes;
    }

    @SuppressWarnings("unused")
    public KnowtatorTextPane getTextPaneByIndex(int i) {
        JScrollPane scrollPane = (JScrollPane) getComponent(i);
        JViewport viewPort = (scrollPane).getViewport();
        return (KnowtatorTextPane) viewPort.getView();
    }

    public KnowtatorTextPane getSelectedTextPane() {
        return (KnowtatorTextPane) ((JScrollPane) getSelectedComponent()).getViewport().getView();
    }

}
