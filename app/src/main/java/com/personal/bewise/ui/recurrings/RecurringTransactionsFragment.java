package com.personal.bewise.ui.recurrings;

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
import com.personal.bewise.database.RecurrenceTable;
import com.personal.bewise.database.TransactionsData;
import com.personal.bewise.ui.CustomListFragment;
import com.personal.bewise.ui.details.TransactionDetailsDialog;
import com.personal.bewise.utils.TransactionDetailsMode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * TODO: Fix it...
 *
 * @author bilal
 */

public class RecurringTransactionsFragment extends CustomListFragment {

    private ImageButton _editButton;
    private ImageButton _deleteButton;

    private int _selectedItem = 0;
    private Map<Integer, Boolean> _checkState;
    private List<TransactionsData> _transactions;

    public RecurringTransactionsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recurring_transactions, container, false);
        setRetainInstance(true);
        _editButton = (ImageButton) view.findViewById(R.id.edit_button);
        _editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Impact of deleting recurring transaction would be:
                // 1. The transaction will be edited
                // 2. Only future transactions will be impacted
                // -- OR --
                // TODO: User can select the option to update all previous
                // transactions as well.
                FragmentManager fm = getFragmentManager();
                TransactionsData data = _transactions.get(_selectedItem);
                boolean isIncome = data.isIncome();
                String title = isIncome ? BeWiseConstants.EDIT_INCOME : BeWiseConstants.EDIT_EXPENSE;
                // TransactionDialog editNameDialog = new TransactionDialog(new
                // DialogDismissalHandler(), title,
                // BeWiseConstants.EDIT_DIALOG_MODE, data, isIncome);
                // editNameDialog.show(fm,
                // BeWiseConstants.ADD_TRANSACTION_DIALOG_TAG);
            }
        });
        _editButton.setEnabled(false);

        _deleteButton = (ImageButton) view.findViewById(R.id.delete_button);
        _deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecurrenceTable recurrenceTable = new RecurrenceTable(getActivity());
                Iterator<Map.Entry<Integer, Boolean>> iterator = _checkState.entrySet().iterator();
                while (iterator.hasNext()) {
                    // Impact of deleting recurring transaction would be:
                    // 1. The transaction will be removed
                    // 2. There will be no future recurring transaction under
                    // that head
                    // -- OR --
                    // TODO: User can select the option to delete all previous
                    // transactions as well.
                    Map.Entry<Integer, Boolean> item = iterator.next();
                    TransactionsData data = _transactions.get(item.getKey());
                    recurrenceTable.deleteRecurringTransaction(data.getRecurringID());
                    _transactions.clear();
                    _transactions = recurrenceTable.getAllRecurringTransactions();
                    iterator.remove();
                }
                updateActivity();
            }
        });
        _deleteButton.setEnabled(false);

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
        TransactionDetailsDialog detailsDialog = new TransactionDetailsDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("MODE", TransactionDetailsMode.RECURRING);
        bundle.putSerializable("TRANSACTION", transaction);
        detailsDialog.setArguments(bundle);
        detailsDialog.show(fm, BeWiseConstants.TRANSACTION_DETAILS_DIALOG_TAG);
    }

    /** */
    private void updateActivity() {
        _checkState = new HashMap<Integer, Boolean>();
        RecurrenceTable recurringTable = new RecurrenceTable(getActivity());
        _transactions = recurringTable.getAllRecurringTransactions();
        setListAdapter(new RecurringTransactionsListView(getActivity(), R.layout.recurring_transactions_out, _transactions, this));
    }

    @Override
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

    private class DialogDismissalHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            updateActivity();
        }
    }

}
