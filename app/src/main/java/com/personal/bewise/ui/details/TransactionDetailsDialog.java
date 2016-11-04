package com.personal.bewise.ui.details;

import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.personal.bewise.R;
import com.personal.bewise.database.TransactionsData;
import com.personal.bewise.utils.TransactionDetailsMode;

/**
 * TODO: Improve UI
 * <p/>
 * TODO: for pending transaction recurring period and start date will not be
 * displayed.
 *
 * @author bilal
 */
public class TransactionDetailsDialog extends DialogFragment {

    private TransactionsData _transaction;
    private String _dialogTitle;
    private TransactionDetailsMode _dialogMode;

    private TextView _dateLabel;
    private TextView _date;
    private LinearLayout _lastBilledDateLayout;
    private TextView _lastBilledDate;
    private TextView _amount;
    private TextView _category;
    private TextView _description;
    private TextView _budget;
    private TextView _modifyReason;
    private LinearLayout _recurringPeriodLayout;
    private TextView _recurringPeriod;
    private Button _dismiss;

    public TransactionDetailsDialog() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View view = inflater.inflate(R.layout.transaction_details_dialog, container);

        Bundle bundle = getArguments();
        this._dialogMode = (TransactionDetailsMode) bundle.getSerializable("MODE");
        this._dialogTitle = this._dialogMode.getValue();
        this._transaction = (TransactionsData) bundle.getSerializable("TRANSACTION");

        getDialog().setTitle(this._dialogTitle);


        _dateLabel = (TextView) view.findViewById(R.id.transaction_date_label);

        _date = (TextView) view.findViewById(R.id.transaction_date);

        _lastBilledDateLayout = (LinearLayout) view.findViewById(R.id.last_accounted_date_layout);
        _lastBilledDate = (TextView) view.findViewById(R.id.transaction_last_accounted_date);
        _lastBilledDate.setText(_transaction.getLastAccountedDate());

        _amount = (TextView) view.findViewById(R.id.transaction_amount);
        if (_transaction.isIncome()) {
            _amount.setTextColor(Color.GREEN);
        } else {
            _amount.setTextColor(Color.RED);
        }
        _amount.setText(Double.toString(_transaction.getAmount()));

        _category = (TextView) view.findViewById(R.id.transaction_category);
        _category.setText(_transaction.getCategory());

        _description = (TextView) view.findViewById(R.id.transaction_description);
        _description.setText(_transaction.getDescription());

        _budget = (TextView) view.findViewById(R.id.transaction_budget);
        _budget.setText(_transaction.getBudget());

        _modifyReason = (TextView) view.findViewById(R.id.modify_reason);
        _modifyReason.setText(_transaction.getModifyReason());

        _recurringPeriodLayout = (LinearLayout) view.findViewById(R.id.recurrence_period_layout);

        _recurringPeriod = (TextView) view.findViewById(R.id.recurring_period);
        _recurringPeriod.setText(_transaction.getRecurringPeriod());

        _dismiss = (Button) view.findViewById(R.id.close_button);
        _dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        if (_dialogMode == TransactionDetailsMode.INCOME || _dialogMode == TransactionDetailsMode.EXPENSE) {
            transactionControls();
        } else if (_dialogMode == TransactionDetailsMode.PENDING) {
            pendingTransactionControls();
        } else if (_dialogMode == TransactionDetailsMode.RECURRING) {
            recurringTransactionControls();
        }

        return view;
    }

    private void transactionControls() {
        _date.setText(_transaction.getStartDate());
        _lastBilledDateLayout.setVisibility(View.GONE);
    }

    private void pendingTransactionControls() {
        _dateLabel.setText("Due Date");
        _date.setText(_transaction.getStartDate());
        _recurringPeriodLayout.setVisibility(View.GONE);
        _lastBilledDateLayout.setVisibility(View.GONE);
    }

    private void recurringTransactionControls() {
        _dateLabel.setText("Due Date");
        _date.setText(_transaction.getNextDueDate());

    }

}
