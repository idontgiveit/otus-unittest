package com.github.vladimirplotnikov.homework;

public class Cassette {
    private final Banknote banknote;
    private int quantity;

    public Cassette(BanknoteDenomination nominalBanknote, int quantity) {
        this.banknote = new Banknote(nominalBanknote);
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity >= 0) {
            this.quantity = quantity;
        }else {
            throw new RuntimeException("Количество купюр в кассете не может быть отрицательным");
        }
    }

    public BanknoteDenomination getNominal(){
        return banknote.getNominal();
    }

    public Banknote getBanknote(){
        return banknote;
    }
}
