package com.personal.bewise.ui;

import android.util.Log;
import android.widget.CompoundButton;

import com.personal.bewise.BeWiseConstants;

public class FragmentCheckboxListener implements CompoundButton.OnCheckedChangeListener {

    private CustomListFragment parent;

    private int itemId = 0;

    public FragmentCheckboxListener(CustomListFragment parent, int itemId) {
        this.parent = parent;
        this.itemId = itemId;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(this.getClass().toString(), "onCheckedChanged(...");
        parent.setCheckBoxSelections(itemId, isChecked);
    }

}
