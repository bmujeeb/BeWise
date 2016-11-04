package com.personal.bewise.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enum RecurrencePeriod
 *
 * @author bilal
 */
public enum RecurrencePeriod {

    /**
     * Recurring period enums.
     */
    NONE(0), DAY(1), WEEK(7), MONTH(30), QUARTER(91), HALF_YEAR(183), YEAR(365);

    /**
     * Value of enum.
     */
    private int value;

    /**
     * Default constructor.
     *
     * @param value
     */
    RecurrencePeriod(int value) {
        this.value = value;
    }

    /**
     * @return
     */
    public static List<RecurrencePeriod> getEnums() {
        RecurrencePeriod[] states = values();
        List<RecurrencePeriod> names = new ArrayList<RecurrencePeriod>();
        Collections.addAll(names, states);
        return names;
    }

    public static List<String> getEnumNames() {
        RecurrencePeriod[] states = values();
        List<String> names = new ArrayList<String>();
        for(RecurrencePeriod period: states) {
            names.add(period.name());
        }
        return names;
    }

    public static List<String> getEnumBoundList(String budgetBound) {
        int bound = RecurrencePeriod.valueOf(budgetBound).getValue();
        RecurrencePeriod[] states = values();
        List<String> names = new ArrayList<String>();
        for (RecurrencePeriod state : states) {
            if (state.getValue() <= bound) {
                names.add(state.name());
            }
        }
        return names;
    }

    /**
     * Get enum value.
     *
     * @return Enum value.
     */
    public int getValue() {
        return value;
    }
}
