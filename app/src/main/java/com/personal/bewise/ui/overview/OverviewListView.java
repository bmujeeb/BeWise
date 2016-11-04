package com.personal.bewise.ui.overview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.personal.bewise.R;
import com.personal.bewise.database.BudgetData;
import com.personal.bewise.database.TransactionsTable;
import com.personal.bewise.utils.DateUtilities;

import java.util.List;

/**
 * TODO: Align the UI items.
 * <p/>
 * TODO: Rather than adding information in each section. Provide meaningful information. The cell should at least contain date, amount, category, budget and if
 * recursive, should show when was last accounted and when next is due.
 * <p/>
 * TODO: Remove the date, category and amount header and replace it with something like recent income/expense transactions.
 * <p/>
 * TODO: Incomes will be shown in green while expenses will be shown in red.
 * <p/>
 * TODO: Add functionality to edit and delete buttons.
 * <p/>
 * TODO: (Improvement) Move the buttons on the top of the screen. Select single item to activate edit. One or more to activate delete button.
 *
 * @author bilal
 */

public class OverviewListView extends ArrayAdapter<BudgetData> {

    private LayoutInflater _layoutInflater;

    private List<BudgetData> _budgetsList;

    private int _viewResourceId;

    public OverviewListView(Context context, int textViewResourceId, List<BudgetData> budgetsList) {
        super(context, textViewResourceId, budgetsList);

        this._budgetsList = budgetsList;
        this._viewResourceId = textViewResourceId;
    }

    @Override
    public int getCount() {
        return _budgetsList.size();
    }

    @Override
    public BudgetData getItem(int position) {
        return _budgetsList.get(position);
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
            vh.budget = (TextView) convertView.findViewById(R.id.budget_name);
            vh.amountAllocated = (TextView) convertView.findViewById(R.id.budget_amount_allocated);
            vh.amountUtilized = (TextView) convertView.findViewById(R.id.budget_amount_spent);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        BudgetData budget = _budgetsList.get(position);

        vh.budget.setText(budget.getBudgetName());
        vh.amountAllocated.setText(Double.toString(budget.getBudgetAmount()));
        TransactionsTable td = new TransactionsTable(getContext());
        double utilizedAmount = td.getBudgetUtilizedAmount(budget.getBudgetName(), budget.getBudgetRecurrencePeriod(), budget.getBudgetStartDate(),
                DateUtilities.getCurrentDate());
        vh.amountUtilized.setText(Double.toString(utilizedAmount));

        return convertView;
    }

    static class ViewHolder {
        TextView budget;
        TextView amountAllocated;
        TextView amountUtilized;
    }

}
