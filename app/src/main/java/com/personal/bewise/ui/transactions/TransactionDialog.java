package com.personal.bewise.ui.transactions;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.personal.bewise.BeWiseConstants;
import com.personal.bewise.R;
import com.personal.bewise.database.BudgetData;
import com.personal.bewise.database.BudgetTable;
import com.personal.bewise.database.PendingTransactionsTable;
import com.personal.bewise.database.RecurrenceTable;
import com.personal.bewise.database.TransactionsData;
import com.personal.bewise.database.TransactionsTable;
import com.personal.bewise.ui.BeWiseDatePicker;
import com.personal.bewise.utils.DateUtilities;
import com.personal.bewise.utils.RecurrencePeriod;

import java.text.ParseException;
import java.util.List;

/**
 * @author bilal
 */
public class TransactionDialog extends DialogFragment implements OnClickListener {
    /**
     * Title of the dialog.
     */
    private String titleDialog;

    /**
     * Dialog mode.
     */
    private String modeDialog;

    /**
     * Is transaction an income.
     */
    private boolean _isIncome;

    private boolean _isRecurring;
    private String _currentDate;
    private TransactionsData _transaction;
    private EditText _amountEditText;
    private Spinner _categorySpinner;
    private EditText _descriptionEditText;
    private Spinner _budgetSpinner;
    private EditText _modifyReasonEditText;
    private Spinner _recurringPeriodSpinner;
    private EditText _startDateEditText;
    private ArrayAdapter<CharSequence> _adapter;
    private Button _okButton;
    private Button _cancelButton;
    private Handler _handler;
    private Context _context;

    private BeWiseDatePicker _datePickerDialog;

    /**
     *
     */
    public TransactionDialog(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.transaction_dialog, container);
        _context = getActivity();

        Bundle args = getArguments();
        this.titleDialog = args.getString("DIALOG_TITLE");
        this.modeDialog = args.getString("DIALOG_MODE");
        this._transaction = (TransactionsData) args.getSerializable("TRANSACTION_DATA");
        this._isIncome = args.getBoolean("IS_INCOME");
        this._handler = (Handler) args.getSerializable("HANDLER");

        getDialog().setTitle(titleDialog);
        _currentDate = DateUtilities.getCurrentDate();

        createAmountLayout(view);
        createCategoryLayout(view);
        createDescriptionLayout(view);
        createBudgetLayout(view);
        createTransactionEditLayout(view);
        createRecurrencePeriodLayout(view);
        createAccountDateLayout(view);
        createDialogButtons(view);

        if (modeDialog.equalsIgnoreCase(BeWiseConstants.EDIT_DIALOG_MODE) && _transaction != null) {
            populateDialogForUpdate(view, _transaction);
        }
        setRetainInstance(true);
        return view;
    }

    /**
     * Populate dialog for update.
     *
     * @param view
     * @param transaction
     * @throws ParseException
     */
    private void populateDialogForUpdate(View view, TransactionsData transaction) {

        _amountEditText.setText(Double.toString(transaction.getAmount()));

        ArrayAdapter adapter = (ArrayAdapter) _categorySpinner.getAdapter();
        int spinnerPosition = adapter.getPosition(transaction.getCategory());
        _categorySpinner.setSelection(spinnerPosition);

        _descriptionEditText.setText(transaction.getDescription());

        adapter = (ArrayAdapter) _budgetSpinner.getAdapter();
        spinnerPosition = adapter.getPosition(transaction.getBudget());
        _budgetSpinner.setSelection(spinnerPosition);

        _modifyReasonEditText.setText(transaction.getModifyReason());

        adapter = (ArrayAdapter) _recurringPeriodSpinner.getAdapter();
        spinnerPosition = adapter.getPosition(transaction.getRecurringPeriod());
        _recurringPeriodSpinner.setSelection(spinnerPosition);

        _startDateEditText.setText(transaction.getStartDate());

        _recurringPeriodSpinner.setEnabled(false);
        if (transaction.getRecurringPeriod().equals(RecurrencePeriod.NONE.toString())) {
            _categorySpinner.setEnabled(false);
            _budgetSpinner.setEnabled(false);
            _startDateEditText.setEnabled(false);
        }
    }

    /**
     * @param view
     */
    private void createAmountLayout(View view) {
        _amountEditText = (EditText) view.findViewById(R.id.transaction_amount);
        if (this._isIncome) {
            _amountEditText.setTextColor(getResources().getColor(R.color.Green));
        } else {
            _amountEditText.setTextColor(getResources().getColor(R.color.Red));
        }
    }

    /**
     * Create the category layout.
     *
     * @param view view Object.
     */
    private void createCategoryLayout(final View view) {
        _categorySpinner = (Spinner) view.findViewById(R.id.transaction_category);
        if (_isIncome) {
            _adapter = ArrayAdapter.createFromResource(_context, R.array.income_categories,
                    android.R.layout.simple_spinner_item);
        } else {
            _adapter = ArrayAdapter.createFromResource(_context, R.array.expense_categories,
                    android.R.layout.simple_spinner_item);
        }
        _categorySpinner.setAdapter(_adapter);
    }

    private void createBudgetLayout(View view) {
        _budgetSpinner = (Spinner) view.findViewById(R.id.transaction_budget);

        BudgetTable budget = new BudgetTable(_context);
        List<String> budgetsList = budget.getBudgetsList();

        budgetsList.add(0, BeWiseConstants.NONE);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(_context,
                android.R.layout.simple_spinner_item, budgetsList);

        _budgetSpinner.setAdapter(dataAdapter);
        if (this._isIncome) {
            _budgetSpinner.setEnabled(false);
        } else {
            _budgetSpinner.setEnabled(true);
        }

    /*
     * Only the equal and lower recurring values than budget recurring values will be shown, not the
     * higher ones. This will keep the consistency that a recurring transaction within a budget
     * should not have higher recurrence period.
     */

        _budgetSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                String budget = parentView.getItemAtPosition(position).toString();
                if (!budget.equalsIgnoreCase("NONE")) {

                    BudgetTable budgetTable = new BudgetTable(_context);
                    BudgetData budgetData = budgetTable.getBudgetDetails(budget);
                    String budgetRecurrence = budgetData.getBudgetRecurrencePeriod();
                    final ArrayAdapter adapter = new ArrayAdapter(_context,
                            android.R.layout.simple_spinner_item,
                            RecurrencePeriod.getEnumBoundList(budgetRecurrence));
                    _recurringPeriodSpinner.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // TODO Auto-generated method stub
            }
        });
    }

    /**
     * Create description layout.
     *
     * @param view Reference of view.
     */
    private void createDescriptionLayout(final View view) {
        _descriptionEditText = (EditText) view.findViewById(R.id.transaction_description);
    }

    /**
     * @param view
     */
    private void createTransactionEditLayout(final View view) {
        _modifyReasonEditText = (EditText) view.findViewById(R.id.transaction_edit_reason);
    }

    /**
     * @param view
     */
    private void createRecurrencePeriodLayout(final View view) {
        _recurringPeriodSpinner = (Spinner) view.findViewById(R.id.recurring_period);
        final ArrayAdapter adapter = new ArrayAdapter(_context,
                android.R.layout.simple_spinner_item, RecurrencePeriod.getEnumNames());
        _recurringPeriodSpinner.setAdapter(adapter);
    }

    /**
     * @param view
     */
    private void createAccountDateLayout(final View view) {
        _startDateEditText = (EditText) view.findViewById(R.id.start_recurring_date);
        _startDateEditText.setText(_currentDate);
        _startDateEditText.setOnClickListener(this);
    }

    /**
     * @param view
     */
    private void createDialogButtons(final View view) {
        _okButton = (Button) view.findViewById(R.id.ok_button);
        _okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modeDialog.equalsIgnoreCase(BeWiseConstants.NEW_DIALOG_MODE)) {
                    try {
                        insertNewTransactionItem();
                    } catch (ParseException pex) {

                    }
                } else if (modeDialog.equalsIgnoreCase(BeWiseConstants.EDIT_DIALOG_MODE)) {
                    TransactionsData transaction = new TransactionsData();
                    String recurrSpinner = _recurringPeriodSpinner.getSelectedItem().toString();
                    transaction.setTransactionID(_transaction.getTransactionID());
                    transaction.setDate(_transaction.getDate());
                    transaction.setIncome(_isIncome);
                    _isRecurring = !recurrSpinner.equals(RecurrencePeriod.NONE.toString());
                    transaction.setUpdateDate(_currentDate);
                    transaction.setCategory(_categorySpinner.getSelectedItem().toString());
                    double amount = Double.parseDouble(_amountEditText.getText().toString());
                    if (amount < 0 && transaction.isIncome()) {
                        amount *= -1;
                    } else if (amount > 0 && !transaction.isIncome()) {
                        amount *= -1;
                    }
                    transaction.setAmount(amount);
                    transaction.setDescription(_descriptionEditText.getText().toString());
                    transaction.setBudget(_budgetSpinner.getSelectedItem().toString());
                    transaction.setModifyReason(_modifyReasonEditText.getText().toString());
                    transaction.setRecurring(_isRecurring);
                    transaction.setRecurringPeriod(recurrSpinner);
                    transaction.setStartDate(_startDateEditText.getText().toString());
                    TransactionsTable transactionsTable = new TransactionsTable(_context);
                    if (!_isRecurring) {
                        if (DateUtilities.isAfterToday(transaction.getStartDate()) == -1) {
                            PendingTransactionsTable pendingTransactionsTable = new PendingTransactionsTable(
                                    _context);
                            pendingTransactionsTable.addNewPendingTransaction(transaction);
                            transactionsTable.deleteSingleTransaction(transaction.getTransactionID());
                        } else {
                            transactionsTable.updateTransaction(transaction);
                        }
                    } else {
                        transactionsTable.updateTransaction(transaction);
                    }

                }
                getDialog().dismiss();
            }
        });

        _cancelButton = (Button) view.findViewById(R.id.cancel_button);
        _cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        _handler.sendEmptyMessage(0);

    }

    /**
     * onDestroyView:
     *
     * --IMPORTANT--
     * It will avoid killing the dialog on orientation is changed.
     */
    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    /**
     * Insert new transaction.
     *
     * @throws ParseException Throw expection on error.
     */
    private void insertNewTransactionItem() throws ParseException {
        // TODO: Refactor..
        long id = System.currentTimeMillis();
        TransactionsData transaction = new TransactionsData();
        transaction.setTransactionID(id);
        transaction.setDate(DateUtilities.getCurrentDate());
        transaction.setStartDate(_startDateEditText.getText().toString());
        transaction.setUpdateDate(DateUtilities.getCurrentDate());
        transaction.setIncome(_isIncome);
        double amount = Double.parseDouble(_amountEditText.getText().toString());
        if (amount <= 0) {
            Toast.makeText(_context, "Amount not acceptable", Toast.LENGTH_LONG).show();
            return;
        } else if (amount > 0 && !transaction.isIncome()) {
            amount *= -1;
        }
        transaction.setAmount(amount);
        transaction.setCategory(_categorySpinner.getSelectedItem().toString());
        transaction.setDescription(_descriptionEditText.getText().toString());
        transaction.setBudget(_budgetSpinner.getSelectedItem().toString());
        String recurrSpinner = _recurringPeriodSpinner.getSelectedItem().toString();
        int recurrence = getRecurrencePeriodAsInt(recurrSpinner);
        _isRecurring = recurrence != 0;
        transaction.setRecurring(_isRecurring);
        transaction.setRecurringPeriod(recurrSpinner);
        transaction.setModifyReason(_modifyReasonEditText.getText().toString());
        transaction.setReceiptPath(""); // TODO: next feature
        if (_isRecurring) {
            transaction.setRecurringID(id);
            if (DateUtilities.isAfterToday(transaction.getStartDate()) == -1) {
                // If the start date is after today, Set next due date to
                // start date, and only add to recurring transactions
                transaction.setNextDueDate(transaction.getStartDate());
                RecurrenceTable recurrenceTable = new RecurrenceTable(_context);
                recurrenceTable.addNewRecurringItem(transaction);
            } else {
                // Otherwise calculate the next due date
                transaction.setNextDueDate(DateUtilities.getNextDueDate(transaction.getStartDate(),
                        DateUtilities.getRecurrencePeriodAsInt(transaction.getRecurringPeriod())));
                // Add transaction to both Recurrence and
                // Transactions tables
                RecurrenceTable recurrenceTable = new RecurrenceTable(_context);
                recurrenceTable.addNewRecurringItem(transaction);
                TransactionsTable transactionsTable = new TransactionsTable(_context);
                transactionsTable.addNewTransaction(transaction);
            }
        } else {
            if (DateUtilities.isAfterToday(transaction.getStartDate()) == -1) {
                PendingTransactionsTable pendingTransactionsTable = new PendingTransactionsTable(
                        _context);
                pendingTransactionsTable.addNewPendingTransaction(transaction);
            } else {
                TransactionsTable transactionsTable = new TransactionsTable(_context);
                transactionsTable.addNewTransaction(transaction);
            }
        }
    }

    /**
     * Get the integer value of recurrence period.
     *
     * @param recurrence Recurrence value.
     * @return Integer value of recurrence period.
     */
    private int getRecurrencePeriodAsInt(String recurrence) {
        if (recurrence.equalsIgnoreCase(RecurrencePeriod.DAY.toString())) {
            return RecurrencePeriod.DAY.getValue();
        } else if (recurrence.equalsIgnoreCase(RecurrencePeriod.WEEK.toString())) {
            return RecurrencePeriod.WEEK.getValue();
        } else if (recurrence.equalsIgnoreCase(RecurrencePeriod.MONTH.toString())) {
            return RecurrencePeriod.MONTH.getValue();
        } else if (recurrence.equalsIgnoreCase(RecurrencePeriod.QUARTER.toString())) {
            return RecurrencePeriod.QUARTER.getValue();
        } else if (recurrence.equalsIgnoreCase(RecurrencePeriod.HALF_YEAR.toString())) {
            return RecurrencePeriod.HALF_YEAR.getValue();
        } else if (recurrence.equalsIgnoreCase(RecurrencePeriod.YEAR.toString())) {
            return RecurrencePeriod.YEAR.getValue();
        } else {
            return 0;
        }
    }

    @Override
    public void onClick(View v) {
        _datePickerDialog = new BeWiseDatePicker(_startDateEditText, _currentDate);
        _datePickerDialog.show(getFragmentManager(), "");
    }

}
