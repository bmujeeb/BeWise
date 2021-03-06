package com.personal.bewise.ui.budget;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.personal.bewise.R;
import com.personal.bewise.database.BudgetData;
import com.personal.bewise.database.BudgetTable;
import com.personal.bewise.ui.BeWiseDatePicker;
import com.personal.bewise.utils.DateUtilities;

/**
 * TODO: Should contain name, date of creation, description, amount and recurrence period.
 * <p/>
 * Add new Budget dialog.
 *
 * @author bilal
 */

public class BudgetDialog extends DialogFragment implements OnClickListener {

    /**
     * The dialog title.
     */
    private String _dialogTitle;

    /**
     * The mode of dialog.
     */
    private String _dialogMode;

    private BudgetData _budgetData;

    private Handler _handler;

    private ArrayAdapter<CharSequence> _adapter;

    private EditText _budgetName;

    private EditText _budgetDate;

    private EditText _budgetDescription;

    private EditText _budgetAmount;

    private Spinner _budgetRecurringPeriod;

    private Button _okButton;

    private Button _cancelButton;

    private BeWiseDatePicker _datePickerDialog;

    private Bundle bundle;

    private Context _context;

    public BudgetDialog() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.budget_dialog, container);
        _context = getActivity();
        bundle = getArguments();
        getDialog().setTitle(bundle.getString("DIALOG_TITLE"));
        this._budgetData = (BudgetData)bundle.getSerializable("BUDGET");
        this._handler = (Handler) bundle.getSerializable("HANDLER");
        this._dialogMode = bundle.getString("DIALOG_MODE");

        _budgetName = (EditText) view.findViewById(R.id.budget_name);
        createBudgetDateControl(view);

        _budgetDescription = (EditText) view.findViewById(R.id.budget_description);
        _budgetAmount = (EditText) view.findViewById(R.id.budget_amount);
        createRecurrencePeriod(view);
        createDialogButtons(view);

        if (_dialogMode.equalsIgnoreCase("edit") && _budgetData != null) {
            updateUiControlsForEdit(_budgetData);
        }

        return view;
    }

    /**
     * @param view
     */
    private void createBudgetDateControl(View view) {
        _budgetDate = (EditText) view.findViewById(R.id.budget_date);
        _budgetDate.setText(DateUtilities.getCurrentDate());

    }

    /**
     * Create recurrence period layout.
     *
     * @param view View object.
     */
    private void createRecurrencePeriod(final View view) {
        _budgetRecurringPeriod = (Spinner) view.findViewById(R.id.budget_recurring_period);
        _adapter = ArrayAdapter.createFromResource(getActivity(), R.array.budget_recurrence_period, android.R.layout.simple_spinner_item);
        _budgetRecurringPeriod.setAdapter(_adapter);
        _budgetRecurringPeriod.setSelection(2);
    }

    /**
     * Create dialog buttons.
     *
     * @param view View object.
     */
    private void createDialogButtons(final View view) {
        _okButton = (Button) view.findViewById(R.id.budget_button_ok);
        _okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                _budgetData = readBudgetUiItems();
                BudgetTable budget = new BudgetTable(_context);
                if (_dialogMode.equalsIgnoreCase("edit")) {
                    budget.updateBudget(_budgetData);
                } else {
                    budget.addNewBudget(_budgetData);
                }
                getDialog().dismiss();
            }
        });
        _cancelButton = (Button) view.findViewById(R.id.budget_button_cancel);
        _cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                getDialog().dismiss();
            }
        });
    }

    /**
     * Read budget data items from UI.
     *
     * @return BudgetDate object.
     */
    private BudgetData readBudgetUiItems() {
        BudgetData data = new BudgetData();
        data.setBudgetName(_budgetName.getText().toString());
        data.setBudgetStartDate(_budgetDate.getText().toString());
        data.setBudgetDescription(_budgetDescription.getText().toString());
        data.setBudgetRecurrencePeriod(_budgetRecurringPeriod.getSelectedItem().toString());
        data.setBudgetAmount(Double.parseDouble(_budgetAmount.getText().toString()));
        return data;
    }

    /**
     * Update UI for for editing budget information.
     *
     * @param data BudgetDate object.
     */
    private void updateUiControlsForEdit(final BudgetData data) {
        _budgetName.setEnabled(false);
        _budgetName.setText(data.getBudgetName());

        _budgetDate.setEnabled(false);
        _budgetDate.setText(data.getBudgetDate());

        _budgetDescription.setText(data.getBudgetDescription());

        ArrayAdapter adapter = (ArrayAdapter) _budgetRecurringPeriod.getAdapter();
        int spinnerPosition = adapter.getPosition(data.getBudgetRecurrencePeriod());
        _budgetRecurringPeriod.setSelection(spinnerPosition);

        _budgetAmount.setText(Double.toString(data.getBudgetAmount()));
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (_handler != null) {
            _handler.sendEmptyMessage(0);
        }
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

    @Override
    public void onClick(final View v) {
        _datePickerDialog = new BeWiseDatePicker(_budgetDate, DateUtilities.getCurrentDate());
        _datePickerDialog.show(getFragmentManager(), "");
    }

}
