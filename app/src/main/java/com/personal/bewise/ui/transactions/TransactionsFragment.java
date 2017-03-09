package com.personal.bewise.ui.transactions;

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

import com.personal.bewise.BeWiseConstants;
import com.personal.bewise.R;
import com.personal.bewise.database.TransactionsData;
import com.personal.bewise.database.TransactionsTable;
import com.personal.bewise.ui.CustomListFragment;
import com.personal.bewise.ui.details.TransactionDetailsDialog;
import com.personal.bewise.utils.TransactionDetailsMode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Bilal Mujeeb
 */

public class TransactionsFragment extends CustomListFragment {
    /**
     * Add new income button.
     */
    private ImageButton _addIncomeButton;
    /**
     * Add new expense button.
     */
    private ImageButton _addExpenseButton;
    /**
     * Edit income or expense button.
     */
    private ImageButton _editButton;
    /**
     * Delete income or expense button.
     */
    private ImageButton _deleteButton;
    /**
     * Index of selected item in the list.
     */
    private int _selectedItem = 0;
    /**
     * List containing transactions data..
     */
    private List<TransactionsData> _transactionsList;
    /**
     * Map indicating which item in the UI is selected for delete (one or more selection) or edit (single item).
     */
    private Map<Integer, Boolean> _checkState;
    /**
     * Application context.
     */
    private Context _context;

    /**
     * Default constructor.
     */
    public TransactionsFragment() {

    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
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
                Boolean isIncome = data.isIncome();

                TransactionDialog editNameDialog = new TransactionDialog();
                Bundle args = new Bundle();
                args.putSerializable("HANDLER", new DialogDismissalHandler());
                args.putString("DIALOG_TITLE", isIncome ? BeWiseConstants.EDIT_INCOME : BeWiseConstants.EDIT_EXPENSE);
                args.putString("DIALOG_MODE", BeWiseConstants.EDIT_DIALOG_MODE);
                args.putSerializable("TRANSACTION_DATA", data);
                args.putBoolean("IS_INCOME", isIncome);
                editNameDialog.setArguments(args);

                editNameDialog.show(fm, BeWiseConstants.ADD_TRANSACTION_DIALOG_TAG);
            }
        });
        _editButton.setEnabled(false);

        _deleteButton = (ImageButton) view.findViewById(R.id.delete_button);
        _deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransactionsTable transactionsTable = new TransactionsTable(_context);

                Iterator<Map.Entry<Integer, Boolean>> iterator = _checkState.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, Boolean> item = iterator.next();
                    TransactionsData data = _transactionsList.get(item.getKey());
                    transactionsTable.deleteSingleTransaction(data.getTransactionID());
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
                TransactionDialog editNameDialog = new TransactionDialog();
                Bundle args = new Bundle();
                args.putSerializable("HANDLER", new DialogDismissalHandler());
                args.putString("DIALOG_TITLE", BeWiseConstants.ADD_INCOME);
                args.putString("DIALOG_MODE", BeWiseConstants.NEW_DIALOG_MODE);
                args.putSerializable("TRANSACTION_DATA", null);
                args.putBoolean("IS_INCOME", true);
                editNameDialog.setArguments(args);
                editNameDialog.show(fm, BeWiseConstants.ADD_INCOME_DIALOG_TAG);
            }
        });

        _addExpenseButton = (ImageButton) view.findViewById(R.id.add_expense_button);
        _addExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                TransactionDialog editNameDialog = new TransactionDialog();
                Bundle args = new Bundle();
                args.putSerializable("HANDLER", new DialogDismissalHandler());
                args.putString("DIALOG_TITLE", BeWiseConstants.ADD_EXPENSE);
                args.putString("DIALOG_MODE", BeWiseConstants.NEW_DIALOG_MODE);
                args.putSerializable("TRANSACTION_DATA", null);
                args.putBoolean("IS_INCOME", false);
                editNameDialog.setArguments(args);
                editNameDialog.show(fm, BeWiseConstants.ADD_EXPENSE_DIALOG_TAG);
            }
        });
        return view;
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this._context = getActivity();
        updateActivity();
    }

    /**
     *
     * @param l
     * @param v
     * @param position
     * @param id
     */
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

    /**
     * Update activity.
     */
    private void updateActivity() {
        _checkState = new HashMap<Integer, Boolean>();
        _transactionsList = getAllTransactions();
        setListAdapter(new TransactionsListView(_context, R.layout.transactions_out, _transactionsList, this));
    }

    /**
     * Read from database and populate the Income table.
     *
     * @return
     */
    private List<TransactionsData> getAllTransactions() {
        TransactionsTable transactionsTable = new TransactionsTable(_context);
        return transactionsTable.getAllTransactions();
    }

    /**
     *
     * @param itemId
     * @param checkedState
     */
    @Override
    public void setCheckBoxSelections(int itemId, boolean checkedState) {
        Log.d(BeWiseConstants.LOG_TAG, "setCheckBoxSelections( "+ itemId+", " +checkedState+")");
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

    /**
     *
     */
    private class DialogDismissalHandler extends Handler implements Serializable {

        /**
         * The constant UID.
         */
        private static final long serialVersionUID = 4616093033484240050L;

        /**
         *
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            updateActivity();
        }
    }

}
