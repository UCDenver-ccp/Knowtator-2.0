package com.sl.app;

import com.sl.connector.ConnectorContainer;
import com.sl.connector.JConnector;
import com.sl.line.ConnectLine;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class TestApplication extends JFrame {
    private Canvas c = new Canvas();
    private ConnectorPropertiesPanel props;
    private TestApplication() {
        super("JConnector demo");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new GridBagLayout());
        init();

        getContentPane().add(new JLabel("Connectors example. You can drag the connected component to see how the line will be changed"),
                             new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
        getContentPane().add(initConnectors(),
                             new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        getContentPane().add(props,
                             new GridBagConstraints(1, 1, 1, 1, 0, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL, new Insets(5, 0, 5, 5), 0, 0));
//        getContentPane().add(c,
//                             new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void init() {
        ConnectLine[] lines = new ConnectLine[5];
        lines[0] = new ConnectLine(new Point(200, 10), new Point(50, 300), ConnectLine.LINE_TYPE_SIMPLE, ConnectLine.LINE_START_HORIZONTAL, ConnectLine.LINE_ARROW_BOTH);
        lines[1] = new ConnectLine(new Point(200, 10), new Point(200, 150), ConnectLine.LINE_TYPE_SIMPLE, ConnectLine.LINE_START_HORIZONTAL, ConnectLine.LINE_ARROW_BOTH);
        lines[2] = new ConnectLine(new Point(50, 150), new Point(100, 100), ConnectLine.LINE_TYPE_SIMPLE, ConnectLine.LINE_START_HORIZONTAL, ConnectLine.LINE_ARROW_BOTH);
        lines[3] = new ConnectLine(new Point(150, 120), new Point(60, 70), ConnectLine.LINE_TYPE_SIMPLE, ConnectLine.LINE_START_HORIZONTAL, ConnectLine.LINE_ARROW_BOTH);

//        lines[1]=new ConnectLine(new Point(30,10), new Point(80,70),ConnectLine.LINE_TYPE_RECT_1BREAK, ConnectLine.LINE_START_HORIZONTAL,ConnectLine.LINE_ARROW_NONE);
//        lines[2]=new ConnectLine(new Point(50,20), new Point(100,100),ConnectLine.LINE_TYPE_RECT_1BREAK, ConnectLine.LINE_START_VERTICAL,ConnectLine.LINE_ARROW_NONE);
//        lines[3]=new ConnectLine(new Point(70,30), new Point(150,170),ConnectLine.LINE_TYPE_RECT_2BREAK, ConnectLine.LINE_START_HORIZONTAL,ConnectLine.LINE_ARROW_NONE);
//        lines[4]=new ConnectLine(new Point(100,50), new Point(200,270),ConnectLine.LINE_TYPE_RECT_2BREAK, ConnectLine.LINE_START_VERTICAL,ConnectLine.LINE_ARROW_NONE);
        c.setLines(lines, Color.blue);
    }

    private ConnectorContainer initConnectors() {
        JConnector[] connectors = new JConnector[2];
        JLabel b1 = new DraggableLabel("Source 1");
        b1.setBounds(10, 10, 100, 50);
        JLabel b2 = new DraggableLabel("Dest 1");
        b2.setBounds(200, 20, 100, 50);
//        JLabel b3=new DraggableLabel("Source 2");
//        b3.setBounds(200,500,100,25);
//        JLabel b4=new DraggableLabel("Dest 2");
//        b4.setBounds(400,300,100,25);
        connectors[0] = new JConnector(b1, b2, ConnectLine.LINE_ARROW_SOURCE, JConnector.CONNECT_LINE_TYPE_RECTANGULAR, Color.red);
        props = new ConnectorPropertiesPanel(connectors[0]);
//        connectors[1]=new JConnector(b3, b4, ConnectLine.LINE_ARROW_DEST, Color.blue);
        ConnectorContainer cc = new ConnectorContainer(connectors);
        cc.setLayout(null);

        cc.add(b1);
        cc.add(b2);
        //cc.add(b3);
//        cc.add(b4);

        cc.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        return cc;
    }

    public static void main(String[] args) {
        TestApplication fr = new TestApplication();
        fr.setVisible(true);
    }

    //temp class to test lines drawing
    protected static class Canvas extends JPanel {
        ConnectLine[] lines;
        Color color;
        void setLines(ConnectLine[] lines, Color color) {
            this.lines = lines;
            this.color = color;
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.white);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.black);
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

            g.setColor(color);
            for (ConnectLine line : lines) {
                if (line != null) {
                    line.paint((Graphics2D) g);
                }
            }
        }
    }
}
