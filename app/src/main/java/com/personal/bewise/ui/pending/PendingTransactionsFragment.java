package com.personal.bewise.ui.pending;

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
import com.personal.bewise.database.PendingTransactionsTable;
import com.personal.bewise.database.TransactionsData;
import com.personal.bewise.ui.CustomListFragment;
import com.personal.bewise.ui.details.TransactionDetailsDialog;
import com.personal.bewise.ui.transactions.TransactionDialog;
import com.personal.bewise.ui.transactions.TransactionsListView;
import com.personal.bewise.utils.TransactionDetailsMode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Notes:
 * <p>
 * 1. User cannot add pending transaction from this fragment.
 * <p>
 * 2. User can only modify or delete the pending transaction.
 *
 * @author bilal
 */

public class PendingTransactionsFragment extends CustomListFragment {

    private ImageButton _editButton;
    private ImageButton _deleteButton;

    private int _selectedItem = 0;
    private Map<Integer, Boolean> _checkState;
    private List<TransactionsData> _transactions;

    private Context _context;

    public PendingTransactionsFragment() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pending_transactions, container, false);
        setRetainInstance(true);
        _editButton = (ImageButton) view.findViewById(R.id.edit_button);
        _editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * TODO: Improve transactions dialog, with pending transactions edit mode.
                 */
                FragmentManager fm = getFragmentManager();
                TransactionsData data = _transactions.get(_selectedItem);
                boolean isIncome = data.isIncome();
                String title = isIncome ? BeWiseConstants.EDIT_INCOME : BeWiseConstants.EDIT_EXPENSE;
                TransactionDialog editNameDialog = new TransactionDialog();
                Bundle args = new Bundle();
                args.putSerializable("HANDLER", new DialogDismissalHandler());
                args.putString("DIALOG_TITLE", title);
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
                PendingTransactionsTable pendingTransactionsTable = new PendingTransactionsTable(
                        getActivity());
                Iterator<Map.Entry<Integer, Boolean>> iterator = _checkState.entrySet().iterator();
                while (iterator.hasNext()) {
                    // bug: transations are only 2 but index is retured as 3
                    Map.Entry<Integer, Boolean> item = iterator.next();
                    TransactionsData data = _transactions.get(item.getKey());
                    pendingTransactionsTable.deleteSinglePendingTransaction(data.getTransactionID());
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
        _context = getActivity();
        updateActivity();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        FragmentManager fm = getFragmentManager();
        TransactionsData transaction = (TransactionsData) l.getItemAtPosition(position);
        TransactionDetailsDialog detailsDialog = new TransactionDetailsDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("MODE", TransactionDetailsMode.PENDING);
        bundle.putSerializable("TRANSACTION", transaction);
        detailsDialog.setArguments(bundle);
        detailsDialog.show(fm, BeWiseConstants.TRANSACTION_DETAILS_DIALOG_TAG);
    }

    /** */
    private void updateActivity() {
        _checkState = new HashMap<Integer, Boolean>();
        PendingTransactionsTable pendingTransactionsTable = new PendingTransactionsTable(_context);
        _transactions = pendingTransactionsTable.getAllPendingTransactions();
        setListAdapter(
                new TransactionsListView(_context, R.layout.transactions_out, _transactions, this));
    }

    @Override
    public void setCheckBoxSelections(int itemId, boolean checkedState) {
        _selectedItem = itemId;
        if (checkedState) {
            _checkState.put(itemId, true); // _checkState should be initialized
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

    private class DialogDismissalHandler extends Handler implements Serializable {

        private static final long serialVersionUID = -1038633200461253851L;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            updateActivity();
        }
    }

}
