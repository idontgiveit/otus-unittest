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
import otus.study.cashmachine.bank.data.Card;
import otus.study.cashmachine.bank.service.AccountService;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;
import otus.study.cashmachine.machine.data.CashMachine;
import otus.study.cashmachine.machine.data.MoneyBox;
import otus.study.cashmachine.machine.service.impl.CashMachineServiceImpl;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

    private CashMachine cashMachine = new CashMachine(new MoneyBox());

    @BeforeEach
    void init() {
        cashMachineService = new CashMachineServiceImpl(cardService, accountService, moneyBoxService);
    }


    @Test
    void getMoney() {
        String cardNum = "1234567890";
        String pin = "231312";
        BigDecimal amount = BigDecimal.TEN;
        List<Integer> expectedResult = List.of(1, 2, 3);
        doReturn(BigDecimal.ZERO).when(cardService).getMoney(cardNum, pin, amount);
        when(moneyBoxService.getMoney(any(), anyInt())).thenReturn(expectedResult);

        List<Integer> result = cashMachineService.getMoney(cashMachine, cardNum, pin, amount);

        assertEquals(expectedResult, result);
        verify(moneyBoxService).getMoney(any(), anyInt());
    }

    @Test
    void putMoney() {
        String pin = "1111";
        String number = "1111";
        BigDecimal expectedBalance = BigDecimal.ZERO;
        doReturn(BigDecimal.TEN).when(cardService).getBalance(any(), any());
        when(accountService.putMoney(anyLong(), any())).thenReturn(expectedBalance);
        when(cardsDao.getCardByNumber(any())).thenReturn(createCard(number, pin));

        BigDecimal result = cashMachineService.putMoney(cashMachine, number, pin, List.of(1, 2, 3));

        assertEquals(expectedBalance, result);
        verify(moneyBoxService).putMoney(any(), anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void checkBalance() {
        String pin = "1111";
        String number = "1111";
        BigDecimal expectedBalance = BigDecimal.ZERO;
        when(accountService.checkBalance(anyLong())).thenReturn(expectedBalance);
        when(cardsDao.getCardByNumber(any())).thenReturn(createCard(number, pin));

        BigDecimal result = cashMachineService.checkBalance(cashMachine, number, pin);

        assertEquals(expectedBalance, result);
        verify(cardService).getBalance(number, pin);
    }

    @Test
    void changePin() {
        String cardNum = "1234567890";
        String oldPin = "231312";
        String newPin = "231312";
        doReturn(true).when(cardService).cnangePin(cardNum, oldPin, newPin);

        boolean result = cashMachineService.changePin(cardNum, oldPin, newPin);

        assertTrue(result);
        ArgumentCaptor<String> changePinCapture = ArgumentCaptor.forClass(String.class);
        verify(cardService, only()).cnangePin(changePinCapture.capture(), changePinCapture.capture(), changePinCapture.capture());
        assertEquals(changePinCapture.getAllValues(), List.of(cardNum, oldPin, newPin));
    }

    @Test
    void changePinWithAnswer() {
        String cardNum = "1234567890";
        String oldPin = "231312";
        String newPin = "12345";
        Card card = createCard(cardNum, oldPin);
        when(cardsDao.getCardByNumber(any())).thenAnswer(it -> card);

        boolean result = cashMachineService.changePin(cardNum, oldPin, newPin);

        assertTrue(result);
        verify(cardService, only()).cnangePin(any(), any(), any());
    }

    @Test
    void changePinShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> cashMachineService.changePin("1", "1234", "5678"));
    }

    private Card createCard(String number, String pin) {
        return new Card(1, number, 1L, getHash(pin));
    }

    private String getHash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            digest.update(value.getBytes());
            String result = HexFormat.of().formatHex(digest.digest());
            return result;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}