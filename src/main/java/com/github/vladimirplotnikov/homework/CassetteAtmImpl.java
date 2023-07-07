package com.github.vladimirplotnikov.homework;

import java.util.Comparator;
import java.util.TreeSet;

public class CassetteAtmImpl implements CassetteATM{
    private final TreeSet<Cassette> cassetteList;

    public CassetteAtmImpl() {
        cassetteList = new TreeSet<>(Comparator.comparingInt(c -> {
            return c.getNominal().getValue();
        }));
    }

    public TreeSet<Cassette> getCassetteList() {
        return cassetteList;
    }

    public void addBanknoteToCassette(Cassette cassette) {
        cassetteList.add(cassette);
    }

    public int getCassetteATMBalance() {
        int sum = 0;
        for (Cassette c : cassetteList) {
            int i = c.getNominal().getValue() * c.getQuantity();
            sum += i;
        }
        return sum;
    }
}
