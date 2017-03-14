package com.personal.bewise.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.personal.bewise.BeWiseConstants;

/**
 * @author bilal
 */

public abstract class TransactionItemsTable extends TransactionsDatabase {

    protected static final String TRANSACTION_TABLE = "transactions";
    protected static final String TRANSACTION_ID = "id";
    protected static final String TRANSACTION_RECURRING_ID = "recurrence_id";
    protected static final String TRANSACTION_DATE = "date";
    protected static final String TRANSACTION_UPDATE_DATE = "update_date";
    protected static final String TRANSACTION_START_DATE = "start_date";
    protected static final String TRANSACTION_CATEGORY = "category";
    protected static final String TRANSACTION_DESCRIPTION = "description";
    protected static final String TRANSACTION_AMOUNT = "amount";
    protected static final String TRANSACTION_IS_INCOME = "is_income";
    protected static final String TRANSACTION_EDIT_REASON = "edit_reason";
    protected static final String TRANSACTION_BUDGET = "budget";
    protected static final String TRANSACTION_RECEIPT_PATH = "recipt_path";

    protected static final String CREATE_TRANSACTION_TABLE = "CREATE TABLE IF NOT EXISTS " + TRANSACTION_TABLE + " (" + TRANSACTION_ID + INTEGER_PK + COMMA_SEP
            + TRANSACTION_RECURRING_ID + INTEGER_TYPE + COMMA_SEP + TRANSACTION_DATE + INTEGER_TYPE + COMMA_SEP + TRANSACTION_UPDATE_DATE + INTEGER_TYPE
            + COMMA_SEP + TRANSACTION_START_DATE + INTEGER_TYPE + COMMA_SEP + TRANSACTION_CATEGORY + TEXT_TYPE + COMMA_SEP + TRANSACTION_DESCRIPTION
            + TEXT_TYPE + COMMA_SEP + TRANSACTION_AMOUNT + REAL_TYPE + DEFAULT + DEFAULT_AMOUNT + COMMA_SEP + TRANSACTION_IS_INCOME + BOOL_TYPE + COMMA_SEP
            + TRANSACTION_EDIT_REASON + TEXT_TYPE + COMMA_SEP + TRANSACTION_BUDGET + TEXT_TYPE + COMMA_SEP + TRANSACTION_RECEIPT_PATH + TEXT_TYPE + " )";

    protected static final String RECURRENCE_TABLE = "recurrence";
    protected static final String RECURRENCE_ID = "recurrence_id";
    protected static final String RECURRENCE_DATE = "last_accounted_date";
    protected static final String RECURRENCE_DUE_DATE = "due_date";
    protected static final String RECURRENCE_PERIOD = "recurrence_period";
    protected static final String RECURRENCE_CATEGORY = "category";
    protected static final String RECURRENCE_DESCRIPTION = "description";
    protected static final String RECURRENCE_AMOUNT = "amount";
    protected static final String RECURRENCE_IS_INCOME = "is_income";
    protected static final String RECURRENCE_EDIT_REASON = "edit_reason";
    protected static final String RECURRENCE_BUDGET = "budget";

    protected static final String CREATE_RECURRENCE_TABLE = "CREATE TABLE IF NOT EXISTS " + RECURRENCE_TABLE + " (" + RECURRENCE_ID + INTEGER_PK + COMMA_SEP
            + RECURRENCE_DATE + INTEGER_TYPE + COMMA_SEP + RECURRENCE_DUE_DATE + INTEGER_TYPE + COMMA_SEP + RECURRENCE_PERIOD + TEXT_TYPE + COMMA_SEP
            + RECURRENCE_CATEGORY + TEXT_TYPE + COMMA_SEP + RECURRENCE_DESCRIPTION + TEXT_TYPE + COMMA_SEP + RECURRENCE_AMOUNT + REAL_TYPE + DEFAULT
            + DEFAULT_AMOUNT + COMMA_SEP + RECURRENCE_IS_INCOME + BOOL_TYPE + COMMA_SEP + RECURRENCE_EDIT_REASON + TEXT_TYPE + COMMA_SEP + RECURRENCE_BUDGET
            + TEXT_TYPE + " )";

    protected static final String BUDGET_TABLE = "budget";
    protected static final String BUDGET_ID = "id";
    protected static final String BUDGET_NAME = "budget";
    protected static final String BUDGET_CREATION_DATE = "creation_date";
    protected static final String BUDGET_START_DATE = "start_date";
    protected static final String BUDGET_DESCRIPTION = "description";
    protected static final String BUDGET_RECURRENCE_PERIOD = "recurrence_period";
    protected static final String BUDGET_AMOUNT = "amount_allocated";
    protected static final String BUDGET_AMOUNT_REMAINING = "amount_remaining";

    protected static final String CREATE_BUDGET_TABLE = "CREATE TABLE IF NOT EXISTS " + BUDGET_TABLE + " (" + BUDGET_ID + INTEGER_PK + COMMA_SEP + BUDGET_NAME
            + TEXT_TYPE + COMMA_SEP + BUDGET_CREATION_DATE + INTEGER_TYPE + COMMA_SEP + BUDGET_START_DATE + INTEGER_TYPE + COMMA_SEP + BUDGET_DESCRIPTION
            + TEXT_TYPE + COMMA_SEP + BUDGET_RECURRENCE_PERIOD + TEXT_TYPE + COMMA_SEP + BUDGET_AMOUNT + REAL_TYPE + DEFAULT + DEFAULT_AMOUNT + COMMA_SEP + BUDGET_AMOUNT_REMAINING + REAL_TYPE + DEFAULT + DEFAULT_AMOUNT + " )";

    /**
     * TODO: After everything is done the functionality of this table will be implemented.
     * <p/>
     * 1. Any transaction whose start date is after today will go to pending transaction table. It will be updated on that day or the day when pending
     * transactions table will be updated.
     * <p/>
     * 2. User can modify and delete the pending transaction.
     */

    protected static final String PENDING_TRANSACTIONS_TABLE = "pending_transactions";
    protected static final String PENDING_TRANSACTION_ID = "id";
    protected static final String PENDING_TRANSACTION_DATE = "date";
    protected static final String PENDING_TRANSACTION_CATEGORY = "category";
    protected static final String PENDING_TRANSACTION_DESCRIPTION = "description";
    protected static final String PENDING_TRANSACTION_AMOUNT = "amount";
    protected static final String PENDING_TRANSACTION_IS_INCOME = "is_income";
    protected static final String PENDING_TRANSACTION_BUDGET = "budget";
    protected static final String PENDING_TRANSACTION_RECEIPT_PATH = "recipt_path";

    protected static final String CREATE_PENDING_TRANSACTIONS_TABLE = "CREATE TABLE IF NOT EXISTS " + PENDING_TRANSACTIONS_TABLE + " ("
            + PENDING_TRANSACTION_ID + INTEGER_PK + COMMA_SEP + PENDING_TRANSACTION_DATE + INTEGER_TYPE + COMMA_SEP + PENDING_TRANSACTION_CATEGORY + TEXT_TYPE
            + COMMA_SEP + PENDING_TRANSACTION_DESCRIPTION + TEXT_TYPE + COMMA_SEP + PENDING_TRANSACTION_AMOUNT + REAL_TYPE + DEFAULT + DEFAULT_AMOUNT
            + COMMA_SEP + PENDING_TRANSACTION_IS_INCOME + BOOL_TYPE + COMMA_SEP + PENDING_TRANSACTION_BUDGET + TEXT_TYPE + COMMA_SEP
            + PENDING_TRANSACTION_RECEIPT_PATH + TEXT_TYPE + " )";

    public TransactionItemsTable(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(this.getClass().toString(), "onCreate(...");
        Log.d(this.getClass().toString(), CREATE_RECURRENCE_TABLE);
        db.execSQL(CREATE_RECURRENCE_TABLE);
        Log.d(this.getClass().toString(), CREATE_TRANSACTION_TABLE);
        db.execSQL(CREATE_TRANSACTION_TABLE);
        Log.d(this.getClass().toString(), CREATE_BUDGET_TABLE);
        db.execSQL(CREATE_BUDGET_TABLE);
        Log.d(this.getClass().toString(), CREATE_PENDING_TRANSACTIONS_TABLE);
        db.execSQL(CREATE_PENDING_TRANSACTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(this.getClass().toString(), "onUpgrade(...");
        Log.d(this.getClass().toString(), CREATE_RECURRENCE_TABLE);
        db.execSQL(CREATE_RECURRENCE_TABLE);
        Log.d(this.getClass().toString(), CREATE_TRANSACTION_TABLE);
        db.execSQL(CREATE_TRANSACTION_TABLE);
        Log.d(this.getClass().toString(), CREATE_BUDGET_TABLE);
        db.execSQL(CREATE_BUDGET_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(this.getClass().toString(), "onDowngrade( " + oldVersion + ", " + newVersion + ")");
        onUpgrade(db, oldVersion, newVersion);
    }

}
