package com.personal.bewise.ui.search;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.personal.bewise.BeWiseConstants;
import com.personal.bewise.R;
import com.personal.bewise.database.BudgetData;
import com.personal.bewise.database.TransactionsData;
import com.personal.bewise.ui.CustomListFragment;
import com.personal.bewise.ui.budget.BudgetListView;
import com.personal.bewise.ui.details.TransactionDetailsDialog;
import com.personal.bewise.ui.transactions.TransactionsListView;
import com.personal.bewise.utils.TransactionDetailsMode;

import java.util.List;

public class SearchFragment extends CustomListFragment {

    private List<?> _transactions;
    private String _fragmentTitle;
    private TextView _searchFragmentTtile;

    public SearchFragment(String fragmentTitle, List<?> transactions) {
        this._transactions = transactions;
        this._fragmentTitle = fragmentTitle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.searched_transactions, container, false);
        _searchFragmentTtile = (TextView) view.findViewById(R.id.title);
        _searchFragmentTtile.setText("Search Result: " + _fragmentTitle);
        setRetainInstance(true);
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
    @SuppressWarnings("unchecked")
    private void updateActivity() {
        if (_transactions.get(0).getClass().equals(TransactionsData.class)) {
            setListAdapter(new TransactionsListView(getActivity(), R.layout.transactions_out, (List<TransactionsData>) _transactions, this));
        } else if (_transactions.get(0).getClass().equals(BudgetData.class)) {
            setListAdapter(new BudgetListView(getActivity(), R.layout.budget_out, (List<BudgetData>) _transactions, this));
        }
    }

    @Override
    public void setCheckBoxSelections(int itemId, boolean checkedState) {

    }
}