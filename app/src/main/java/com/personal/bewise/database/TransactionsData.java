package com.personal.bewise.database;


import java.io.Serializable;

public class TransactionsData implements Serializable {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -5057927675067121464L;

    /**
     * ID of the transaction, so that it can be modified later.
     */
    private long _transactionID;

    /**
     * ID of the recurring transaction. Only if the transaction is recurring.
     */
    private long _recurringID;

    /**
     * Date of transaction - ONLY DATE.
     */
    private String _date;

    /**
     * Date on which the transaction will be accounted.
     */
    private String _startDate;

    /**
     * Date on which the transaction will be accounted.
     */
    private String _lastAccountedDate;

    /**
     * Date on which the transaction will be accounted.
     */
    private String _nextDueDate;

    /**
     * Date on which transaction was updated.
     */
    private String _updateDate;

    /**
     * Amount received or spent.
     */
    private double _amount;

    /**
     * Turn the text to black for gains and red for losses.
     */
    private boolean _isIncome;

    /**
     * Transaction category like Salary, Bonus, Grocery.. etc .
     */
    private String _category;

    /**
     * Transaction description .
     */
    private String _description;

    /**
     * Budget associated with the transaction.
     */
    private String _budget;

    /**
     * Is amount recurring.
     */
    private boolean _recurring;

    /**
     * Recurrence period.
     */
    private String _recurringPeriod;

    /**
     * Reason to modify transaction.
     */
    private String _modifyReason;

    /**
     * Income/expense receipt.
     */
    private String _receiptPath;

    public TransactionsData() {
        _isIncome = true;
        _category = "";
        _description = "";
        _budget = "";
        _recurring = false;
        _recurringPeriod = "";
        _modifyReason = "";
        _receiptPath = "";

    }

    public String getDate() {
        return _date;
    }

    public void setDate(String date) {
        this._date = date;
    }

    public String getCategory() {
        return _category;
    }

    public void setCategory(String category) {
        this._category = category;
    }

    public double getAmount() {
        return _amount;
    }

    public void setAmount(double amount) {
        this._amount = amount;
    }

    public long getTransactionID() {
        return _transactionID;
    }

    public void setTransactionID(long transcationID) {
        this._transactionID = transcationID;
    }

    public boolean isIncome() {
        return _isIncome;
    }

    public void setIncome(boolean isIncome) {
        this._isIncome = isIncome;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        this._description = description;
    }

    public boolean isRecurring() {
        return _recurring;
    }

    public void setRecurring(boolean recurring) {
        this._recurring = recurring;
    }

    public String getRecurringPeriod() {
        return _recurringPeriod;
    }

    public void setRecurringPeriod(String recurringPeriod) {
        this._recurringPeriod = recurringPeriod;
    }

    public String getUpdateDate() {
        return _updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this._updateDate = updateDate;
    }

    public String getReceiptPath() {
        return _receiptPath;
    }

    public void setReceiptPath(String receiptPath) {
        this._receiptPath = receiptPath;
    }

    public String getModifyReason() {
        return _modifyReason;
    }

    public void setModifyReason(String updateReason) {
        this._modifyReason = updateReason;
    }

    public String getBudget() {
        return _budget;
    }

    public void setBudget(String budget) {
        this._budget = budget;
    }

    public String getStartDate() {
        return _startDate;
    }

    public void setStartDate(String startDate) {
        this._startDate = startDate;
    }

    public String getLastAccountedDate() {
        return _lastAccountedDate;
    }

    public void setLastAccountedDate(String _lastAccountedDate) {
        this._lastAccountedDate = _lastAccountedDate;
    }

    public String getNextDueDate() {
        return _nextDueDate;
    }

    public void setNextDueDate(String _nextDueDate) {
        this._nextDueDate = _nextDueDate;
    }

    public long getRecurringID() {
        return _recurringID;
    }

    public void setRecurringID(long _recurringID) {
        this._recurringID = _recurringID;
    }

}
