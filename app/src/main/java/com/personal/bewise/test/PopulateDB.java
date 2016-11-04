package com.personal.bewise.test;

import android.content.Context;

import com.personal.bewise.database.BudgetData;
import com.personal.bewise.database.BudgetTable;
import com.personal.bewise.database.PendingTransactionsTable;
import com.personal.bewise.database.RecurrenceTable;
import com.personal.bewise.database.TransactionsData;
import com.personal.bewise.database.TransactionsTable;
import com.personal.bewise.utils.DateUtilities;
import com.personal.bewise.utils.NumberUtilities;

import java.text.ParseException;
import java.util.Random;

/**
 * Class to put dummy data in DB.
 *
 * @author bilal
 */
public class PopulateDB {

    private static final double MAX_DOUBLE = 98765.4321;
    private static final int JUMP = 12345;
    private static final int MAX_TRANSACTIONS = 50;
    private String currentDate;
    private long transaction_id;
    private double amount;
    private boolean income;
    private String category;
    private String budget;
    private String recurringPeriod;
    private String dueDate;
    private String[] income_category = {"Salary", "Stocks", "Bonus", "Credit", "Sales", "Gift"};
    private String[] expense_category = {"Rent", "Car Insurance", "Health Insurance",
            "Utility Bills"};
    private String[] recurring_period = {"NONE", "DAY", "WEEK", "MONTH", "QUARTER", "HALF_YEAR",
            "YEAR"};
    private String[] budget_list = {"HOME", "FEE", "HOLIDAYS", "RESTURANT"};
    private Context context;

    /**
     * Constructor.
     *
     * @param context
     */
    public PopulateDB(Context context) {
        this.context = context;
    }

    private int generateRandomNumber(int limit) {
        Random r = new Random();
        int num = r.nextInt();
        num = num < 0 ? num * -1 : num;
        return num % limit;
    }

    private double generateRandomAmount() {
        Random r = new Random();
        Double num = r.nextDouble();
        num = num < 0 ? num * -1 : num;
        return NumberUtilities.round(num * MAX_DOUBLE, 2);
    }

    private boolean generateBoolean() {
        Random r = new Random();
        int num = r.nextInt();
        num = num < 0 ? num * -1 : num;
        return (num % 2 == 1);
    }

    private String getRandomItemFromArray(String[] categories) {
        Random r = new Random();
        int num = r.nextInt();
        num = num < 0 ? num * -1 : num;
        return categories[num % categories.length];
    }

    private String getRandomStartDate(long currentDate) throws ParseException {
        return DateUtilities.addDays(DateUtilities.getDateFromTimestamp(currentDate),
                generateRandomNumber(30));
    }

    public void populateDataBase() {

        currentDate = DateUtilities.getCurrentDate();

        transaction_id = System.currentTimeMillis();
        try {
            BudgetTable bt = new BudgetTable(context);
            BudgetData bd = new BudgetData();
            for (String aBudget_list : budget_list) {
                bd.setBudgetName(aBudget_list);
                bd.setBudgetAmount(generateRandomAmount());
                bd.setBudgetRecurrencePeriod(getRandomItemFromArray(recurring_period));
                bd.setBudgetDescription("Budget: " + aBudget_list);
                bd.setBudgetStartDate(DateUtilities.getCurrentDate());
                bt.addNewBudget(bd);
            }

            TransactionsData transaction = new TransactionsData();
            for (int i = 0; i < MAX_TRANSACTIONS; i++) {
                transaction_id += JUMP;
                transaction.setTransactionID(transaction_id);
                transaction.setDate(currentDate);
                transaction.setUpdateDate(currentDate);
                transaction.setIncome(generateBoolean());
                double amount = generateRandomAmount();
                if (amount < 0 && transaction.isIncome()) {
                    amount *= -1;
                } else if (amount > 0 && !transaction.isIncome()) {
                    amount *= -1;
                }
                transaction.setAmount(amount);
                transaction.setDescription("");
                if (!transaction.isIncome()) {
                    transaction.setBudget(getRandomItemFromArray(budget_list));
                    transaction.setCategory(getRandomItemFromArray(expense_category));
                } else {
                    transaction.setBudget("");
                    transaction.setCategory(getRandomItemFromArray(income_category));
                }
                recurringPeriod = getRandomItemFromArray(recurring_period);
                transaction.setRecurringPeriod(recurringPeriod);
                if (recurringPeriod.equalsIgnoreCase("NONE")) {
                    transaction.setRecurring(false);
                } else {
                    transaction.setRecurring(true);
                }
                transaction
                        .setStartDate(getRandomStartDate(DateUtilities.getTimestampFromDate(currentDate)));
                insertItemInDB(transaction, context);
            }

        } catch (ParseException pe) {

        }
    }

    /**
     * @param transaction
     * @param context
     */
    public void insertItemInDB(TransactionsData transaction, Context context) {
        if (transaction.isRecurring()) {
            transaction.setRecurringID(transaction.getTransactionID());

            if (DateUtilities.isAfterToday(transaction.getStartDate()) == -1) {
                // If the start date is after today, Set next due date to
                // start date, and only add to recurring transactions
                transaction.setNextDueDate(transaction.getStartDate());
                RecurrenceTable recurrenceTable = new RecurrenceTable(context);
                recurrenceTable.addNewRecurringItem(transaction);
            } else {
                // Otherwise calculate the next due date
                transaction.setNextDueDate(DateUtilities.getNextDueDate(transaction.getStartDate(),
                        DateUtilities.getRecurrencePeriodAsInt(transaction.getRecurringPeriod())));
                // Add transaction to both Recurrence and
                // Transactions tables
                RecurrenceTable recurrenceTable = new RecurrenceTable(context);
                recurrenceTable.addNewRecurringItem(transaction);
                TransactionsTable transactionsTable = new TransactionsTable(context);
                transactionsTable.addNewTransaction(transaction);
            }

        } else {

            if (DateUtilities.isAfterToday(transaction.getStartDate()) == -1) {
                PendingTransactionsTable pendingTransactionsTable = new PendingTransactionsTable(context);
                pendingTransactionsTable.addNewPendingTransaction(transaction);
            } else {
                TransactionsTable transactionsTable = new TransactionsTable(context);
                transactionsTable.addNewTransaction(transaction);
            }

        }
    }

}
