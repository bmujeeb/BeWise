package com.personal.bewise.ui.budget;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.personal.bewise.R;
import com.personal.bewise.database.BudgetData;
import com.personal.bewise.database.TransactionsTable;
import com.personal.bewise.ui.CustomListFragment;
import com.personal.bewise.ui.FragmentCheckboxListener;
import com.personal.bewise.utils.DateUtilities;

import java.util.List;

/**
 * @author bilal
 */

public class BudgetListView extends ArrayAdapter<BudgetData> {

    /** Layout Inflator  */
    private LayoutInflater _layoutInflater;
    /**    */
    private List<BudgetData> _transactions;
    /**    */
    private int _viewResourceId;
    /**    */
    private CustomListFragment _parentFragment;

    /**
     * @param context
     * @param textViewResourceId
     * @param transactions
     * @param parent
     */
    public BudgetListView(Context context, int textViewResourceId, List<BudgetData> transactions, CustomListFragment parent) {
        super(context, textViewResourceId, transactions);
        this._transactions = transactions;
        this._viewResourceId = textViewResourceId;
        this._parentFragment = parent;

    }

    @Override
    public int getCount() {
        return _transactions.size();
    }

    @Override
    public BudgetData getItem(int position) {
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
            vh.name = (TextView) convertView.findViewById(R.id.name);
            vh.date = (TextView) convertView.findViewById(R.id.date);
            vh.amountAllocated = (TextView) convertView.findViewById(R.id.amount);
            vh.amountUtilized = (TextView) convertView.findViewById(R.id.amount_utilized);
            vh.recurringPeriod = (TextView) convertView.findViewById(R.id.recurring_period);
            vh.itemsSelectCheckBox = (CheckBox) convertView.findViewById(R.id.items_select_checkbox);
            vh.itemsSelectCheckBox.setOnCheckedChangeListener(new FragmentCheckboxListener(_parentFragment, position));
            convertView.setTag(vh);

        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        BudgetData budget = _transactions.get(position);
        vh.name.setText(budget.getBudgetName());
        vh.date.setText(budget.getBudgetDate());
        vh.amountAllocated.setText(Double.toString(budget.getBudgetAmount()));

        TransactionsTable td = new TransactionsTable(getContext());
        double utilizedAmount = td.getBudgetUtilizedAmount(budget.getBudgetName(), budget.getBudgetRecurrencePeriod(), budget.getBudgetStartDate(),
                DateUtilities.getCurrentDate());
        vh.amountUtilized.setText(Double.toString(utilizedAmount));
        vh.recurringPeriod.setText(budget.getBudgetRecurrencePeriod());

        if (position % 2 == 1) {
            convertView.setBackgroundColor(parent.getResources().getColor(R.color.LightBlue));
        }

        return convertView;
    }

    static class ViewHolder {
        TextView name;
        TextView date;
        TextView amountAllocated;
        TextView amountUtilized;
        TextView recurringPeriod;
        CheckBox itemsSelectCheckBox;
    }
}