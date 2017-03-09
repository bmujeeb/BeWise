package com.personal.bewise.ui.transactions;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.personal.bewise.R;
import com.personal.bewise.database.TransactionsData;
import com.personal.bewise.ui.CustomListFragment;
import com.personal.bewise.ui.FragmentCheckboxListener;

import java.util.List;

/**
 * TODO: Align the UI items.
 * <p/>
 * TODO: Rather than adding information in each section. Provide meaningful information. The cell should at least
 * contain date, amount, category, budget and if recursive, should show when was last accounted and when next is due.
 * <p/>
 * TODO: Remove the date, category and amount header and replace it with something like recent income/expense
 * transactions.
 * <p/>
 * TODO: Incomes will be shown in green while expenses will be shown in red.
 * <p/>
 * TODO: Add functionality to edit and delete buttons.
 * <p/>
 * TODO: (Improvement) Move the buttons on the top of the screen. Select single item to activate edit. One or more to
 * activate delete button.
 *
 * @author bilal
 */

public class TransactionsListView extends ArrayAdapter<TransactionsData> {

    private LayoutInflater _layoutInflater;

    private List<TransactionsData> _transactions;

    private int _viewResourceId;

    private CustomListFragment _parent;

    public TransactionsListView(Context context, int textViewResourceId, List<TransactionsData> transactions, CustomListFragment parent) {
        super(context, textViewResourceId, transactions);
        Log.d(this.getClass().toString(), "TransactionsListView(....");
        this._transactions = transactions;
        this._viewResourceId = textViewResourceId;
        this._parent = parent;
    }

    @Override
    public int getCount() {
        return _transactions.size();
    }

    @Override
    public TransactionsData getItem(int position) {
        return _transactions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder vh = new ViewHolder();

        if (convertView == null) {
            _layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = _layoutInflater.inflate(_viewResourceId, null);
            vh.date = (TextView) convertView.findViewById(R.id.date);
            vh.category = (TextView) convertView.findViewById(R.id.category);
            vh.amount = (TextView) convertView.findViewById(R.id.amount);
            vh.itemsSelectCheckBox = (CheckBox) convertView.findViewById(R.id.items_select_checkbox);
            vh.listViewLayout = (LinearLayout) convertView.findViewById(R.id.list_item_layout);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
            vh.itemsSelectCheckBox.setOnCheckedChangeListener(null);
        }

        if (_transactions.get(position).isIncome()) {
            vh.listViewLayout.setBackgroundColor(Color.parseColor("#2E7D32")); // MediumSeaGreen
        } else {
            vh.listViewLayout.setBackgroundColor(Color.parseColor("#C62828")); // IndianRed
        }

        vh.date.setText(_transactions.get(position).getStartDate());
        vh.category.setText(_transactions.get(position).getCategory());
        vh.amount.setText(Double.toString(_transactions.get(position).getAmount()));
        vh.itemsSelectCheckBox.setOnCheckedChangeListener(new FragmentCheckboxListener(_parent, position));

        return convertView;
    }

    static class ViewHolder {
        TextView date;
        TextView category;
        TextView amount;
        CheckBox itemsSelectCheckBox;
        LinearLayout listViewLayout;
    }

}
