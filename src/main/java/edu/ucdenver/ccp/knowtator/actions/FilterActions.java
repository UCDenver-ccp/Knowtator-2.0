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

package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.KnowtatorController;

import javax.swing.undo.UndoableEdit;

public class FilterActions {

    public static class FilterAction extends AbstractKnowtatorAction {

        private final KnowtatorController controller;
        private final String filter;
        private final boolean isFilter;
        private final boolean previousIsFilter;

        public FilterAction(KnowtatorController controller, String filter, boolean isFilter) {
            super("Change filter");
            this.controller = controller;
            this.filter = filter;
            this.isFilter = isFilter;
            this.previousIsFilter = controller.getFilterModel().isFilter(filter);

        }

        @Override
        public void execute() {
            controller.getFilterModel().setFilter(filter, isFilter);
        }

        @Override
        public UndoableEdit getEdit() {
            return new FilterEdit(controller, filter, isFilter, previousIsFilter);
        }
    }

    private static class FilterEdit extends KnowtatorEdit {
        private final KnowtatorController controller;
        private final boolean isFilter;
        private final String filter;
        private boolean previousIsFilter;

        FilterEdit(KnowtatorController controller, String filter, boolean isFilter, boolean previousIsFilter) {
            super("Change filter");
            this.controller = controller;
            this.isFilter = isFilter;
            this.filter = filter;
            this.previousIsFilter = previousIsFilter;
        }

        @Override
        public void undo() {
            controller.getFilterModel().setFilter(filter, previousIsFilter);
        }

        @Override
        public void redo() {
            controller.getFilterModel().setFilter(filter,isFilter);
        }


    }
}
