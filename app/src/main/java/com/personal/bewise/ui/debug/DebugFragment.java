package com.personal.bewise.ui.debug;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.personal.bewise.R;
import com.personal.bewise.database.BudgetData;
import com.personal.bewise.database.BudgetTable;
import com.personal.bewise.database.RecurrenceTable;
import com.personal.bewise.database.TransactionsData;
import com.personal.bewise.database.TransactionsTable;
import com.personal.bewise.test.PopulateDB;

import java.util.List;

public class DebugFragment extends Fragment {

    private Spinner _debugSpinner;

    private TextView _debugTextViewer;

    private ArrayAdapter<CharSequence> _adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.debug_layout, container, false);

        _debugSpinner = (Spinner) view.findViewById(R.id.debug_table_name_spinner);
        _adapter = ArrayAdapter.createFromResource(this.getActivity().getApplicationContext(), R.array.table_names, R.layout.custom_spinner_item);
        _adapter.setDropDownViewResource(R.layout.custom_spinner_item);
        _debugSpinner.setAdapter(_adapter);
        _debugSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selected = parent.getItemAtPosition(pos).toString();
                String display = "";
                if (selected.equalsIgnoreCase("transactions")) {
                    TransactionsTable transactionsTable = new TransactionsTable(getActivity());
                    List<TransactionsData> transactions = transactionsTable.getAllTransactions();
                    for (TransactionsData data : transactions) {
                        display = display + data.getTransactionID() + ", " + data.getStartDate() + ", " + data.getCategory() + ", " + data.getDescription()
                                + ", " + data.getAmount() + ", " + data.isRecurring() + ", " + data.getRecurringPeriod() + ", " + data.getUpdateDate() + ", "
                                + data.getReceiptPath() + ", " + data.getModifyReason();
                        display += "\n";
                    }
                } else if (selected.equalsIgnoreCase("recurrence")) {
                    RecurrenceTable recurTable = new RecurrenceTable(getActivity());
                    List<TransactionsData> recurList = recurTable.getAllRecurringTransactions();
                    for (TransactionsData data : recurList) {
                        display = display + data.getRecurringID() + ", " + data.getLastAccountedDate() + ", " + data.getNextDueDate() + ", " + data.getAmount()
                                + ", " + data.getRecurringPeriod();
                        display += "\n";
                    }
                } else if (selected.equalsIgnoreCase("budget")) {
                    BudgetTable budgetTable = new BudgetTable(getActivity());
                    List<BudgetData> budgetsList = budgetTable.getAllBudgets();
                    for (BudgetData data : budgetsList) {
                        display = display + data.getBudgetName() + ", " + data.getBudgetDate() + ", " + data.getBudgetAmount() + ", "
                                + data.getBudgetDescription() + ", " + data.getBudgetRecurrencePeriod();
                        display += "\n";
                    }
                } else if (selected.equalsIgnoreCase("database")) {
                    PopulateDB db = new PopulateDB(getActivity());
                    db.populateDataBase();
                }
                _debugTextViewer.setText(display);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
        _debugTextViewer = (TextView) view.findViewById(R.id.debug_table_contents_textview);
        return view;
    }
}
