package edu.ucdenver.ccp.knowtator.model;

import java.util.ArrayList;
import java.util.List;

public class FilterModel {
    private boolean isFilterByOWLClass;
    private boolean isFilterByProfile;

    private List<FilterModelListener> filterModelListeners;

    public FilterModel() {
        filterModelListeners = new ArrayList<>();
        isFilterByOWLClass = false;
        isFilterByProfile = false;
    }

    public void addFilterModelListener(FilterModelListener listener) {
        filterModelListeners.add(listener);
    }

    public void removeFilterModelListener(FilterModelListener listener) {
        filterModelListeners.remove(listener);
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


}