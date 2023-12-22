package otus.study.cashmachine.machine.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.TestUtil;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.data.Card;
import otus.study.cashmachine.bank.service.AccountService;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;
import otus.study.cashmachine.machine.data.CashMachine;
import otus.study.cashmachine.machine.data.MoneyBox;
import otus.study.cashmachine.machine.service.impl.CashMachineServiceImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

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
// @TODO create get money test using spy as mock
        Card card = new Card(1L, "1111", 1L, TestUtil.getHash("0000"));
        when(cardsDao.getCardByNumber(any())).thenReturn(card);
        cashMachineService.getMoney(cashMachine, "1111", "0000", new BigDecimal(6600));
        Mockito.verify(cardService).getMoney("1111", "0000", new BigDecimal(6600));
        Mockito.verify(moneyBoxService).getMoney(cashMachine.getMoneyBox(), 6600);
    }

    @Test
    void putMoney() {
        Card card = new Card(1L, "1111", 1L, TestUtil.getHash("0000"));
        when(cardsDao.getCardByNumber(any())).thenReturn(card);
        List <Integer> notes = List.of(3,1,0,5);
        cashMachineService.putMoney(cashMachine,"1111", "0000", notes);
        Mockito.verify(moneyBoxService).putMoney(cashMachine.getMoneyBox(), notes.get(3), notes.get(2),notes.get(1),notes.get(0));
        Mockito.verify(cardService).putMoney("1111", "0000", new BigDecimal(16500));
    }

    @Test
    void checkBalance() {
       doReturn(new BigDecimal(16500)).when(cardService).getBalance(any(), any());
        BigDecimal expected = new BigDecimal(16500);
        BigDecimal actual =  cashMachineService.checkBalance(cashMachine, "1111", TestUtil.getHash("0000"));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void changePin() {
// @TODO create change pin test using spy as implementation and ArgumentCaptor and thenReturn
        Card card = new Card(1L, "1111", 1L, TestUtil.getHash("0000"));
        ArgumentCaptor <String> captor = ArgumentCaptor.forClass(String.class);
        when(cardsDao.getCardByNumber(captor.capture())).thenReturn(card);
        cashMachineService.changePin("1111", "0000", "1111");
        Mockito.verify(cardService).cnangePin("1111", "0000", "1111");
        String expectedNewPin = "1111";
        String actualNewPin = card.getPinCode();
        Assertions.assertEquals(TestUtil.getHash(expectedNewPin), actualNewPin);
    }

    @Test
    void changePinWithAnswer() {
// @TODO create change pin test using spy as implementation and mock an thenAnswer
        Card card = new Card(1L, "1111", 1L, TestUtil.getHash("0000"));
        when(cardsDao.getCardByNumber(any())).thenAnswer(invocation -> card);
        cashMachineService.changePin("1111", "0000", "1111");
        Mockito.verify(cardService).cnangePin("1111", "0000", "1111");
        String expectedNewPin = "1111";
        String actualNewPin = card.getPinCode();
        Assertions.assertEquals(TestUtil.getHash(expectedNewPin), actualNewPin);
    }
}