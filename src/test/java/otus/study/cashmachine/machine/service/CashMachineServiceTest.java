package otus.study.cashmachine.machine.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.service.AccountService;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;
import otus.study.cashmachine.machine.data.CashMachine;
import otus.study.cashmachine.machine.data.MoneyBox;
import otus.study.cashmachine.machine.service.impl.CashMachineServiceImpl;

import java.math.BigDecimal;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.math.BigDecimal.TEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CashMachineServiceTest {

    @Spy
    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private CardsDao cardsDao;

    @Mock
    private AccountService accountService;

    @Mock
    private MoneyBoxService moneyBoxService;

    private CashMachineServiceImpl cashMachineService;

    private final CashMachine cashMachine = new CashMachine(new MoneyBox());

    @BeforeEach
    void init() {
        cashMachineService = new CashMachineServiceImpl(cardService, accountService, moneyBoxService);
    }


    @Test
    void getMoney() {
        doReturn(TEN).when(cardService).getMoney("1234", "123", TEN);

        cashMachineService.getMoney(cashMachine, "1234", "123", TEN);

        verify(cardService, times(1)).getMoney("1234", "123", TEN);
        verify(moneyBoxService, times(1)).getMoney(any(), anyInt());
    }

    @Test
    void putMoney() {
        doReturn(TEN).when(cardService).getBalance("1234", "0000");
        doReturn(BigDecimal.ONE).when(cardService).putMoney(eq("1234"), eq("0000"), any());

        BigDecimal actual = cashMachineService.putMoney(cashMachine, "1234", "0000", List.of(1, 3, 6, 1));

        assertEquals(BigDecimal.ONE, actual);
        verify(cardService, times(1)).getBalance("1234", "0000");
        verify(cardService, times(1)).putMoney(eq("1234"), eq("0000"), any());
        verify(moneyBoxService, times(1)).putMoney(any(), eq(1), eq(6), eq(3), eq(1));
    }

    @Test
    void checkBalance() {
        doReturn(TEN).when(cardService).getBalance("1234", "0000");

        BigDecimal actual = cashMachineService.checkBalance(cashMachine, "1234", "0000");

        assertEquals(TEN, actual);
    }

    @Test
    void changePin() {
        ArgumentCaptor<String> cardNum = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> oldPin = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> newPin = ArgumentCaptor.forClass(String.class);
        doReturn(TRUE).when(cardService).cnangePin(cardNum.capture(), oldPin.capture(), newPin.capture());

        boolean actual = cashMachineService.changePin("1234", "123", "newPin");

        assertEquals("1234", cardNum.getValue());
        assertEquals("123", oldPin.getValue());
        assertEquals("newPin", newPin.getValue());
        assertEquals(TRUE, actual);
    }

    @Test
    void changePinWithAnswer() {
        doAnswer(i -> {
            Object[] arguments = i.getArguments();
            if (arguments[0].equals("1234") && arguments[1].equals("123") && arguments[2].equals("newPin")) {
                return TRUE;
            }
            return FALSE;
        }).when(cardService).cnangePin(anyString(), anyString(), anyString());

        boolean actual = cashMachineService.changePin("1234", "123", "newPin");

        assertEquals(TRUE, actual);
    }
}