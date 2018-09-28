package edu.ucdenver.ccp.knowtator.model;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class SelectionModel implements CaretListener {
    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    private int start;
    private int end;

    public SelectionModel() {
        start = 0;
        end = 0;
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        setStart(Math.min(e.getDot(), e.getMark()));
        setEnd(Math.max(e.getDot(), e.getMark()));
    }

}
