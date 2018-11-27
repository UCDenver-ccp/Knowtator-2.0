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

import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollectionListener;
import edu.ucdenver.ccp.knowtator.model.collection.SelectionEvent;
import edu.ucdenver.ccp.knowtator.model.profile.ColorListener;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.view.KnowtatorComponent;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

import static edu.ucdenver.ccp.knowtator.view.actions.model.ProfileAction.assignColorToClass;

public class ColorList extends JList<OWLClass> implements KnowtatorCollectionListener<Profile>, KnowtatorComponent, ColorListener {

    private final ListSelectionListener lsl;

    public ColorList(KnowtatorView view) {
        setModel(new DefaultListModel<>());

        setCellRenderer(new ColorListRenderer<>());
	    lsl = e -> assignColorToClass(view, getSelectedValue());

        KnowtatorView.CONTROLLER.getProfileCollection().getSelection()
                .ifPresent(this::setCollection);
    }

    private void setCollection(Profile profile) {
        dispose();
        profile.getColors().keySet().stream().filter(o -> o instanceof OWLClass)
                .map(o -> (OWLClass) o)
                .sorted(KnowtatorView.CONTROLLER.getOWLModel().getOWLObjectComparator())
                .forEach(o -> ((DefaultListModel<OWLClass>) getModel()).addElement(o));
//        profile.getColors().keySet().stream().filter(o -> !(o instanceof OWLObject))
//                .sorted(Comparator.comparing(Object::toString))
//                .forEach(o -> ((DefaultListModel<OWLObject>) getModel()).addElement(o));
        reset();
    }

    @Override
    public void selected(SelectionEvent<Profile> event) {
        KnowtatorView.CONTROLLER.getProfileCollection().getSelection().ifPresent(this::setCollection);
    }

    @Override
    public void added() {
        KnowtatorView.CONTROLLER.getProfileCollection().getSelection().ifPresent(this::setCollection);
    }

    @Override
    public void removed() {
        KnowtatorView.CONTROLLER.getProfileCollection().getSelection().ifPresent(this::setCollection);
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
        KnowtatorView.CONTROLLER.getProfileCollection().addCollectionListener(this);
        KnowtatorView.CONTROLLER.getProfileCollection().addColorListener(this);
    }

    @Override
    public void dispose() {
        removeListSelectionListener(lsl);
        setModel(new DefaultListModel<>());
    }

    @Override
    public void colorChanged() {
        KnowtatorView.CONTROLLER.getProfileCollection().getSelection().ifPresent(this::setCollection);
    }

    class ColorListRenderer<o> extends JLabel implements ListCellRenderer<o> {

        ColorListRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            KnowtatorView.CONTROLLER.getProfileCollection().getSelection().ifPresent(profile -> setBackground(profile.getColors().get(value)));
            if (value instanceof OWLEntity) {
                KnowtatorView.CONTROLLER.getOWLModel().getOWLEntityRendering((OWLEntity) value).ifPresent(this::setText);
            } else {
                setText(value.toString());
            }
            return this;
        }
    }
}
