package com.github.vladimirplotnikov.homework;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AtmImpl implements ATM{
    private final CassetteAtmImpl cassetteAtmImpl;
    public AtmImpl(Map<BanknoteDenomination, Integer> moneyMap) {
        cassetteAtmImpl = new CassetteAtmImpl();
        moneyMap.forEach(this::putBanknotes);
    }
    //принимать банкноты разных номиналов
    @Override
    public void putBanknotes(BanknoteDenomination denomination, int quantity) {
        Cassette atmCassette = new Cassette(denomination, quantity);
        cassetteAtmImpl.addBanknoteToCassette(atmCassette);
    }
    //выдавать запрошенную сумму минимальным количеством банкнот или ошибку, если сумму нельзя выдать.
    @Override
    public List<Banknote> getBanknote(int amount) {
        if (amount <= 0) {
            throw new RuntimeException("Запрашиваемая сумма должна быть больше 0");
        }
        return getMoneyFromAtmRecursively(new ArrayList<>(), amount);
    }
    //выдавать сумму остатка денежных средств
    @Override
    public int getBalance() {
        return cassetteAtmImpl.getCassetteATMBalance();
    }

    private List<Banknote> getMoneyFromAtmRecursively(List<Banknote> money, int resultAmount) {
        if (resultAmount == 0) {
            return money;
        } else if (resultAmount < 0) {
            throw new RuntimeException("Сумму нельзя выдать");
        }
        if (getBalance() <= 0) {
            throw new RuntimeException("В банкомате нет денег");
        }

        Banknote banknote = getClosestToAmountBanknote(resultAmount);
        if (banknote == null) {
            throw new RuntimeException("Сумму нельзя выдать");
        }

        resultAmount = decreaseBanknoteQuantity(money, banknote, resultAmount);

        return getMoneyFromAtmRecursively(money, resultAmount);
    }

    private int decreaseBanknoteQuantity(List<Banknote> money, Banknote banknote, int resultAmount) {
        for (Cassette cassette : cassetteAtmImpl.getCassetteList()) {
            if (cassette.getBanknote().equals(banknote)) {
                money.add(banknote);
                cassette.setQuantity(cassette.getQuantity() - 1);
                resultAmount = resultAmount - banknote.getNominal().getValue();
            }
        }
        return resultAmount;
    }

    private Banknote getClosestToAmountBanknote(int amount) {
        Banknote banknote = null;
        for (Cassette cassette : cassetteAtmImpl.getCassetteList()) {
            if (cassette.getNominal().getValue() <= amount && cassette.getQuantity() > 0) {
                banknote = cassette.getBanknote();
            }
        }
        return banknote;
    }
}
