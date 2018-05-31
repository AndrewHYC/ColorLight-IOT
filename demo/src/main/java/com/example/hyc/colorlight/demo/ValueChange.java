package com.example.hyc.colorlight.demo;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Created by hyc on 18-5-31.
 *
 * 此类用于监听数值的标志量变化
 */

public class ValueChange {

//    ValueChange newChange = new ValueChange();
//    newChange.addPropertyChangeListener(new PropertyChangeListener listener) {
//        @Override
//        public void propertyChange(PropertyChangeEvent evt) {
//            //What you want to do
//        }
//        }

    private PropertyChangeSupport changes = new PropertyChangeSupport(this);
    private boolean colorChanged = false;

    public boolean getColorChanged() {
        return colorChanged;
    }

    public void setColorChanged(boolean colorChanged) {
        boolean oldColorChanged = colorChanged;
        this.colorChanged = colorChanged;
        changes.firePropertyChange("color", oldColorChanged, this.colorChanged);
    }


    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changes.removePropertyChangeListener(listener);
    }

}
