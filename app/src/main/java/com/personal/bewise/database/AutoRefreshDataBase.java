package com.personal.bewise.database;

import android.content.Context;

import com.personal.bewise.utils.DateUtilities;
import com.personal.bewise.utils.RecurrencePeriod;

import java.util.List;

public class AutoRefreshDataBase {

    private Context context;

    public AutoRefreshDataBase(Context parentContext) {
        this.context = parentContext;

    }

    /**
     * Go to recurring and pending table, see if:
     * <p/>
     * 1. Some pending transactions are there to be accounted for. Remove the
     * pending transaction and move it to transaction table with date as
     * account/start date.
     * <p/>
     * 2. Some recurring transactions required to added to DB. Calculate how may
     * time it is required to be added to DB. Update the DB accordingly.
     *
     * @return True of passed otherwise false
     */
    public void enforceDataBaseConsistancy() {
        reviewPendingTransactions(context);
        reviewRecurringTransactions(context);
        reviewBudgets(context);
    }

    /**
     * TODO: Require some heavy testing.
     *
     * @param context
     */
    private void reviewPendingTransactions(Context context) {
        PendingTransactionsTable pendingTransactions = new PendingTransactionsTable(context);
        TransactionsTable transactionsTable = new TransactionsTable(context);
        List<TransactionsData> pendingTransactionsList = pendingTransactions.getAllPendingTransactions();
        for (TransactionsData transaction : pendingTransactionsList) {
            if (DateUtilities.isAfterToday(transaction.getStartDate()) != -1) {
                // This transaction is due
                // Add the transaction to transaction table.
                transactionsTable.addNewTransaction(transaction);
                // Remove the transaction from pending table.
                pendingTransactions.deleteSinglePendingTransaction(transaction.getTransactionID());
            }
        }
    }

    /**
     * TODO: Require some heavy testing.
     *
     * @param context
     */
    private void reviewRecurringTransactions(Context context) {
        RecurrenceTable recurrenceTable = new RecurrenceTable(context);
        TransactionsTable transactionsTable = new TransactionsTable(context);
        List<TransactionsData> recurringTransactionsList = recurrenceTable.getAllRecurringTransactions();
        for (TransactionsData transaction : recurringTransactionsList) {
            // TODO: consider case of accounting multiple transactions
            // Hopefully will not happen because the next due date is
            // changed as well which is not today
            if (DateUtilities.isAfterToday(transaction.getNextDueDate()) != -1) {
                long recurringCount = DateUtilities.overdueCount(transaction.getNextDueDate(), transaction.getRecurringPeriod());
                long currentTime = System.currentTimeMillis();
                for (long i = 0; i < recurringCount + 1; i++) {
                    currentTime += 1000;
                    transaction.setTransactionID(currentTime);
                    transaction.setLastAccountedDate(transaction.getNextDueDate());
                    transaction.setStartDate(transaction.getNextDueDate());
                    transaction.setNextDueDate(DateUtilities.getNextDueDate(transaction.getNextDueDate(),
                            DateUtilities.getRecurrencePeriodAsInt(transaction.getRecurringPeriod())));
                    transactionsTable.addNewTransaction(transaction);
                }
                transaction.setNextDueDate(DateUtilities.getNextDueDate(transaction.getNextDueDate(), 1));
                recurrenceTable.updateRecurringTransaction(transaction);
            }
        }
    }

    /**
     * Review budgets and update their start date according to current start
     * date and recurring period.
     *
     * @param context Context of the application.
     */
    private void reviewBudgets(Context context) {
        BudgetTable budgetTable = new BudgetTable(context);
        List<BudgetData> budgetsList = budgetTable.getAllBudgets();
        for (BudgetData budget : budgetsList) {
            String budgetStartDate = budget.getBudgetStartDate();
            String budgetRecurrencePeriod = budget.getBudgetRecurrencePeriod();
            if (!budgetRecurrencePeriod.equalsIgnoreCase(RecurrencePeriod.NONE.toString()) || DateUtilities.isAfterToday(budgetStartDate) != -1) {
                long recurringCount = DateUtilities.overdueCount(budget.getBudgetStartDate(), budget.getBudgetRecurrencePeriod());
                for (int i = 0; i < recurringCount; i++) {
                    String nextDueDate = DateUtilities.getNextDueDate(budget.getBudgetStartDate(),
                            DateUtilities.getRecurrencePeriodAsInt(budgetRecurrencePeriod));
                    if (DateUtilities.isAfterToday(nextDueDate) != -1) {
                        budget.setBudgetStartDate(nextDueDate);
                    }
                }
                budgetTable.updateBudget(budget);
            }
        }
    }
}
