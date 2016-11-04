package com.personal.bewise.ui.search;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.personal.bewise.BeWiseConstants;
import com.personal.bewise.R;
import com.personal.bewise.database.BudgetTable;
import com.personal.bewise.database.PendingTransactionsTable;
import com.personal.bewise.database.RecurrenceTable;
import com.personal.bewise.database.TransactionsTable;
import com.personal.bewise.ui.BeWiseDatePicker;
import com.personal.bewise.utils.DateUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TODO: Fill it
 * <p/>
 * 1. If Income is selected to search the min amount cannot go below 0
 * <p/>
 * 2. If Expense is selected to search the max amount cannot go above 0
 * <p/>
 * 3. If both are selected, negative to postive range will be selected
 *
 * @author bilal
 */
public class SearchDialog extends DialogFragment implements OnClickListener {

    private Spinner _searchCategory;

    private LinearLayout _categoryLayout;

    private Spinner _category;

    private LinearLayout _budgetLayout;

    private Spinner _budgets;

    private EditText _startDate;

    private EditText _endDate;

    private EditText _minAmount;

    private EditText _maxAmount;

    private Spinner _recurringPeriod;

    private EditText _searchString;

    private Button _okButton;

    private Button _cancelButton;

    private ArrayAdapter<String> _adapter;

    private BeWiseDatePicker _datePickerDialog;

    private List<?> _transactions;

    public SearchDialog() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.search, container);
        getDialog().setTitle(R.string.label_search);

        createCategoryLayout(view);
        createSearchCategory(view);
        createBudgetLayout(view);
        createDateLayout(view);

        _minAmount = (EditText) view.findViewById(R.id.min_amount);
        _maxAmount = (EditText) view.findViewById(R.id.max_amount);

        createRecurringPeriod(view);

        _searchString = (EditText) view.findViewById(R.id.search_string);

        createButtons(view);

        return view;
    }

    private void createSearchCategory(View view) {
        _searchCategory = (Spinner) view.findViewById(R.id.search_items);
        String[] category = getResources().getStringArray(R.array.search_categories);
        _adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, category);
        _searchCategory.setAdapter(_adapter);
        _searchCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selected = parentView.getItemAtPosition(position).toString();
                if (selected.equalsIgnoreCase("Incomes and Expenses")) {
                    setLayoutsVisibility(false, false, false);
                } else if (selected.equalsIgnoreCase("Incomes")) {
                    setLayoutsVisibility(true, false, false);
                } else if (selected.equalsIgnoreCase("Expenses")) {
                    setLayoutsVisibility(false, true, true);
                } else if (selected.equalsIgnoreCase("Budgets")) {
                    setLayoutsVisibility(false, true, false);
                }
            }

            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
    }

    private void createCategoryLayout(View view) {
        _categoryLayout = (LinearLayout) view.findViewById(R.id.category_layout);
        _category = (Spinner) view.findViewById(R.id.category);
    }

    private void createBudgetLayout(View view) {
        _budgetLayout = (LinearLayout) view.findViewById(R.id.budget_layout);
        _budgets = (Spinner) view.findViewById(R.id.budgets);
    }

    private void createDateLayout(View view) {
        _startDate = (EditText) view.findViewById(R.id.from_date);
        _startDate.setText(DateUtilities.getCurrentDate());
        _startDate.setOnClickListener(this);

        _endDate = (EditText) view.findViewById(R.id.to_date);
        _endDate.setText(DateUtilities.getCurrentDate());
        _endDate.setOnClickListener(this);
    }

    private void createRecurringPeriod(View view) {
        _recurringPeriod = (Spinner) view.findViewById(R.id.recurring_period);
        List<String> recurring = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.recurrence_period)));
        recurring.add(0, "ANY");
        _adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, recurring);
        _recurringPeriod.setAdapter(_adapter);
    }

    private void createButtons(View view) {

        _okButton = (Button) view.findViewById(R.id.ok_button);
        _okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemToSearch = _searchCategory.getSelectedItem().toString();
                String category = _category.getSelectedItem().toString();
                String startDate = _startDate.getText().toString();
                String endDate = _endDate.getText().toString();
                double minAmount = Double.parseDouble(_minAmount.getText().toString());
                double maxAmount = Double.parseDouble(_maxAmount.getText().toString());
                String recurringPeriod = _recurringPeriod.getSelectedItem().toString();
                String stringToSearch = _searchString.getText().toString();

                // TODO: Add constants
                if (itemToSearch.equalsIgnoreCase("Incomes and Expenses") || itemToSearch.equalsIgnoreCase("Incomes")
                        || itemToSearch.equalsIgnoreCase("Expenses")) {
                    TransactionsTable transactionsTable = new TransactionsTable(getActivity());
                    _transactions = transactionsTable.searchTransactionsTable(itemToSearch, category, startDate, endDate, minAmount, maxAmount,
                            recurringPeriod, stringToSearch);
                } else if (itemToSearch.equalsIgnoreCase("Pending")) {
                    PendingTransactionsTable pendingTable = new PendingTransactionsTable(getActivity());
                    _transactions = pendingTable.searchPendingTransactionsTable(itemToSearch, startDate, endDate, minAmount, maxAmount, stringToSearch);
                } else if (itemToSearch.equalsIgnoreCase("Budgets")) {
                    BudgetTable budgetTable = new BudgetTable(getActivity());
                    _transactions = budgetTable.searchBudgetsTable(itemToSearch, startDate, endDate, minAmount, maxAmount, recurringPeriod, stringToSearch);
                } else if (itemToSearch.equalsIgnoreCase("Recurring")) {
                    RecurrenceTable recurringTable = new RecurrenceTable(getActivity());
                    _transactions = recurringTable.searchRecurringTable(itemToSearch, category, startDate, endDate, minAmount, maxAmount, recurringPeriod,
                            stringToSearch);
                }

                if (_transactions != null && !_transactions.isEmpty()) {
                    // WORKING
                    getDialog().dismiss();
                    FragmentManager fm = getFragmentManager();
                    SearchFragment searchFragment = new SearchFragment(itemToSearch, _transactions);
                    fm.beginTransaction().replace(R.id.container, searchFragment).commit();
                } else {
                    Toast.makeText(getActivity(), "Search criteria returns no results.", Toast.LENGTH_LONG).show();
                }
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

    private void setLayoutsVisibility(boolean incomeLayout, boolean budgetLayout, boolean expenseLayout) {
        _categoryLayout.setEnabled(incomeLayout);
        _categoryLayout.setVisibility(incomeLayout || expenseLayout ? View.VISIBLE : View.GONE);

        List<String> category;
        if (incomeLayout) {
            category = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.income_categories)));
        } else {
            category = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.expense_categories)));
        }
        _adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, category);
        category.add(0, "ANY");

        _category.setAdapter(_adapter);

        _budgetLayout.setEnabled(budgetLayout);
        _budgetLayout.setVisibility(budgetLayout ? View.VISIBLE : View.GONE);
        if (budgetLayout) {
            BudgetTable budget = new BudgetTable(getActivity());
            List<String> budgetsList = budget.getBudgetsList();
            budgetsList.add(0, BeWiseConstants.NONE);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, budgetsList);
            _budgets.setAdapter(dataAdapter);
        }

    }

    @Override
    public void onClick(View v) {
        if (v == _startDate) {
            _datePickerDialog = new BeWiseDatePicker(_startDate, null);
            _datePickerDialog.show(getFragmentManager(), "");
        } else if (v == _endDate) {
            _datePickerDialog = new BeWiseDatePicker(_endDate, _startDate.getText().toString());
            _datePickerDialog.show(getFragmentManager(), "");
        }
    }
}
