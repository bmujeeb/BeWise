package com.personal.bewise.ui.budget;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.personal.bewise.BeWiseConstants;
import com.personal.bewise.R;
import com.personal.bewise.database.BudgetData;
import com.personal.bewise.database.BudgetTable;
import com.personal.bewise.database.PendingTransactionsTable;
import com.personal.bewise.database.RecurrenceTable;
import com.personal.bewise.database.TransactionsData;
import com.personal.bewise.database.TransactionsTable;
import com.personal.bewise.ui.CustomListFragment;
import com.personal.bewise.ui.details.TransactionDetailsDialog;
import com.personal.bewise.ui.transactions.TransactionsListView;
import com.personal.bewise.utils.DateUtilities;
import com.personal.bewise.utils.TransactionDetailsMode;

import java.io.Serializable;
import java.util.ArrayList;
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

    private List<TransactionsData> _budgetTransaction;

    private Map<Integer, Boolean> _checkState;

    private ImageButton _editButton;

    private ImageButton _deleteButton;

    // Default listview when the budget fragment is created
    private BudgetListView _budgetListView;

    /**
     * listview containing transactions in a listview
     */
    private TransactionsListView _transactionsListView;

    /**
     *
     */
    private int _selectedCheckboxItem = 0;

    /**
     *
     */
    private Context _context;

    /**
     *
     */
    private BudgetTable _budgetTable;

    /**
     *
     */
    private int _selectedIndex;

    /**
     *
     */
    private String _currentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(this.getClass().toString(), "onCreateView(LayoutInflater...: View created.");
        View view = inflater.inflate(R.layout.budget, container, false);
        _addBudgetButton = (ImageButton) view.findViewById(R.id.add_budget_button);
        _addBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(this.getClass().toString(), "onClick: Add budget button clicked");
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
                Log.d(this.getClass().toString(), "onClick: Edit budget button clicked");
                FragmentManager fm = getFragmentManager();
                BudgetDialog editNameDialog = new BudgetDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable("HANDLER", new DialogDismisselHandler());
                bundle.putSerializable("DIALOG_TITLE", "Edit Budget");
                bundle.putSerializable("DIALOG_MODE", "EDIT");
                bundle.putSerializable("BUDGET", _budgetsList.get(_selectedCheckboxItem));
                editNameDialog.setArguments(bundle);
                editNameDialog.show(fm, "add_budget_dialog");
            }
        });

        _editButton.setEnabled(false); // initial state
        _deleteButton = (ImageButton) view.findViewById(R.id.delete_button);
        _deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(this.getClass().toString(), "onClick: Delete budget button clicked");
                // FIXED ISSUE: If the transactions in a budget are displayed and deleted, it crashes the application.
                // Reason been, delete action is made on budgets list, not on the transactions in a budget.
                if (_transactionsListView == null) {
                    Log.d(this.getClass().toString(), "Selected budget(s) will be deleted.");
                    BudgetTable budgetTable = new BudgetTable(_context);
                    TransactionsTable transactionsTable = new TransactionsTable(_context);
                    RecurrenceTable recurrenceTable = new RecurrenceTable(_context);
                    PendingTransactionsTable pendingTransactionsTable = new PendingTransactionsTable(_context);
                    Iterator<Map.Entry<Integer, Boolean>> iterator = _checkState.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Integer, Boolean> item = iterator.next();
                        String budgetName = _budgetsList.get(item.getKey()).getBudgetName();
                        budgetTable.deleteBudget(budgetName);
                        transactionsTable.updateTransaction("budget", budgetName, null);
                        recurrenceTable.updateRecurringTransaction("budget", budgetName, null);
                        pendingTransactionsTable.updatePendingTransaction("budget", budgetName, null);
                    }
                    updateActivity();
                } else {
                    Log.d(this.getClass().toString(), "Selected transation(s) associated with a budget will be de-referenced from the budget..");
                    // Otherwise remove reference of the budget from the selected transactions.
                    TransactionsTable transactionsTable = new TransactionsTable(_context);
                    Iterator<Map.Entry<Integer, Boolean>> iterator = _checkState.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Integer, Boolean> item = iterator.next();
                        TransactionsData transaction = _budgetTransaction.get(item.getKey());
                        transaction.setBudget(null);
                        transactionsTable.updateTransaction(transaction);
                    }
                    updateBudgetTransactionsActivity(_selectedIndex);
                }
            }
        });
        _deleteButton.setEnabled(false); // initial state

        if (savedInstanceState != null) {
            //TODO: Find and fix with a better solution
            Log.d(this.getClass().toString(), "onCreateView: Fragmnent configuration changed and saved on view recreate or orientation change.");
            _selectedIndex = savedInstanceState.getInt("SELECTED_INDEX");
            _currentView = savedInstanceState.getString("CURRENT_VIEW");
        }
        return view;
    }

    /**
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(this.getClass().toString(), "onActivityCreated(Bundle...: Activity created.");
        super.onActivityCreated(savedInstanceState);
        _context = getActivity();
        _budgetTable = new BudgetTable(_context);
        _checkState = new HashMap<Integer, Boolean>();
        _budgetsList = _budgetTable.getAllBudgets();
        if (null != _currentView && _currentView.equalsIgnoreCase("TRANSACTION_LIST_VIEW")) {
            updateBudgetTransactionsActivity(_selectedIndex);
        } else {
            updateActivity();
        }
    }

    /**
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(this.getClass().toString(), "onSaveInstanceState(.....");
        super.onSaveInstanceState(savedInstanceState);
        if (_budgetTransaction != null) {
            savedInstanceState.putInt("SELECTED_INDEX", _selectedIndex);
            savedInstanceState.putString("CURRENT_VIEW", "TRANSACTION_LIST_VIEW");
        }
    }

    /**
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Restore the view on rotation change
        Log.d(this.getClass().toString(), "onCreate(.....");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(this.getClass().toString(), "onListItemClick(ListView l, View v, int position, long id) - Budget item selected.");
        super.onListItemClick(l, v, position, id);

        _selectedIndex = position;

        if (_transactionsListView == null) {
            // A budget is selected and items related to the budget are available
            Log.d(this.getClass().toString(), "onListItemClick: Open the transactions associated with a budget.");
            updateBudgetTransactionsActivity(position);
        } else {
            // Otherwise open the TransactionDetailsDialog and show the transaction detail
            Log.d(this.getClass().toString(), "onListItemClick: Budget transactions listview is visible and transaction details dialog will appear.");
            FragmentManager fm = getFragmentManager();
            TransactionsData transaction = _budgetTransaction.get(position);
            Bundle bundle = new Bundle();
            bundle.putSerializable("TRANSACTION", transaction);
            TransactionDetailsDialog detailsDialog = new TransactionDetailsDialog();
            if (transaction.isIncome()) {
                bundle.putSerializable("MODE", TransactionDetailsMode.INCOME);
                detailsDialog.setArguments(bundle);
                detailsDialog.show(fm, BeWiseConstants.TRANSACTION_DETAILS_DIALOG_TAG);
            } else {
                bundle.putSerializable("MODE", TransactionDetailsMode.EXPENSE);
                detailsDialog.setArguments(bundle);
                detailsDialog.show(fm, BeWiseConstants.TRANSACTION_DETAILS_DIALOG_TAG);
            }
        }
    }

    private void updateActivity() {
        Log.d(this.getClass().toString(), "updateActivity(): Populate the budgets view with all available budgets.");
        // TODO: calculate the budget utilization here and send it to listview

        _budgetListView = new BudgetListView(_context, R.layout.budget_out, _budgetsList, this);
        setListAdapter(_budgetListView);
    }

    /**
     * @param position
     */
    private void updateBudgetTransactionsActivity(int position) {
        // A budget is selected and items related to the budget are available
        Log.d(this.getClass().toString(), "updateBudgetTransactionsActivity( " + position + " )");
        String budgetName = _budgetsList.get(position).getBudgetName();
        String budRecurringPeriod = _budgetsList.get(position).getBudgetRecurrencePeriod();
        TransactionsTable transactionsTable = new TransactionsTable(_context);

        _budgetTransaction = transactionsTable.getTransactionsInBudget(budgetName,
                budRecurringPeriod, _budgetsList.get(position).getBudgetDate(),
                DateUtilities.getCurrentDate());

        _transactionsListView = new TransactionsListView(getActivity(), R.layout.transactions_out, _budgetTransaction, this);

        setListAdapter(_transactionsListView);
        _addBudgetButton.setEnabled(false);
        _addBudgetButton.setVisibility(View.GONE);
    }

    /**
     * @param itemId
     * @param checkedState
     */
    public void setCheckBoxSelections(int itemId, boolean checkedState) {
        _selectedCheckboxItem = itemId;
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
        Log.d(this.getClass().toString(), "onResume()");
        super.onResume();
        // Back button from budget transaction listview to budgets listview
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.d(this.getClass().toString(), "onResume(): Back button pressed.");
                    if (_transactionsListView != null) {
                        Log.d(this.getClass().toString(), "onResume(): Back button from budget transactions to budgets.");
                        _transactionsListView = null;
                        _budgetTransaction = null;
                        updateActivity();
                    }
                    return true;
                }
                return false;
            }
        });
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
