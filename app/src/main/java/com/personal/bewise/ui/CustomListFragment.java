package com.personal.bewise.ui;

import android.app.ListFragment;

import java.io.Serializable;

/**
 * @author bilal
 */
public abstract class CustomListFragment extends ListFragment implements Serializable{

    private static final long serialVersionUID = 8912662212963482085L;

    public CustomListFragment() {

    }

    public abstract void setCheckBoxSelections(int itemId, boolean checkedState);
}
