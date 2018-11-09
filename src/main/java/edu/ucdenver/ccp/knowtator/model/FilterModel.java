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

    public boolean isFilter(String filter) {
        switch (filter) {
            case PROFILE:
                return isFilterByProfile;
            case OWLCLASS:
                return isFilterByOWLClass;
        }
        return false;
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