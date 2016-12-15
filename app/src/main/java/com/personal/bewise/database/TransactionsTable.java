package com.personal.bewise.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.personal.bewise.BeWiseConstants;
import com.personal.bewise.utils.DateUtilities;
import com.personal.bewise.utils.NumberUtilities;
import com.personal.bewise.utils.SearchableItems;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add budget related or custom select query.
 *
 * @author bilal
 */
public class TransactionsTable extends TransactionItemsTable {

    private static final String[] TRANSACTIONS_TABLE_ARRAY = {TRANSACTION_ID, TRANSACTION_RECURRING_ID, TRANSACTION_DATE, TRANSACTION_UPDATE_DATE,
            TRANSACTION_START_DATE, TRANSACTION_CATEGORY, TRANSACTION_DESCRIPTION, TRANSACTION_AMOUNT, TRANSACTION_IS_INCOME, TRANSACTION_EDIT_REASON,
            TRANSACTION_BUDGET, TRANSACTION_RECEIPT_PATH};

    public TransactionsTable(Context context) {
        super(context);
    }

    public long addNewTransaction(TransactionsData transaction) {
        Log.d(BeWiseConstants.LOG_TAG, "addNewTransaction(...");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TRANSACTION_ID, transaction.getTransactionID());
        values.put(TRANSACTION_RECURRING_ID, transaction.getRecurringID());
        values.put(TRANSACTION_DATE, DateUtilities.getTimestampFromDate(transaction.getDate()));
        values.put(TRANSACTION_UPDATE_DATE, DateUtilities.getTimestampFromDate(transaction.getUpdateDate()));
        values.put(TRANSACTION_START_DATE, DateUtilities.getTimestampFromDate(transaction.getStartDate()));
        values.put(TRANSACTION_CATEGORY, transaction.getCategory());
        values.put(TRANSACTION_DESCRIPTION, transaction.getDescription());
        values.put(TRANSACTION_AMOUNT, transaction.getAmount());
        values.put(TRANSACTION_IS_INCOME, transaction.isIncome() ? 1 : 0);
        values.put(TRANSACTION_EDIT_REASON, transaction.getModifyReason());
        values.put(TRANSACTION_BUDGET, transaction.getBudget());
        values.put(TRANSACTION_RECEIPT_PATH, transaction.getReceiptPath());
        long result = db.insert(TRANSACTION_TABLE, null, values);
        db.close();
        return result;
    }

    /**
     * To get a single item from transaction, use the transaction transaction ID.
     *
     * @param transactionID Transaction ID.
     * @return
     */
    public TransactionsData getTransactionItem(long transactionID) {
        Log.d(BeWiseConstants.LOG_TAG, "getTransactionItem(" + transactionID + ", ....");
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TRANSACTION_TABLE, TRANSACTIONS_TABLE_ARRAY, TRANSACTION_ID + "=?", new String[]{String.valueOf(transactionID)}, null,
                null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;
        TransactionsData transaction = new TransactionsData();
        transaction.setTransactionID(transactionID);
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
        db.close();
        cursor.close();
        return transaction;
    }

    /**
     * Get transaction (income, expense or both) balance based on give time period
     *
     * @param item
     * @param date
     * @return
     */
    public double getTransactionsBalance(SearchableItems item, String date) {
        Log.d(BeWiseConstants.LOG_TAG, "getTransactionsBalance(...");
        String selectQuery = "SELECT sum(" + TRANSACTION_AMOUNT + ") FROM " + TRANSACTION_TABLE;

        if (item.getValue().equals(SearchableItems.INCOME.getValue())) {
            selectQuery += " where " + TRANSACTION_AMOUNT + " >= 0 ";
        } else if (item.getValue().equals(SearchableItems.EXPENSES.getValue())) {
            selectQuery += " where " + TRANSACTION_AMOUNT + " < 0 ";
        }

        if (!"".equals(date)) {
            if (!selectQuery.contains(" where ")) {
                selectQuery += "where ";
            } else {
                selectQuery += "AND ";
            }
            selectQuery += TRANSACTION_DATE + " >= " + DateUtilities.getTimestampFromDate(date) + "";
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(BeWiseConstants.LOG_TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        double amount = 0.0;
        if (cursor != null && cursor.moveToFirst()) {
            amount = cursor.getDouble(0);
        } else {
            amount = -1;
        }
        db.close();
        cursor.close();
        return NumberUtilities.round(amount, 2);
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
    public List<TransactionsData> searchTransactionsTable(String searchableItems, String category, String startDate, String endDate, double minAmount,
                                                          double maxAmount, String recurringPeriod, String stringToSearch) {
        // TODO: FIX logging
        Log.d(BeWiseConstants.LOG_TAG, "searchTransactionsTable(" + searchableItems + ", " + startDate + ")");
        List<TransactionsData> transactionsList = new ArrayList<TransactionsData>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TRANSACTION_TABLE + " WHERE ";

        boolean empty = true;

        // Income and Expense Filter
        if (searchableItems.equalsIgnoreCase(SearchableItems.INCOME.getValue())) {
            selectQuery += TRANSACTION_AMOUNT + " >= 0 ";
            empty = false;
        } else if (searchableItems.equalsIgnoreCase(SearchableItems.EXPENSES.getValue())) {
            selectQuery += TRANSACTION_AMOUNT + " < 0 ";
            empty = false;
        }

        // Category filter
        if (category != null && !"ANY".equalsIgnoreCase(recurringPeriod)) {
            if (!empty) {
                selectQuery += " AND ";
            }
            selectQuery += TRANSACTION_CATEGORY + " IS '" + category + "' ";
            empty = false;
        }

        // date range filter
        if (startDate != null && endDate != null) {
            if (!empty) {
                selectQuery += " AND ";
            }
            selectQuery += TRANSACTION_START_DATE + " BETWEEN " + DateUtilities.getTimestampFromDate(startDate) + " AND "
                    + DateUtilities.getTimestampFromDate(endDate);
            empty = false;
        }

        // Amount range filter

        if (minAmount != 0.0 && maxAmount != 0.0) {
            if (!empty) {
                selectQuery += " AND ";
            }
            selectQuery += TRANSACTION_AMOUNT + " BETWEEN " + minAmount + " AND  " + maxAmount;
            empty = false;
        }

        // Recurring period filter
        // TODO: Big pain
        if (recurringPeriod != null && !"ANY".equalsIgnoreCase(recurringPeriod)) {
            if (!empty) {
                selectQuery += " AND ";
            }
            // TODO: Fix it
            selectQuery += TRANSACTION_RECURRING_ID + " IN " + " (select " + RECURRENCE_ID + " from " + RECURRENCE_TABLE + " where " + RECURRENCE_PERIOD
                    + " IS '" + recurringPeriod + "')";
            empty = false;
        }

        // String to search in
        if (stringToSearch != null && !"".equals(stringToSearch)) {
            if (!empty) {
                selectQuery += " AND ";
            }
            selectQuery += TRANSACTION_DESCRIPTION + " LIKE '" + stringToSearch + "' ";
            empty = false;
        }

        Log.d(BeWiseConstants.LOG_TAG, "Search Query: " + selectQuery);

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

    public List<TransactionsData> getAllTransactions() {
        Log.d(BeWiseConstants.LOG_TAG, "getAllTransactions(...");
        List<TransactionsData> transactionsList = new ArrayList<TransactionsData>();
        String selectQuery = "SELECT  * FROM " + TRANSACTION_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(BeWiseConstants.LOG_TAG, "Search Query: " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
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

    public int updateTransaction(TransactionsData transaction) {
        Log.d(BeWiseConstants.LOG_TAG, "updateTransaction(...");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TRANSACTION_DATE, DateUtilities.getTimestampFromDate(transaction.getStartDate()));
        values.put(TRANSACTION_CATEGORY, transaction.getCategory());
        values.put(TRANSACTION_AMOUNT, transaction.getAmount());
        values.put(TRANSACTION_IS_INCOME, transaction.isIncome());
        values.put(TRANSACTION_EDIT_REASON, transaction.getModifyReason());
        values.put(TRANSACTION_BUDGET, transaction.getBudget());
        int result = db.update(TRANSACTION_TABLE, values, TRANSACTION_ID + " = ?", new String[]{String.valueOf(transaction.getTransactionID())});
        db.close();
        return result;
    }

    /**
     * Delete multiple transaction items.
     *
     * @param transactionId
     */
    public void deleteSingleTransaction(long transactionId) {
        Log.d(BeWiseConstants.LOG_TAG, "deleteSingleTransaction(" + transactionId + ")");
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TRANSACTION_TABLE, TRANSACTION_ID + " = ?", new String[]{String.valueOf(transactionId)});
        db.close();
    }

    /**
     * Delete multiple transaction items.
     *
     * @param transactionId
     */
    public void deleteMultipleTransactions(long transactionId) {
        Log.d(BeWiseConstants.LOG_TAG, "deleteMultipleTransactions(" + transactionId + ")");
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TRANSACTION_TABLE, TRANSACTION_ID + " = ?", new String[]{String.valueOf(transactionId)});
        db.close();
    }

    /**
     * Get Budget utilized amount.
     *
     * @param budgetName
     * @param period
     * @param startDate
     * @param endDate
     * @return
     */
    public double getBudgetUtilizedAmount(String budgetName, String period, String startDate, String endDate) {
        Log.d(BeWiseConstants.LOG_TAG, "getBudgetUtilizedAmount(...");
        String selectQuery = "SELECT sum(" + TRANSACTION_AMOUNT + ") FROM " + TRANSACTION_TABLE;

        selectQuery += " where " + TRANSACTION_BUDGET + " IS '" + budgetName + "'";

        if (period != null && "".equals(period)) {
            if (!selectQuery.contains(" where ")) {
                selectQuery += " where ";
            } else {
                selectQuery += " AND ";
            }
            selectQuery += TRANSACTION_DATE + " BETWEEN " + DateUtilities.getTimestampFromDate(startDate) + " AND "
                    + DateUtilities.getTimestampFromDate(endDate);
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(BeWiseConstants.LOG_TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        double amount = 0.0;
        if (cursor.moveToFirst())
            amount = cursor.getDouble(0);
        else
            amount = -1;
        db.close();
        cursor.close();
        return NumberUtilities.round(amount, 2);
    }

    /**
     * Get the list of all the transactions in a budget based on a time period.
     *
     * @param budgetName      Name of the budget.
     * @param recurringPeriod Recurring period.
     * @param startDate       Start date of the Budget.
     * @param endDate         End date of the Budget.
     * @return List of transactions as TransactionData in the given budget.
     */
    public List<TransactionsData> getTransactionsInBudget(String budgetName, String recurringPeriod, String startDate, String endDate) {
        Log.d(this.getClass().toString(), "getTransactionsInBudget(String " + budgetName + ", String " + recurringPeriod + ", String " + startDate + ", String " + endDate + ")");
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TRANSACTION_TABLE;
        selectQuery += " where " + BUDGET_NAME + " = '" + budgetName + "'";

        if (recurringPeriod.equals("")) {
            selectQuery += " AND " + TRANSACTION_DATE + " BETWEEN " + DateUtilities.getTimestampFromDate(startDate) + " AND "
                    + DateUtilities.getTimestampFromDate(endDate);
        }

        Cursor cursor = db.rawQuery(selectQuery, null);
        List<TransactionsData> transactionsData = new ArrayList<TransactionsData>();
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
                transactionsData.add(transaction);
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return transactionsData;
    }
}
