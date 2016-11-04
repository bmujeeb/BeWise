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

public class RecurrenceTable extends TransactionItemsTable {

    private static final String[] RECURRENCE_TABLE_ARRAY = {RECURRENCE_ID, RECURRENCE_DATE, RECURRENCE_DUE_DATE, RECURRENCE_PERIOD, RECURRENCE_CATEGORY,
            RECURRENCE_DESCRIPTION, RECURRENCE_AMOUNT, RECURRENCE_IS_INCOME, RECURRENCE_EDIT_REASON, RECURRENCE_BUDGET};

    public RecurrenceTable(Context context) {
        super(context);
    }

    // TODO: Link recurring item to income and expense
    public long addNewRecurringItem(TransactionsData data) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(RECURRENCE_ID, data.getRecurringID());
        values.put(RECURRENCE_DATE, DateUtilities.getTimestampFromDate(data.getLastAccountedDate()));
        values.put(RECURRENCE_DUE_DATE, DateUtilities.getTimestampFromDate(data.getNextDueDate()));
        values.put(RECURRENCE_PERIOD, data.getRecurringPeriod());
        values.put(RECURRENCE_CATEGORY, data.getCategory());
        values.put(RECURRENCE_DESCRIPTION, data.getDescription());
        values.put(RECURRENCE_AMOUNT, data.getAmount());
        values.put(RECURRENCE_IS_INCOME, data.isIncome() ? 1 : 0);
        values.put(RECURRENCE_EDIT_REASON, data.getModifyReason());
        values.put(RECURRENCE_BUDGET, data.getBudget());
        long result = db.insert(RECURRENCE_TABLE, null, values);
        db.close();
        return result;
    }

    public TransactionsData getRecurringItem(String recurringID) {
        TransactionsData data = new TransactionsData();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(RECURRENCE_TABLE, RECURRENCE_TABLE_ARRAY, RECURRENCE_ID + "=?", new String[]{String.valueOf(recurringID)}, null, null,
                null, null);
        if (cursor != null && cursor.moveToFirst()) {
            data.setRecurringID(cursor.getLong(0));
            data.setLastAccountedDate(DateUtilities.getDateFromTimestamp(cursor.getLong(1)));
            data.setNextDueDate(DateUtilities.getDateFromTimestamp(cursor.getLong(2)));
            data.setRecurringPeriod(cursor.getString(3));
            data.setCategory(cursor.getString(4));
            data.setDescription(cursor.getString(5));
            data.setAmount(cursor.getDouble(6));
            data.setIncome(cursor.getInt(7) == 1 ? true : false);
            data.setModifyReason(cursor.getString(8));
            data.setBudget(cursor.getString(9));
            cursor.close();
        }
        db.close();
        return data;
    }

    public List<TransactionsData> getAllRecurringTransactions() {
        List<TransactionsData> transactionsList = new ArrayList<TransactionsData>();
        String selectQuery = "SELECT  * FROM " + RECURRENCE_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TransactionsData data = new TransactionsData();
                data.setRecurringID(cursor.getLong(0));
                data.setLastAccountedDate(DateUtilities.getDateFromTimestamp(cursor.getLong(1)));
                data.setNextDueDate(DateUtilities.getDateFromTimestamp(cursor.getLong(2)));
                data.setRecurringPeriod(cursor.getString(3));
                data.setCategory(cursor.getString(4));
                data.setDescription(cursor.getString(5));
                data.setAmount(cursor.getDouble(6));
                data.setIncome(cursor.getInt(7) == 1 ? true : false);
                data.setModifyReason(cursor.getString(8));
                data.setBudget(cursor.getString(9));
                transactionsList.add(data);
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return transactionsList;
    }

    public int updateRecurringTransaction(TransactionsData data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RECURRENCE_DATE, DateUtilities.getTimestampFromDate(data.getLastAccountedDate()));
        values.put(RECURRENCE_DUE_DATE, DateUtilities.getTimestampFromDate(data.getNextDueDate()));
        values.put(RECURRENCE_PERIOD, data.getRecurringPeriod());
        values.put(RECURRENCE_CATEGORY, data.getCategory());
        values.put(RECURRENCE_DESCRIPTION, data.getDescription());
        values.put(RECURRENCE_AMOUNT, data.getAmount());
        values.put(RECURRENCE_IS_INCOME, data.isIncome() ? 1 : 0);
        values.put(RECURRENCE_EDIT_REASON, data.getModifyReason());
        values.put(RECURRENCE_BUDGET, data.getBudget());
        int result = db.update(RECURRENCE_TABLE, values, RECURRENCE_ID + " = ?", new String[]{String.valueOf(data.getRecurringID())});
        db.close();
        return result;
    }

    // Can be refactored
    public void deleteRecurringTransaction(long recurrenceID) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(RECURRENCE_TABLE, RECURRENCE_ID + " = ?", new String[]{String.valueOf(recurrenceID)});
        db.close();
    }

    /**
     * @param searchableItems
     * @param category
     * @param startDate
     * @param endDate
     * @param minAmount
     * @param maxAmount
     * @param recurringPeriod
     * @param stringToSearch
     * @return
     */
    public List<TransactionsData> searchRecurringTable(String searchableItems, String category, String startDate, String endDate, double minAmount,
                                                       double maxAmount, String recurringPeriod, String stringToSearch) {
        // TODO: FIX logging
        Log.d(BeWiseConstants.LOG_TAG, "searchTransactionsTable(" + searchableItems + ", " + startDate + ")");
        List<TransactionsData> transactionsList = new ArrayList<TransactionsData>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + RECURRENCE_TABLE + " WHERE ";

        boolean empty = true;

        // Income and Expense Filter
        if (searchableItems.equalsIgnoreCase(SearchableItems.INCOME.getValue())) {
            selectQuery += RECURRENCE_AMOUNT + " >= 0 ";
            empty = false;
        } else if (searchableItems.equalsIgnoreCase(SearchableItems.EXPENSES.getValue())) {
            selectQuery += RECURRENCE_AMOUNT + " < 0 ";
            empty = false;
        }

        // Category filter
        if (category != null) {
            if (!empty) {
                selectQuery += " AND ";
            }
            selectQuery += RECURRENCE_CATEGORY + " IS '" + category + "'";
            empty = false;
        }

        // date range filter
        if (startDate != null && endDate != null) {
            if (!empty) {
                selectQuery += " AND ";
            }
            selectQuery += RECURRENCE_DATE + " BETWEEN " + DateUtilities.getTimestampFromDate(startDate) + " AND "
                    + DateUtilities.getTimestampFromDate(endDate);
            empty = false;
        }

        // Amount range filter

        if (minAmount != 0.0 && maxAmount != 0.0) {
            if (!empty) {
                selectQuery += " AND ";
            }
            selectQuery += RECURRENCE_AMOUNT + " BETWEEN " + minAmount + " AND  " + maxAmount;
            empty = false;
        }

        // Recurring period filter
        if (recurringPeriod != null) {
            if (!empty) {
                selectQuery += " AND ";
            }
            selectQuery += RECURRENCE_PERIOD + " IS '" + recurringPeriod + "'";
            empty = false;
        }

        // String to search in
        if (stringToSearch != null && !"".equals(stringToSearch)) {
            if (!empty) {
                selectQuery += " AND ";
            }
            selectQuery += RECURRENCE_DESCRIPTION + " like '" + stringToSearch + "'";
            empty = false;
        }

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TransactionsData transaction = new TransactionsData();
                transaction.setTransactionID(cursor.getLong(0));
                transaction.setRecurringID(cursor.getLong(1));
                transaction.setDate(DateUtilities.getDateFromTimestamp(cursor.getLong(2)));
                transaction.setUpdateDate(DateUtilities.getDateFromTimestamp(cursor.getLong(3)));
                transaction.setStartDate(DateUtilities.getDateFromTimestamp(cursor.getLong(4)));
                transaction.setCategory(cursor.getString(5));
                transaction.setDescription(cursor.getString(6));
                transaction.setAmount(cursor.getDouble(7));
                transaction.setIncome(cursor.getInt(8) == 1);
                transaction.setModifyReason(cursor.getString(9));
                transaction.setBudget(cursor.getString(10));
                transaction.setReceiptPath(cursor.getString(11));
                transactionsList.add(transaction);
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return transactionsList;
    }
}
