package com.github.vladimirplotnikov.homework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            Map<BanknoteDenomination, Integer> moneyMap = new HashMap<>();
            moneyMap.put(BanknoteDenomination.HUNDRED, 10);
            moneyMap.put(BanknoteDenomination.FIVE_HUNDRED, 1);
            moneyMap.put(BanknoteDenomination.THOUSAND, 3);
            moneyMap.put(BanknoteDenomination.FIVE_THOUSAND, 1);

            AtmImpl atm = new AtmImpl(moneyMap);

            System.out.println("Баланс " + atm.getBalance());

            List<Banknote> money1 = atm.getBanknote(700);
            System.out.println(money1);
            System.out.println("Баланс " + atm.getBalance());
            atm.getBanknote(1900);
            System.out.println("Баланс " + atm.getBalance());
            atm.getBalance();
        } catch (RuntimeException e) {
            System.out.println(e);
        }
    }
}