/*
 *  MIT License
 *
 *  Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.view.list;

import edu.ucdenver.ccp.knowtator.model.collection.event.SelectionEvent;
import edu.ucdenver.ccp.knowtator.model.collection.listener.ColorListener;
import edu.ucdenver.ccp.knowtator.model.collection.listener.ProfileCollectionListener;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.view.KnowtatorComponent;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

import static edu.ucdenver.ccp.knowtator.view.actions.modelactions.ProfileAction.assignColorToClass;

public class ColorList extends JList<OWLClass> implements ProfileCollectionListener, KnowtatorComponent, ColorListener {

    private final ListSelectionListener lsl;

    public ColorList(KnowtatorView view) {
        setModel(new DefaultListModel<>());

        setCellRenderer(new ColorListRenderer<>());
	    lsl = e -> assignColorToClass(view, getSelectedValue());

        KnowtatorView.MODEL.getSelectedProfile()
                .ifPresent(this::setCollection);
    }

    private void setCollection(Profile profile) {
        dispose();
        profile.getColors().keySet().stream()
                .sorted(KnowtatorView.MODEL.getOWLObjectComparator())
                .forEach(o -> ((DefaultListModel<OWLClass>) getModel()).addElement(o));
//        profile.getColors().keySet().stream().filter(o -> !(o instanceof OWLObject))
//                .sorted(Comparator.comparing(Object::toString))
//                .forEach(o -> ((DefaultListModel<OWLObject>) getModel()).addElement(o));
        reset();
    }

    @Override
    public void selected(SelectionEvent<Profile> event) {
        KnowtatorView.MODEL.getSelectedProfile().ifPresent(this::setCollection);
    }

    @Override
    public void added() {
        KnowtatorView.MODEL.getSelectedProfile().ifPresent(this::setCollection);
    }

    @Override
    public void removed() {
        KnowtatorView.MODEL.getSelectedProfile().ifPresent(this::setCollection);
    }

    @Override
    public void emptied() {

    }

    @Override
    public void firstAdded() {

    }

    @Override
    public void reset() {
        addListSelectionListener(lsl);
        setupListeners();
    }

    @Override
    public void setupListeners() {
        KnowtatorView.MODEL.addProfileCollectionListener(this);
        KnowtatorView.MODEL.addColorListener(this);
    }

    @Override
    public void dispose() {
        removeListSelectionListener(lsl);
        setModel(new DefaultListModel<>());
    }

    @Override
    public void colorChanged() {
        KnowtatorView.MODEL.getSelectedProfile().ifPresent(this::setCollection);
    }

    class ColorListRenderer<o> extends JLabel implements ListCellRenderer<o> {

        ColorListRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof OWLClass) {
                KnowtatorView.MODEL.getSelectedProfile().ifPresent(profile -> setBackground(profile.getColors().get(value)));
                setText(KnowtatorView.MODEL.getOWLEntityRendering((OWLEntity) value));
            }
            return this;
        }
    }
}
