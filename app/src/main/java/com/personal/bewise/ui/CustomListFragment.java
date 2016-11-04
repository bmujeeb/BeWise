package com.personal.bewise.ui;

import android.app.ListFragment;

/**
 * @author bilal
 */
public abstract class CustomListFragment extends ListFragment {
    public CustomListFragment() {

    }

    public abstract void setCheckBoxSelections(int itemId, boolean checkedState);
}
