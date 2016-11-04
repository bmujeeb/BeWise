package com.personal.bewise.database;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class TransactionsDatabase extends SQLiteOpenHelper {

    /**
     * Database name.
     */
    public static final String APPLICATION_DB_NAME = "transactionsDatabase.db";
    public static final String APPLICATION_DB_LOCATION = "//data//data//com.personal.bewise//databases";
    /**
     * Database version.
     */
    protected static final int DATABASE_VERSION = 1;
    /**
     * Database blob data type.
     */
    protected static final String INTEGER_TYPE = " INTEGER";

    /**
     * Database text data type.
     */
    protected static final String TEXT_TYPE = " TEXT";

    /**
     * Database real data type.
     */
    protected static final String REAL_TYPE = " REAL";

    /**
     * Database null data type.
     */
    protected static final String NULL_TYPE = " NULL";

    /**
     * Database blob data type.
     */
    protected static final String BLOB_TYPE = " BLOB";

    /**
     * Database boolean data type - since sqlite does not support boolean, integer is used.
     */
    protected static final String BOOL_TYPE = " INTEGER";

    /**
     * Database comma delimiter.
     */
    protected static final String COMMA_SEP = ",";

    // LOGIN table name
    protected static final String LOGIN_TABLE = "login";

    protected static final String INTEGER_PK = " INTEGER PRIMARY KEY";

    protected static final String DEFAULT = " DEFAULT";

    protected static final String DEFAULT_AMOUNT = " 0";

    public TransactionsDatabase(Context context) {
        super(context, APPLICATION_DB_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

}
