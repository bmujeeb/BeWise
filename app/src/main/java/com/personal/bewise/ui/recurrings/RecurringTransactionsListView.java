package com.personal.bewise.ui.recurrings;

import android.content.Context;
import android.graphics.Color;
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

public class RecurringTransactionsListView extends ArrayAdapter<TransactionsData> {

    private CustomListFragment _parent;
    private LayoutInflater _layoutInflater;
    private List<TransactionsData> _transactions;
    private int _viewResourceId;

    public RecurringTransactionsListView(Context context, int textViewResourceId, List<TransactionsData> transactions, CustomListFragment parent) {
        super(context, textViewResourceId, transactions);
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
            vh.recurringPeriod = (TextView) convertView.findViewById(R.id.recurring_period);
            vh.itemsSelectCheckBox = (CheckBox) convertView.findViewById(R.id.items_select_checkbox);
            vh.itemsSelectCheckBox.setOnCheckedChangeListener(new FragmentCheckboxListener(_parent, position));
            vh.listViewLayout = (LinearLayout) convertView.findViewById(R.id.list_item_layout);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        // Change Color of income and expenses transactions
        if (_transactions.get(position).isIncome()) {
            vh.listViewLayout.setBackgroundColor(Color.parseColor("#2E7D32"));
        } else {
            vh.listViewLayout.setBackgroundColor(Color.parseColor("#C62828"));
        }

        vh.date.setText(_transactions.get(position).getNextDueDate());
        vh.category.setText(_transactions.get(position).getCategory());
        vh.amount.setText(Double.toString(_transactions.get(position).getAmount()));
        vh.recurringPeriod.setText(_transactions.get(position).getRecurringPeriod());

        return convertView;
    }

    static class ViewHolder {
        TextView date;
        TextView category;
        TextView amount;
        TextView recurringPeriod;
        CheckBox itemsSelectCheckBox;
        LinearLayout listViewLayout;
    }

}
