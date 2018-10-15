package edu.ucdenver.ccp.knowtator.model;

import java.util.ArrayList;
import java.util.List;

public class FilterModel implements BaseKnowtatorModel {
    private boolean isFilterByOWLClass;
    private boolean isFilterByProfile;

    private final List<FilterModelListener> filterModelListeners;

    public FilterModel() {
        filterModelListeners = new ArrayList<>();
        isFilterByOWLClass = false;
        isFilterByProfile = false;
    }

    public void addFilterModelListener(FilterModelListener listener) {
        filterModelListeners.add(listener);
    }

    public boolean isFilterByOWLClass() {
        return isFilterByOWLClass;
    }

    public void setFilterByOWLClass(boolean filterByOWLClass) {
        isFilterByOWLClass = filterByOWLClass;
        filterModelListeners.forEach(l -> l.owlClassFilterChanged(isFilterByOWLClass));
    }

    public boolean isFilterByProfile() {
        return isFilterByProfile;
    }

    public void setFilterByProfile(boolean filterByProfile) {
        isFilterByProfile = filterByProfile;
        filterModelListeners.forEach(listener -> listener.profileFilterChanged(isFilterByProfile));
    }


    @Override
    public void dispose() {
        filterModelListeners.clear();
    }

    @Override
    public void reset() {

    }

    @Override
    public void finishLoad() {

    }
}