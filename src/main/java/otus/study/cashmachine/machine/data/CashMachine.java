package otus.study.cashmachine.machine.data;

import lombok.Getter;

@Getter
public class CashMachine {
    private MoneyBox moneyBox;

    public CashMachine(final MoneyBox moneyBox) {
        this.moneyBox = moneyBox;
    }

    public void setMoneyBox(final MoneyBox moneyBox) {
        this.moneyBox = moneyBox;
    }
}
