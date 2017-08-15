package com.sl.app;

import com.sl.connector.JConnector;
import com.sl.line.ConnectLine;

import javax.swing.*;
import java.awt.*;

class ConnectorPropertiesPanel extends JPanel {
    private JComboBox<String> cbxType = new JComboBox<>(new String[] {"Simple", "Rectangular"});
    private JComboBox<String> cbxArrow = new JComboBox<>(new String[] {"No arrow", "Source", "Dest", "Both"});
    private JButton btnColor = new JButton("...");
    private JConnector connector;
    ConnectorPropertiesPanel(JConnector connector) {
        this.connector = connector;
        setLayout(new GridBagLayout());
        add(new JLabel("Line type:"), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(cbxType, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));

        add(new JLabel("Line arrow:"), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(cbxArrow, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));

        add(new JLabel("Line color:"), new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(btnColor, new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));

        add(new JLabel(" "), new GridBagConstraints(0, 7, 2, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        setColor(connector.getLineColor());
        if (connector.getLineType() == ConnectLine.LINE_TYPE_SIMPLE) {
            cbxType.setSelectedIndex(0);
        }
        else {
            cbxType.setSelectedIndex(1);
        }
        switch (connector.getLineArrow()) {
            case ConnectLine.LINE_ARROW_NONE:
                cbxArrow.setSelectedIndex(0);
                break;
            case ConnectLine.LINE_ARROW_SOURCE:
                cbxArrow.setSelectedIndex(1);
                break;
            case ConnectLine.LINE_ARROW_DEST:
                cbxArrow.setSelectedIndex(2);
                break;
            case ConnectLine.LINE_ARROW_BOTH:
                cbxArrow.setSelectedIndex(3);
                break;
        }

        initListeners();
    }

    private void setColor(Color c) {
        btnColor.setBackground(c);
    }

    private void initListeners() {
        cbxType.addActionListener(e -> {
            if (cbxType.getSelectedIndex() == 0) {
                connector.setLineType(ConnectLine.LINE_TYPE_SIMPLE);
            }
            else {
                connector.setLineType(ConnectLine.LINE_TYPE_RECT_1BREAK);
            }
            getTopLevelAncestor().repaint();
        });
        cbxArrow.addActionListener(e -> {
            switch (cbxArrow.getSelectedIndex()) {
                case 0:
                    connector.setLineArrow(ConnectLine.LINE_ARROW_NONE);
                    break;
                case 1:
                    connector.setLineArrow(ConnectLine.LINE_ARROW_SOURCE);
                    break;
                case 2:
                    connector.setLineArrow(ConnectLine.LINE_ARROW_DEST);
                    break;
                case 3:
                    connector.setLineArrow(ConnectLine.LINE_ARROW_BOTH);
                    break;
            }
            getTopLevelAncestor().repaint();
        });

        btnColor.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(ConnectorPropertiesPanel.this, "Select line color", connector.getLineColor());
            if (newColor != null) {
                setColor(newColor);
                connector.setLineColor(newColor);
                getTopLevelAncestor().repaint();
            }
        });
    }
}
