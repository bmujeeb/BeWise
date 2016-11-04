package com.personal.bewise.utils;

public enum TransactionDetailsMode {

    INCOME("Income Detail"), EXPENSE("Expense Detail"), PENDING("Pending Transaction"), RECURRING("Recurring Transaction");

    private String value;

    TransactionDetailsMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
