package otus.study.cashmachine.machine.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import otus.study.cashmachine.TestUtil;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.data.Card;
import otus.study.cashmachine.bank.service.AccountService;
import otus.study.cashmachine.bank.service.CardService;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;
import otus.study.cashmachine.machine.data.CashMachine;
import otus.study.cashmachine.machine.data.MoneyBox;
import otus.study.cashmachine.machine.service.impl.CashMachineServiceImpl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
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
        cashMachineService = new CashMachineServiceImpl(cardService, accountService, moneyBoxService);
    }


    @Test
    void getMoney() {
// @TODO create get money test using spy as mock
        doReturn(amount).when(cardService).getMoney(cardNum, currentPin, amount);

        when(moneyBoxService.getMoney(moneyBox, amount.intValue())).thenReturn(papers);

        List<Integer> resultPapers = cashMachineService.getMoney(cashMachine, cardNum, currentPin, amount);
        Assertions.assertEquals(papers, resultPapers);
    }

    @Test
    void putMoney() {
        testedCard = new Card(1L, "1111", 10L, TestUtil.getHash("1234"));
        when(cardsDao.getCardByNumber(any())).thenReturn(testedCard);

        doNothing().when(moneyBoxService).putMoney(any(MoneyBox.class), anyInt(), anyInt(), anyInt(), anyInt());

        BigDecimal expectedAmount = new BigDecimal(6600);
        when(cardService.putMoney("1111", "1234", expectedAmount)).thenReturn(expectedAmount);

        BigDecimal resultAmount = cashMachineService.putMoney(cashMachine, "1111", "1234", Arrays.asList(1, 1, 1, 1));
        Assertions.assertEquals(expectedAmount, resultAmount);
    }

    @Test
    void checkBalance() {
        testedCard = new Card(1L, cardNum, 10L, TestUtil.getHash(currentPin));
        when(cardsDao.getCardByNumber(any())).thenReturn(new Card(2L, cardNum + "1", 10L, TestUtil.getHash(currentPin)));
        when(cardsDao.getCardByNumber(cardNum)).thenReturn(testedCard);

        when(accountService.checkBalance(any())).thenReturn(new BigDecimal(0));
        when(accountService.checkBalance(testedCard.getId())).thenReturn(amount);

        BigDecimal resultAmount1 = cashMachineService.checkBalance(cashMachine, cardNum, currentPin);
        Assertions.assertEquals(resultAmount1, amount);

        BigDecimal resultAmount2 = cashMachineService.checkBalance(cashMachine, cardNum + "1", currentPin);
        Assertions.assertNotEquals(resultAmount2, amount);
    }

    @Test
    void changePin() {
// @TODO create change pin test using spy as implementation and ArgumentCaptor and thenReturn

//        when(cardsDao.getCardByNumber("1111"))
//                .thenReturn(new Card(1, "1111", 1L, "0000"));
//
//        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
//        when(cardsDao.saveCard(captor.capture())).thenReturn(new Card(1, "1", 1L, "1"));
//
//        cashMachineService.changePin("1111", "0000", "0001");
//        Assertions.assertEquals(TestUtil.getHash("0001"), captor.getValue().getPinCode());

        testedCard = new Card(1L, cardNum, 10L, TestUtil.getHash(currentPin));
        when(cardsDao.getCardByNumber(cardNum)).thenReturn(testedCard);

        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
        when(cardsDao.saveCard(captor.capture())).thenReturn(null);

        cashMachineService.changePin(cardNum, currentPin, newPin);
        Assertions.assertEquals(captor.getValue().getPinCode(), TestUtil.getHash(newPin));
    }

    @Test
    void changePinWithAnswer() {
// @TODO create change pin test using spy as implementation and mock an thenAnswer
        Card testedCard = new Card(1L, cardNum, 10L, TestUtil.getHash(currentPin));

        when(cardsDao.getCardByNumber(cardNum)).thenReturn(testedCard);

        final Card[] savedCards = new Card[1];
        when(cardsDao.saveCard(any(Card.class))).thenAnswer((Answer<Card>) invocation -> savedCards[0] = invocation.getArgument(0));

        cashMachineService.changePin(cardNum, currentPin, newPin);
        Assertions.assertEquals(savedCards[0].getPinCode(), TestUtil.getHash(newPin));
    }
}