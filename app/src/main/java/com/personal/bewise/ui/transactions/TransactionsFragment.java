package com.personal.bewise.ui.transactions;

import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.personal.bewise.BeWiseConstants;
import com.personal.bewise.R;
import com.personal.bewise.database.TransactionsData;
import com.personal.bewise.database.TransactionsTable;
import com.personal.bewise.ui.CustomListFragment;
import com.personal.bewise.ui.details.TransactionDetailsDialog;
import com.personal.bewise.utils.TransactionDetailsMode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TransactionsFragment extends CustomListFragment {

    private ImageButton _addIncomeButton;
    private ImageButton _addExpenseButton;
    private ImageButton _editButton;
    private ImageButton _deleteButton;
    private int _selectedItem = 0;
    private List<TransactionsData> _transactionsList;
    private Map<Integer, Boolean> _checkState;

    public TransactionsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transactions, container, false);
        setRetainInstance(true);

        _editButton = (ImageButton) view.findViewById(R.id.edit_button);
        _editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                TransactionsData data = _transactionsList.get(_selectedItem);
                boolean isIncome = data.isIncome();
                String title = isIncome ? BeWiseConstants.EDIT_INCOME : BeWiseConstants.EDIT_EXPENSE;
                // TODO: Use bundle pass the arguments instead of passing it to constructor which destroys
                // TODO: the object when orientation is changed
                TransactionDialog editNameDialog = new TransactionDialog(new DialogDismissalHandler(), title, BeWiseConstants.EDIT_DIALOG_MODE, data, isIncome);
                editNameDialog.show(fm, BeWiseConstants.ADD_TRANSACTION_DIALOG_TAG);
            }
        });
        _editButton.setEnabled(false);

        _deleteButton = (ImageButton) view.findViewById(R.id.delete_button);
        _deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransactionsTable transactionsTable = new TransactionsTable(getActivity());

                Iterator<Map.Entry<Integer, Boolean>> iterator = _checkState.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, Boolean> item = iterator.next();
                    TransactionsData data = _transactionsList.get(item.getKey());
                    transactionsTable.deleteSingleTransaction(data.getTransactionID());
                    _transactionsList.clear();
                    _transactionsList = transactionsTable.getAllTransactions();
                    iterator.remove();
                }
                updateActivity();
            }
        });
        _deleteButton.setEnabled(false);

        _addIncomeButton = (ImageButton) view.findViewById(R.id.add_income_button);
        _addIncomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                TransactionDialog editNameDialog = new TransactionDialog(new DialogDismissalHandler(), BeWiseConstants.ADD_INCOME,
                        BeWiseConstants.NEW_DIALOG_MODE, null, true);
                editNameDialog.show(fm, BeWiseConstants.ADD_INCOME_DIALOG_TAG);
            }
        });

        _addExpenseButton = (ImageButton) view.findViewById(R.id.add_expense_button);
        _addExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                TransactionDialog editNameDialog = new TransactionDialog(new DialogDismissalHandler(), BeWiseConstants.ADD_EXPENSE,
                        BeWiseConstants.NEW_DIALOG_MODE, null, false);
                editNameDialog.show(fm, BeWiseConstants.ADD_EXPENSE_DIALOG_TAG);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateActivity();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        FragmentManager fm = getFragmentManager();
        TransactionsData transaction = (TransactionsData) l.getItemAtPosition(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("TRANSACTION", transaction);
        if (transaction.isIncome()) {
            TransactionDetailsDialog detailsDialog = new TransactionDetailsDialog();
            bundle.putSerializable("MODE", TransactionDetailsMode.INCOME);
            detailsDialog.setArguments(bundle);
            detailsDialog.show(fm, BeWiseConstants.TRANSACTION_DETAILS_DIALOG_TAG);
        } else {
            TransactionDetailsDialog detailsDialog = new TransactionDetailsDialog();
            bundle.putSerializable("MODE", TransactionDetailsMode.EXPENSE);
            detailsDialog.setArguments(bundle);
            detailsDialog.show(fm, BeWiseConstants.TRANSACTION_DETAILS_DIALOG_TAG);
        }

    }

    /** */
    private void updateActivity() {
        _checkState = new HashMap<Integer, Boolean>();
        _transactionsList = getAllTransactions();
        setListAdapter(new TransactionsListView(getActivity(), R.layout.transactions_out, _transactionsList, this));
    }

    /**
     * Read from database and populate the Income table.
     *
     * @return
     */
    private List<TransactionsData> getAllTransactions() {
        TransactionsTable transactionsTable = new TransactionsTable(getActivity());
        return transactionsTable.getAllTransactions();
    }

    @Override
    public void setCheckBoxSelections(int itemId, boolean checkedState) {
        _selectedItem = itemId;
        if (checkedState) {
            _checkState.put(itemId, checkedState);
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

    private class DialogDismissalHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            updateActivity();
        }
    }

}
