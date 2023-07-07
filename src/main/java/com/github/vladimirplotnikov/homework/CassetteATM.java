package com.github.vladimirplotnikov.homework;

import java.util.TreeSet;

public interface CassetteATM {
    TreeSet<Cassette> getCassetteList();

    void addBanknoteToCassette(Cassette cassette);

    int getCassetteATMBalance();
}
