package edu.ucdenver.ccp.knowtator.model;

import java.util.ArrayList;
import java.util.List;

public class FilterModel implements BaseKnowtatorModel {
    public static final String PROFILE = "profile";
    public static final String OWLCLASS = "owl-class";
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

    public boolean isFilterByProfile() {
        return isFilterByProfile;
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

    public void setFilter(String filter, boolean isFilter) {
        switch (filter) {
            case PROFILE:
                isFilterByProfile = isFilter;
                filterModelListeners.forEach(listener -> listener.profileFilterChanged(isFilterByProfile));
                break;
            case OWLCLASS:
                isFilterByOWLClass = isFilter;
                filterModelListeners.forEach(l -> l.owlClassFilterChanged(isFilterByOWLClass));
                break;
        }
    }
}