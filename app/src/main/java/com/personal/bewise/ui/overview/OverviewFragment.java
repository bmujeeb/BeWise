package com.personal.bewise.ui.overview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.personal.bewise.R;
import com.personal.bewise.database.BudgetData;
import com.personal.bewise.database.BudgetTable;
import com.personal.bewise.database.TransactionsTable;
import com.personal.bewise.ui.CustomListFragment;
import com.personal.bewise.utils.DateUtilities;
import com.personal.bewise.utils.SearchableItems;

import java.util.List;

/**
 * TODO: Overview should not display transactions_out.xml
 * <p/>
 * TODO: Overview must show
 * <p/>
 * a) The balance of account.
 * <p/>
 * b) Running budgets overview.
 * <p/>
 * c) Latest transactions from history.
 * <p/>
 * TODO: Add account balance.
 * <p/>
 * TODO: Remove checkbox
 *
 * @author bilal
 */

public class OverviewFragment extends CustomListFragment {

    private TextView _accountBalance;

    private TextView _incomeLast7Days;

    private TextView _incomeLastOneMonth;

    private TextView _expenseLast7Days;

    private TextView _expenseLastOneMonth;

    private List<BudgetData> _budgetsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.overview, container, false);

        setOverallBalance(view);

        setIncomeBalanceLast7Days(view);

        setIncomeBalanceLastMonth(view);

        setExpenseBalanceLast7Days(view);

        setExpenseBalanceLastMonth(view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateActivity();
    }

    /** */
    private void updateActivity() {
        BudgetTable budgetTable = new BudgetTable(getActivity());
        _budgetsList = budgetTable.getAllBudgets();
        setListAdapter(new OverviewListView(getActivity(), R.layout.overview_out, _budgetsList));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO: When an budget item is selected, open a dialog and shows all
        // the items related to that budget
    }

    @Override
    public void setCheckBoxSelections(int itemId, boolean checkedState) {
        // TODO Auto-generated method stub
    }

    private void setOverallBalance(View view) {
        _accountBalance = (TextView) view.findViewById(R.id.balance);
        TransactionsTable transactionsTable = new TransactionsTable(getActivity());
        _accountBalance.setText(Double.toString(transactionsTable.getTransactionsBalance(SearchableItems.INCOME_AND_EXPENSES, "")));
    }

    private void setIncomeBalanceLast7Days(View view) {
        _incomeLast7Days = (TextView) view.findViewById(R.id.income_last_7_days);
        String date = DateUtilities.addDays(DateUtilities.getCurrentDate(), -7);
        TransactionsTable transactionsTable = new TransactionsTable(getActivity());
        _incomeLast7Days.setText(Double.toString(transactionsTable.getTransactionsBalance(SearchableItems.INCOME, date)));
    }

    private void setIncomeBalanceLastMonth(View view) {
        _incomeLastOneMonth = (TextView) view.findViewById(R.id.income_last_one_month);
        String date = DateUtilities.addMonths(DateUtilities.getCurrentDate(), -1);
        TransactionsTable transactionsTable = new TransactionsTable(getActivity());
        _incomeLastOneMonth.setText(Double.toString(transactionsTable.getTransactionsBalance(SearchableItems.INCOME, date)));
    }

    private void setExpenseBalanceLast7Days(View view) {
        _expenseLast7Days = (TextView) view.findViewById(R.id.expense_last_7_days);
        String date = DateUtilities.addDays(DateUtilities.getCurrentDate(), -7);
        TransactionsTable transactionsTable = new TransactionsTable(getActivity());
        _expenseLast7Days.setText(Double.toString(transactionsTable.getTransactionsBalance(SearchableItems.EXPENSES, date)));
    }

    private void setExpenseBalanceLastMonth(View view) {
        _expenseLastOneMonth = (TextView) view.findViewById(R.id.expense_last_one_month);
        String date = DateUtilities.addMonths(DateUtilities.getCurrentDate(), -1);
        TransactionsTable transactionsTable = new TransactionsTable(getActivity());
        _expenseLastOneMonth.setText(Double.toString(transactionsTable.getTransactionsBalance(SearchableItems.EXPENSES, date)));
    }

}