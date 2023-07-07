package com.github.vladimirplotnikov.homework;

import java.util.List;

//Объект класса АТМ должен уметь
public interface ATM {
    //принимать банкноты разных номиналов
    void putBanknotes(BanknoteDenomination denomination, int quantity);
    //выдавать запрошенную сумму минимальным количеством банкнот или ошибку, если сумму нельзя выдать.
    List<Banknote> getBanknote(int amount);
    //выдавать сумму остатка денежных средств
    int getBalance();
}
