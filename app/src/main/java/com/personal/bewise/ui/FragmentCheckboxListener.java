package com.personal.bewise.ui;

import android.widget.CompoundButton;

public class FragmentCheckboxListener implements CompoundButton.OnCheckedChangeListener {

    private CustomListFragment parent;

    private int itemId = 0;

    public FragmentCheckboxListener(CustomListFragment parent, int itemId) {
        this.parent = parent;
        this.itemId = itemId;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        parent.setCheckBoxSelections(itemId, isChecked);
    }

}
