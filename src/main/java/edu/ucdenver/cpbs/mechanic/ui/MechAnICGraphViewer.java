package edu.ucdenver.cpbs.mechanic.ui;

import com.sl.app.ConnectorPropertiesPanel;
import com.sl.app.DraggableLabel;
import com.sl.connector.ConnectorContainer;
import com.sl.connector.JConnector;
import com.sl.line.ConnectLine;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("Duplicates")
public class MechAnICGraphViewer extends JPanel {
    private final Integer NODE_WIDTH = 100, NODE_HEIGHT = 50;

    private ConnectorPropertiesPanel connectorPropertiesPanel;
    private ArrayList<JConnector> connectors;
    private ConnectorContainer connectorContainer;

    public MechAnICGraphViewer() {
        setLayout(new GridBagLayout());

        connectors = new ArrayList<>();
        connectorContainer = new ConnectorContainer();
        connectorContainer.setLayout(null);
        connectorContainer.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        connectorContainer.setConnectors(connectors);
        add(connectorContainer, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        initConnectors();

//        connectorPropertiesPanel = new ConnectorPropertiesPanel(connectors.get(0));
//        add(connectorPropertiesPanel, new GridBagConstraints(1, 1, 1, 1, 0, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL, new Insets(5, 0, 5, 5), 0, 0));


    }

    public void addNode(String name) {
        DraggableLabel newNode = new DraggableLabel(name);
        int x = 10;
        int y = 10;
        for (Component node: connectorContainer.getComponents()) {
            if (y == node.getY()) {
                y += NODE_HEIGHT + 10;
            }
        }
        newNode.setBounds(x, y, NODE_WIDTH, NODE_HEIGHT);
        connectorContainer.addNode(name, newNode);
    }

    private void makeConnection(String nodeName1, String nodeName2) {
        JComponent node1 = connectorContainer.getNode(nodeName1);
        JComponent node2 = connectorContainer.getNode(nodeName2);
        if (node1 != null && node2 != null) {
            JConnector newConnection = new JConnector(node1, node2, ConnectLine.LINE_ARROW_SOURCE, JConnector.CONNECT_LINE_TYPE_RECTANGULAR, Color.red);
            connectors.add(newConnection);
        } else {
            System.out.println(String.format("Node1: %s\tNode2: %s", nodeName1, nodeName2));
        }
    }

    private void initConnectors() {
        String nodeName1 = "Node 1";
        String nodeName2 = "Node 2";
        addNode(nodeName1);
        addNode(nodeName2);

        makeConnection(nodeName1, nodeName2);
    }
}
