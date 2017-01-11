package com.personal.bewise.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.personal.bewise.BeWiseConstants;
import com.personal.bewise.utils.DateUtilities;
import com.personal.bewise.utils.SearchableItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author bilal
 */
public class BudgetTable extends TransactionItemsTable {

    /**
     * @param context
     */
    public BudgetTable(Context context) {
        super(context);
    }

    /**
     * @param budget
     * @return
     */
    public long addNewBudget(BudgetData budget) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        int id = budget.getBudgetName().hashCode();
        values.put(BUDGET_ID, id < 0 ? (id * (-1)) : id);
        long timestamp = DateUtilities.getCurrentDateStamp();
        values.put(BUDGET_NAME, budget.getBudgetName().toUpperCase(Locale.ENGLISH));
        values.put(BUDGET_CREATION_DATE, timestamp);
        if (budget.getBudgetStartDate().isEmpty()) {
            values.put(BUDGET_START_DATE, timestamp);
        } else {
            values.put(BUDGET_START_DATE, DateUtilities.getTimestampFromDate(budget.getBudgetStartDate()));
        }
        values.put(BUDGET_DESCRIPTION, budget.getBudgetDescription());
        values.put(BUDGET_RECURRENCE_PERIOD, budget.getBudgetRecurrencePeriod());
        values.put(BUDGET_AMOUNT, budget.getBudgetAmount());
        values.put(BUDGET_AMOUNT_REMAINING, budget.getBudgetRemainingAmount());
        long result = db.insert(BUDGET_TABLE, null, values);
        db.close();
        return result;
    }

    /**
     * @param budgetName
     * @return
     */
    public BudgetData getBudgetDetails(String budgetName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(BUDGET_TABLE, new String[]{BUDGET_NAME, BUDGET_CREATION_DATE, BUDGET_START_DATE, BUDGET_DESCRIPTION,
                BUDGET_RECURRENCE_PERIOD, BUDGET_AMOUNT, BUDGET_AMOUNT_REMAINING}, BUDGET_NAME + "=?", new String[]{budgetName}, null, null, null, null);
        BudgetData budget = new BudgetData();
        if (cursor != null && cursor.moveToFirst()) {
            budget.setBudgetName(cursor.getString(0));
            budget.setBudgetDate(DateUtilities.getDateFromTimestamp(cursor.getLong(1)));
            budget.setBudgetStartDate(DateUtilities.getDateFromTimestamp(cursor.getLong(2)));
            budget.setBudgetDescription(cursor.getString(3));
            budget.setBudgetRecurrencePeriod(cursor.getString(4));
            budget.setBudgetAmount(cursor.getDouble(5));
            budget.setBudgetRemainingAmount(cursor.getDouble(6));
            cursor.close();
        }
        db.close();
        return budget;
    }

    /**
     * @return
     */
    public List<BudgetData> getAllBudgets() {
        List<BudgetData> budgets = new ArrayList<BudgetData>();
        String selectQuery = "SELECT  * FROM " + BUDGET_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                BudgetData budget = new BudgetData();
                budget.setBudgetName(cursor.getString(1));
                budget.setBudgetDate(DateUtilities.getDateFromTimestamp(cursor.getLong(2)));
                budget.setBudgetStartDate(DateUtilities.getDateFromTimestamp(cursor.getLong(3)));
                budget.setBudgetDescription(cursor.getString(4));
                budget.setBudgetRecurrencePeriod(cursor.getString(5));
                budget.setBudgetAmount(cursor.getDouble(6));
                budget.setBudgetRemainingAmount(cursor.getDouble(7));
                budgets.add(budget);
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return budgets;
    }

    /**
     * Get the list (ony names) the available Budgets.
     *
     * @return List of budgets.
     */
    public List<String> getBudgetsList() {
        List<String> budgets = new ArrayList<String>();
        String selectQuery = "SELECT " + BUDGET_NAME + " FROM " + BUDGET_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                budgets.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return budgets;
    }

    /**
     * @param budget
     * @return
     */
    public int updateBudget(BudgetData budget) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BUDGET_DESCRIPTION, budget.getBudgetDescription());
        values.put(BUDGET_START_DATE, DateUtilities.getTimestampFromDate(budget.getBudgetStartDate()));
        values.put(BUDGET_RECURRENCE_PERIOD, budget.getBudgetRecurrencePeriod());
        values.put(BUDGET_AMOUNT, budget.getBudgetAmount());
        values.put(BUDGET_AMOUNT_REMAINING, budget.getBudgetRemainingAmount());
        int result = db.update(BUDGET_TABLE, values, BUDGET_NAME + " = ?", new String[]{budget.getBudgetName()});
        db.close();
        return result;
    }

    /**
     * @param budgetName
     */
    public void deleteBudget(String budgetName) {
        Log.d(BeWiseConstants.LOG_TAG, "deleteBudget(" + budgetName + ")");
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(BUDGET_TABLE, BUDGET_NAME + " = ?", new String[]{budgetName});
        db.close();
    }

    /**
     * @param searchableItems
     * @param startDate
     * @param endDate
     * @param minAmount
     * @param maxAmount
     * @param recurringPeriod
     * @param stringToSearch
     * @return
     */
    public List<BudgetData> searchBudgetsTable(String searchableItems, String startDate, String endDate, double minAmount, double maxAmount,
                                               String recurringPeriod, String stringToSearch) {
        // TODO: FIX logging
        Log.d(BeWiseConstants.LOG_TAG, "searchTransactionsTable(" + searchableItems + ", " + startDate + ")");
        List<BudgetData> budgets = new ArrayList<BudgetData>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + BUDGET_TABLE + " WHERE ";

        boolean empty = true;

        // Income and Expense Filter
        if (searchableItems.equalsIgnoreCase(SearchableItems.INCOME.getValue())) {
            selectQuery += BUDGET_AMOUNT + " >= 0 ";
            empty = false;
        } else if (searchableItems.equalsIgnoreCase(SearchableItems.EXPENSES.getValue())) {
            selectQuery += BUDGET_AMOUNT + " < 0 ";
            empty = false;
        }

        // date range filter
        if (startDate != null && endDate != null) {
            if (!empty) {
                selectQuery += " AND ";
            }
            selectQuery += BUDGET_START_DATE + " BETWEEN " + DateUtilities.getTimestampFromDate(startDate) + " AND "
                    + DateUtilities.getTimestampFromDate(endDate);
            empty = false;
        }

        // Amount range filter

        if (minAmount != 0.0 && maxAmount != 0.0) {
            if (!empty) {
                selectQuery += " AND ";
            }
            selectQuery += BUDGET_AMOUNT + " BETWEEN " + minAmount + " AND  " + maxAmount;
            empty = false;
        }

        // Recurring period filter
        if (recurringPeriod != null) {
            if (!empty) {
                selectQuery += " AND ";
            }
            selectQuery += BUDGET_RECURRENCE_PERIOD + " IS '" + recurringPeriod + "'";
            empty = false;
        }

        // String to search in
        if (stringToSearch != null && !"".equals(stringToSearch)) {
            if (!empty) {
                selectQuery += " AND ";
            }
            selectQuery += BUDGET_DESCRIPTION + " LIKE '" + stringToSearch + "' ";
            empty = false;
        }

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                BudgetData budget = new BudgetData();
                budget.setBudgetName(cursor.getString(1));
                budget.setBudgetDate(DateUtilities.getDateFromTimestamp(cursor.getLong(2)));
                budget.setBudgetStartDate(DateUtilities.getDateFromTimestamp(cursor.getLong(3)));
                budget.setBudgetDescription(cursor.getString(4));
                budget.setBudgetRecurrencePeriod(cursor.getString(5));
                budget.setBudgetAmount(cursor.getDouble(6));
                budgets.add(budget);
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return budgets;
    }

}
