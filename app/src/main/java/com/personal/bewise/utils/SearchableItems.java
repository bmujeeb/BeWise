package com.personal.bewise.utils;

public enum SearchableItems {

    INCOME_AND_EXPENSES("Incomes and Expense"), INCOME("Income"), EXPENSES("Expenses"), BUDGETS("Budgets"), PENDING(
            "Pending"), RECURRING("Recurring");

    private String _value;

    SearchableItems(String value) {
        this._value = value;
    }

    public String getValue() {
        return _value;
    }

}
