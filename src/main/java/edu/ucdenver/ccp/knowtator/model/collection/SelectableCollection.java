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

package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.KnowtatorDataObjectInterface;

import java.util.TreeSet;

public abstract class SelectableCollection<K extends KnowtatorDataObjectInterface, L extends SelectableCollectionListener<K>> extends CyclableCollection<K, L> {

    private final KnowtatorController controller;
    private K selection;

    SelectableCollection(KnowtatorController controller, TreeSet<K> collection) {
        super(collection);
        this.controller = controller;
        selection = null;
    }

    public K getSelection() throws NoSelectionException {
        if (selection == null) {
            throw new NoSelectionException();
        }
        return selection;

    }

    public void selectNext() {
        setSelection(getNext(selection));
    }

    public void selectPrevious() {
        setSelection(getPrevious(selection));
    }

    public void setSelection(K newSelection) {
        if (controller != null && controller.isNotLoading() && this.selection != newSelection) {
            SelectionEvent<K> selectionEvent = new SelectionEvent<>(this.selection, newSelection);
            this.selection = newSelection;
            collectionListeners.forEach(selectionListener -> selectionListener.selected(selectionEvent));

        }
    }

    @Override
    public void add(K item) {
        super.add(item);
        setSelection(item);
    }

    @Override
    public void remove(K item) {
        super.remove(item);
        if (item == selection) {
            setSelection(null);
        }
    }

    public void removeSelected() {
        super.remove(selection);
        setSelection(null);
    }
}
