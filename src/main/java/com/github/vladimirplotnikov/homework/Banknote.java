package com.github.vladimirplotnikov.homework;

public class Banknote {
    private final BanknoteDenomination denomination;

    public Banknote(BanknoteDenomination nominal) {
        this.denomination = nominal;
    }

    public BanknoteDenomination getNominal() {
        return denomination;
    }

    @Override
    public String toString() {
        return "denomination=" + denomination.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Banknote banknote = (Banknote) o;
        return denomination == banknote.denomination;
    }
}