package otus.study.cashmachine.API;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import otus.study.cashmachine.machine.data.CashMachine;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ATMRequest {

    private String cardNum;
    private String pin;
    private BigDecimal amount;
    int count5000;
    int count1000;
    int count500;
    int count100;
    private CashMachine machine;
    private String newPin;

}
