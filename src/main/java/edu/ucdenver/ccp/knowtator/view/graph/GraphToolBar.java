/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.view.graph;

import edu.ucdenver.ccp.knowtator.view.KnowtatorIcons;
import org.apache.log4j.Logger;

import javax.swing.*;

class GraphToolBar extends JToolBar {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(GraphToolBar.class);

    private GraphViewer graphViewer;

    GraphToolBar(GraphViewer graphViewer) {

        this.graphViewer = graphViewer;

        setFloatable(false);

        add(addGraphNodeCommand());
        add(removeCellCommand());
        //TODO: Add arrows to switch between graph spaces
    }

    private JButton removeCellCommand() {
        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.REMOVE));
        button.setToolTipText("Remove vertex or edge");

        button.addActionListener(e -> graphViewer.removeSelectedCell());

        return button;
    }

    private JButton addGraphNodeCommand() {
        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.ADD));
        button.setToolTipText("Add annotation as node");

        button.addActionListener(e -> graphViewer.addSelectedAnnotationAsVertex());

        return button;
    }


}
