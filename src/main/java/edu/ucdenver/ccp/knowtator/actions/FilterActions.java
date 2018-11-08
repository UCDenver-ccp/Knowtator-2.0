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
