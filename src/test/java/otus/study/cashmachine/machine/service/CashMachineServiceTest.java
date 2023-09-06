package otus.study.cashmachine.machine.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import otus.study.cashmachine.TestUtil;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.data.Card;
import otus.study.cashmachine.bank.service.AccountService;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;
import otus.study.cashmachine.machine.data.CashMachine;
import otus.study.cashmachine.machine.data.MoneyBox;
import otus.study.cashmachine.machine.service.impl.CashMachineServiceImpl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CashMachineServiceTest {

    @Spy
    @InjectMocks
    private CardServiceImpl cardServiceImpl;
    @Mock
    private CardsDao cardsDao;
    @Mock
    private AccountService accountService;
    @Mock
    private MoneyBoxService moneyBoxService;
    private CashMachineServiceImpl cashMachineServiceImpl;
    private BigDecimal amount;
    private List<Integer> papers;
    private MoneyBox moneyBox;
    private CashMachine cashMachine;
    private Card testedCard;
    private String cardNum;
    private String currentPin;
    private String newPin;

    @BeforeEach
    void init() {
        cardNum = "88002323";
        currentPin = "1234";
        newPin = "9999";
        amount = new BigDecimal(10000);
        papers = Arrays.asList(1, 3, 2, 0);
        moneyBox = new MoneyBox();
        cashMachine = new CashMachine(moneyBox);
        cashMachineServiceImpl = new CashMachineServiceImpl(cardServiceImpl, accountService, moneyBoxService);
        testedCard = new Card(1L, "1111", 10L, TestUtil.getHash("1234"));
    }

    @Test
    void getMoney() {
        doReturn(amount).when(cardServiceImpl).getMoney(cardNum, currentPin, amount);
        when(moneyBoxService.getMoney(moneyBox, amount.intValue())).thenReturn(papers);
        List<Integer> resultPapers = cashMachineServiceImpl.getMoney(cashMachine, cardNum, currentPin, amount);
        Assertions.assertEquals(papers, resultPapers);
    }

    @Test
    void putMoney() {
        when(cardsDao.getCardByNumber(any())).thenReturn(testedCard);
        doNothing().when(moneyBoxService).putMoney(any(MoneyBox.class), anyInt(), anyInt(), anyInt(), anyInt());
        BigDecimal expectedAmount = new BigDecimal(6600);
        when(cardServiceImpl.putMoney("1111", "1234", expectedAmount)).thenReturn(expectedAmount);
        BigDecimal resultAmount = cashMachineServiceImpl.putMoney(cashMachine, "1111", "1234", Arrays.asList(1, 1, 1, 1));
        Assertions.assertEquals(expectedAmount, resultAmount);
    }

    @Test
    void checkBalance() {

        when(cardsDao.getCardByNumber(any())).thenReturn(new Card(2L, cardNum + "1", 10L, TestUtil.getHash(currentPin)));
        when(cardsDao.getCardByNumber(cardNum)).thenReturn(testedCard);

        when(accountService.checkBalance(any())).thenReturn(new BigDecimal(0));
        when(accountService.checkBalance(testedCard.getId())).thenReturn(amount);

        BigDecimal resultAmount1 = cashMachineServiceImpl.checkBalance(cashMachine, cardNum, currentPin);
        Assertions.assertEquals(resultAmount1, amount);

        BigDecimal resultAmount2 = cashMachineServiceImpl.checkBalance(cashMachine, cardNum + "1", currentPin);
        Assertions.assertNotEquals(resultAmount2, amount);
    }

    @Test
    void changePin() {

        when(cardsDao.getCardByNumber(cardNum)).thenReturn(testedCard);

        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
        when(cardsDao.saveCard(captor.capture())).thenReturn(null);

        cashMachineServiceImpl.changePin(cardNum, currentPin, newPin);
        Assertions.assertEquals(captor.getValue().getPinCode(), TestUtil.getHash(newPin));
    }

    @Test
    void changePinWithAnswer() {

        when(cardsDao.getCardByNumber(cardNum)).thenReturn(testedCard);
        final Card[] savedCards = new Card[1];
        when(cardsDao.saveCard(any(Card.class))).thenAnswer((Answer<Card>) invocation -> savedCards[0] = invocation.getArgument(0));
        cashMachineServiceImpl.changePin(cardNum, currentPin, newPin);
        Assertions.assertEquals(savedCards[0].getPinCode(), TestUtil.getHash(newPin));
    }
}
