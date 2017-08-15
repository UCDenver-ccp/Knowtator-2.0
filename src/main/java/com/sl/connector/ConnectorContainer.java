package com.sl.connector;

import javax.swing.*;
import java.awt.*;

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
    private JConnector[] connectors;
    public ConnectorContainer() {
    }

    public ConnectorContainer(JConnector[] connectors) {
        this.connectors = connectors;
    }

    public void setConnectors(JConnector[] connectors) {
        this.connectors = connectors;
    }

    public JConnector[] getConnectors() {
        return connectors;
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
