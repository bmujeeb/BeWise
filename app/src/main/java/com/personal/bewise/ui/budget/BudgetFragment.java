package com.personal.bewise.ui.budget;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.personal.bewise.R;
import com.personal.bewise.database.BudgetData;
import com.personal.bewise.database.BudgetTable;
import com.personal.bewise.database.TransactionsData;
import com.personal.bewise.database.TransactionsTable;
import com.personal.bewise.ui.CustomListFragment;
import com.personal.bewise.ui.transactions.TransactionsListView;
import com.personal.bewise.utils.DateUtilities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author bilal
 */

public class BudgetFragment extends CustomListFragment {
    /** */
    private ImageButton _addBudgetButton;

    /** */
    private List<BudgetData> _budgetsList;

    private Map<Integer, Boolean> _checkState;

    private ImageButton _editButton;

    private ImageButton _deleteButton;

    private TransactionsListView _transactionsListView;

    private BudgetListView _budgetListView;

    private int _selectedItem = 0;

    private Context _context;

    BudgetTable _budgetTable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(this.getClass().toString(), "onCreateView(LayoutInflater...: View created.");
        View view = inflater.inflate(R.layout.budget, container, false);
        _addBudgetButton = (ImageButton) view.findViewById(R.id.add_budget_button);
        _addBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                BudgetDialog editNameDialog = new BudgetDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable("HANDLER", new DialogDismisselHandler());
                bundle.putString("DIALOG_TITLE", "Add Budget");
                bundle.putString("DIALOG_MODE", "NEW");
                bundle.putSerializable("BUDGET", null);
                editNameDialog.setArguments(bundle);
                editNameDialog.show(fm, "add_budget_dialog");
            }
        });

        _editButton = (ImageButton) view.findViewById(R.id.edit_button);
        _editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                BudgetDialog editNameDialog = new BudgetDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable("HANDLER", new DialogDismisselHandler());
                bundle.putSerializable("DIALOG_TITLE", "Edit Budget");
                bundle.putSerializable("DIALOG_MODE", "EDIT");
                bundle.putSerializable("BUDGET", _budgetsList.get(_selectedItem));
                editNameDialog.setArguments(bundle);
                editNameDialog.show(fm, "add_budget_dialog");
            }
        });

        _editButton.setEnabled(false); // initial state
        _deleteButton = (ImageButton) view.findViewById(R.id.delete_button);
        _deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BudgetTable budgetTable = new BudgetTable(_context);
                Iterator<Map.Entry<Integer, Boolean>> iterator = _checkState.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, Boolean> item = iterator.next();
                    budgetTable.deleteBudget(_budgetsList.get(item.getKey()).getBudgetName());
                    _budgetsList.clear();
                    _budgetsList = budgetTable.getAllBudgets();
                    iterator.remove();
                }
                updateActivity();
            }
        });
        _deleteButton.setEnabled(false); // initial state
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(this.getClass().toString(), "onActivityCreated(Bundle...: Activity created.");
        super.onActivityCreated(savedInstanceState);
        _context = getActivity();
        _budgetTable = new BudgetTable(_context);
        updateActivity();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(this.getClass().toString(), "onListItemClick(ListView l, View v, int position, long id) - Budget item selected.");
        super.onListItemClick(l, v, position, id);
        // TODO: Get the start date of the budget and calculate the transactions in between
        String budgetName = _budgetsList.get(position).getBudgetName();
        String budRecurringPeriod = _budgetsList.get(position).getBudgetRecurrencePeriod();
        TransactionsTable transactionsTable = new TransactionsTable(_context);

        List<TransactionsData> budgets = transactionsTable.getTransactionsInBudget(budgetName,
                budRecurringPeriod, _budgetsList.get(position).getBudgetDate(),
                DateUtilities.getCurrentDate());

        _transactionsListView = new TransactionsListView(getActivity(), R.layout.transactions_out, budgets, this);

        setListAdapter(_transactionsListView);
        _addBudgetButton.setEnabled(false);
        _addBudgetButton.setVisibility(View.GONE);
    }

    private void updateActivity() {
        // TODO: calculate the budget utilization here and send it to listview
        _checkState = new HashMap<Integer, Boolean>();
        _budgetsList = _budgetTable.getAllBudgets();
        _budgetListView = new BudgetListView(_context, R.layout.budget_out, _budgetsList,
                this);
        setListAdapter(_budgetListView);
    }

    /**
     * @param itemId
     * @param checkedState
     */
    public void setCheckBoxSelections(int itemId, boolean checkedState) {
        _selectedItem = itemId;
        if (checkedState) {
            _checkState.put(itemId, true);
        } else {
            _checkState.remove(itemId);
        }
        if (_checkState.size() > 0) {
            _deleteButton.setEnabled(true);
            if (_checkState.size() < 2) {
                _editButton.setEnabled(true);
            } else {
                _editButton.setEnabled(false);
            }
        } else {
            _editButton.setEnabled(false);
            _deleteButton.setEnabled(false);
        }
    }

    @Override
    public void onResume() {
        //TODO: FIXIT
        Log.d(this.getClass().toString(), "Back button called.");
        super.onResume();
    }

    private class DialogDismisselHandler extends Handler implements Serializable {
        private static final long serialVersionUID = -7708712287964495953L;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            updateActivity();
        }
    }

}
