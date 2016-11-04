package com.personal.bewise.database;

import com.personal.bewise.utils.DateUtilities;

import java.io.Serializable;

public class BudgetData implements Serializable{

    private static final long serialVersionUID = -1118480538705788666L;

    private String _budgetName;

    private String _budgetDate;

    private String _budgetStartDate;

    private String _budgetDescription;

    private double _budgetAmount;

    private double _remainingAmount;

    private String _budgetRecurrencePeriod;

    public BudgetData() {
        _budgetDate = DateUtilities.getCurrentDate();
        _budgetStartDate = DateUtilities.getCurrentDate();
        _budgetAmount = 0.0;
    }

    public String getBudgetName() {
        return _budgetName;
    }

    public void setBudgetName(String budgetName) {
        this._budgetName = budgetName == null ? "" : budgetName;
    }

    public String getBudgetDate() {
        return _budgetDate;
    }

    public void setBudgetDate(String budgetDate) {
        this._budgetDate = budgetDate;
    }

    public String getBudgetStartDate() {
        return _budgetStartDate;
    }

    public void setBudgetStartDate(String budgetStartDate) {
        this._budgetStartDate = budgetStartDate;
    }

    public String getBudgetDescription() {
        return _budgetDescription;
    }

    public void setBudgetDescription(String budgetDescription) {
        this._budgetDescription = budgetDescription == null ? "" : budgetDescription;
    }

    public double getBudgetAmount() {
        return _budgetAmount;
    }

    public void setBudgetAmount(double budgetAmount) {
        this._budgetAmount = budgetAmount;
    }

    public double getBudgetRemainingAmount() {
        return _remainingAmount;
    }

    public void setBudgetRemainingAmount(double _remainingAmount) {
        this._remainingAmount = _remainingAmount;
    }

    public String getBudgetRecurrencePeriod() {
        return _budgetRecurrencePeriod;
    }

    public void setBudgetRecurrencePeriod(String budgetRecurrencePeriod) {
        this._budgetRecurrencePeriod = budgetRecurrencePeriod;
    }

}
