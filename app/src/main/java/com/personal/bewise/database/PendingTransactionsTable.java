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

public class PendingTransactionsTable extends TransactionItemsTable {

    private static final String[] PENDING_TRANSACTIONS_TABLE_ARRAY = {PENDING_TRANSACTION_ID, PENDING_TRANSACTION_DATE, PENDING_TRANSACTION_CATEGORY,
            PENDING_TRANSACTION_DESCRIPTION, PENDING_TRANSACTION_AMOUNT, PENDING_TRANSACTION_IS_INCOME, PENDING_TRANSACTION_BUDGET,
            PENDING_TRANSACTION_RECEIPT_PATH};

    public PendingTransactionsTable(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public long addNewPendingTransaction(TransactionsData transaction) {
        Log.d(BeWiseConstants.LOG_TAG, "addNewPendingTransaction(...");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PENDING_TRANSACTION_ID, transaction.getTransactionID());
        values.put(PENDING_TRANSACTION_DATE, DateUtilities.getTimestampFromDate(transaction.getStartDate()));
        values.put(PENDING_TRANSACTION_CATEGORY, transaction.getCategory());
        values.put(PENDING_TRANSACTION_DESCRIPTION, transaction.getDescription());
        values.put(PENDING_TRANSACTION_AMOUNT, transaction.getAmount());
        values.put(PENDING_TRANSACTION_IS_INCOME, transaction.isIncome() ? 1 : 0);
        values.put(PENDING_TRANSACTION_BUDGET, transaction.getBudget());
        values.put(PENDING_TRANSACTION_RECEIPT_PATH, transaction.getReceiptPath());
        long result = db.insert(PENDING_TRANSACTIONS_TABLE, null, values);
        db.close();
        return result;
    }

    /**
     * To get a single item from pending transactions table, use the transaction ID.
     *
     * @param transactionID Transaction ID.
     * @return
     */
    public TransactionsData getPendingTransactionItem(long transactionID) {
        Log.d(BeWiseConstants.LOG_TAG, "getPendingTransactionItem(" + transactionID + ", ....");
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(PENDING_TRANSACTIONS_TABLE, PENDING_TRANSACTIONS_TABLE_ARRAY, PENDING_TRANSACTION_ID + "=?",
                new String[]{String.valueOf(transactionID)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;
        TransactionsData transaction = new TransactionsData();
        transaction.setTransactionID(transactionID);
        transaction.setStartDate(DateUtilities.getDateFromTimestamp(cursor.getLong(1)));
        transaction.setCategory(cursor.getString(2));
        transaction.setDescription(cursor.getString(3));
        transaction.setAmount(cursor.getDouble(4));
        transaction.setIncome(cursor.getInt(5) == 1);
        transaction.setBudget(cursor.getString(6));
        transaction.setReceiptPath(cursor.getString(7));
        db.close();
        cursor.close();
        return transaction;
    }

    public List<TransactionsData> getAllPendingTransactions() {
        Log.d(BeWiseConstants.LOG_TAG, "getAllPendingTransactions(...");
        List<TransactionsData> transactionsList = new ArrayList<TransactionsData>();
        String selectQuery = "SELECT  * FROM " + PENDING_TRANSACTIONS_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                TransactionsData transaction = new TransactionsData();
                transaction.setTransactionID(cursor.getLong(0));
                transaction.setStartDate(DateUtilities.getDateFromTimestamp(cursor.getLong(1)));
                transaction.setCategory(cursor.getString(2));
                transaction.setDescription(cursor.getString(3));
                transaction.setAmount(cursor.getDouble(4));
                transaction.setIncome(cursor.getInt(5) == 1);
                transaction.setBudget(cursor.getString(6));
                transaction.setReceiptPath(cursor.getString(7));
                transactionsList.add(transaction);
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return transactionsList;
    }

    public int updatePendingTransaction(TransactionsData transaction) {
        Log.d(BeWiseConstants.LOG_TAG, "updatePendingTransaction(...");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PENDING_TRANSACTION_DATE, DateUtilities.getTimestampFromDate(transaction.getStartDate()));
        values.put(PENDING_TRANSACTION_CATEGORY, transaction.getCategory());
        values.put(PENDING_TRANSACTION_AMOUNT, transaction.getAmount());
        values.put(PENDING_TRANSACTION_IS_INCOME, transaction.isIncome());
        values.put(PENDING_TRANSACTION_BUDGET, transaction.getBudget());
        int result = db.update(PENDING_TRANSACTIONS_TABLE, values, PENDING_TRANSACTION_ID + " = ?",
                new String[]{String.valueOf(transaction.getTransactionID())});
        db.close();
        return result;
    }

    public int updatePendingTransaction(String columnName, String currentColumnValue, String newColumnValue) {
        Log.d(BeWiseConstants.LOG_TAG, "updatePendingTransaction(...");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(columnName, newColumnValue);

        int result = db.update(PENDING_TRANSACTIONS_TABLE, values, columnName + " = ?",
                new String[]{currentColumnValue});

        db.close();
        return result;
    }

    /**
     * Delete multiple pending transaction items.
     *
     * @param transactionId
     */
    public void deleteSinglePendingTransaction(long transactionId) {
        Log.d(BeWiseConstants.LOG_TAG, "deleteSinglePendingTransaction(" + transactionId + ")");
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PENDING_TRANSACTIONS_TABLE, PENDING_TRANSACTION_ID + " = ?", new String[]{String.valueOf(transactionId)});
        db.close();
    }

    /**
     * @param searchableItems
     * @param startDate
     * @param endDate
     * @param minAmount
     * @param maxAmount
     * @param stringToSearch
     * @return
     */
    public List<TransactionsData> searchPendingTransactionsTable(String searchableItems, String startDate, String endDate, double minAmount, double maxAmount,
                                                                 String stringToSearch) {
        // TODO: FIX logging
        Log.d(BeWiseConstants.LOG_TAG, "searchPendingTransactionsTable(..." + searchableItems + ", " + startDate + ")");
        List<TransactionsData> transactionsList = new ArrayList<TransactionsData>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + PENDING_TRANSACTIONS_TABLE + " WHERE ";

        boolean empty = true;

        // Income and Expense Filter
        if (searchableItems.equalsIgnoreCase(SearchableItems.INCOME.getValue())) {
            selectQuery += PENDING_TRANSACTION_AMOUNT + " >= 0 ";
            empty = false;
        } else if (searchableItems.equalsIgnoreCase(SearchableItems.EXPENSES.getValue())) {
            selectQuery += PENDING_TRANSACTION_AMOUNT + " < 0 ";
            empty = false;
        }

        // date range filter
        if (startDate != null && endDate != null) {
            if (!empty) {
                selectQuery += " AND ";
            }
            selectQuery += PENDING_TRANSACTION_DATE + " BETWEEN " + DateUtilities.getTimestampFromDate(startDate) + " AND "
                    + DateUtilities.getTimestampFromDate(endDate);
            empty = false;
        }

        // Amount range filter

        if (minAmount != 0.0 && maxAmount != 0.0) {
            if (!empty) {
                selectQuery += " AND ";
            }
            selectQuery += PENDING_TRANSACTION_AMOUNT + " BETWEEN " + minAmount + " AND  " + maxAmount;
            empty = false;
        }

        // String to search in
        if (stringToSearch != null && !"".equals(stringToSearch)) {
            if (!empty) {
                selectQuery += " AND ";
            }
            selectQuery += PENDING_TRANSACTION_DESCRIPTION + " LIKE '" + stringToSearch + "' ";
            empty = false;
        }

        Log.d(this.getClass().toString(), "Query: " + selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TransactionsData transaction = new TransactionsData();
                transaction.setTransactionID(cursor.getLong(0));
                transaction.setStartDate(DateUtilities.getDateFromTimestamp(cursor.getLong(1)));
                transaction.setCategory(cursor.getString(2));
                transaction.setDescription(cursor.getString(3));
                transaction.setAmount(cursor.getDouble(4));
                transaction.setIncome(cursor.getInt(5) == 1);
                transaction.setBudget(cursor.getString(6));
                transaction.setReceiptPath(cursor.getString(7));
                transactionsList.add(transaction);
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return transactionsList;
    }
}
