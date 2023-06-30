package com.github.vladimirplotnikov.homework;

public enum BanknoteDenomination {
    HUNDRED(100),
    FIVE_HUNDRED(500),
    THOUSAND(1000),
    FIVE_THOUSAND(5000);
    private final int value;

    BanknoteDenomination(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
