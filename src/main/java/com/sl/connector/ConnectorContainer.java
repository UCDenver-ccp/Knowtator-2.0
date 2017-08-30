package com.sl.connector;

import com.sl.app.DraggableLabel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The collateral class contains array of connectors and renders them.
 * The rendering can be called in a different way. E.g. JConnectors cn be just
 * added as usual component. In this case programmer must care about their size,
 * and layout.
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * @author Stanislav Lapitsky
 * @version 1.0
 */
@SuppressWarnings("unused")
public class ConnectorContainer extends JPanel {
    private ArrayList<JConnector> connectors;
    private HashMap<String, DraggableLabel> nodes;

    public ConnectorContainer() {
        nodes = new HashMap<>();
    }

    public ConnectorContainer(ArrayList<JConnector> connectors) {
        this.connectors = connectors;
        nodes = new HashMap<>();
    }

    public void setConnectors(ArrayList<JConnector> connectors) {
        this.connectors = connectors;
    }

    public ArrayList<JConnector> getConnectors() {
        return connectors;
    }

    public void addNode(String name, DraggableLabel node) {
        nodes.put(name, node);
        add(node);
    }

    public DraggableLabel getNode(String name) {
        return nodes.get(name);
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (connectors != null) {
            for (JConnector connector : connectors) {
                if (connector != null) {
                    connector.paint(g);
                }
            }
        }
    }
}
