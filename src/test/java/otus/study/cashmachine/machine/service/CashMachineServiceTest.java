package otus.study.cashmachine.machine.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.TestUtil;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.data.Card;
import otus.study.cashmachine.bank.service.AccountService;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;
import otus.study.cashmachine.machine.data.CashMachine;
import otus.study.cashmachine.machine.data.MoneyBox;
import otus.study.cashmachine.machine.service.impl.CashMachineServiceImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

        ArgumentCaptor<String> cardNumCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> pinCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<BigDecimal> amountCaptor = ArgumentCaptor.forClass(BigDecimal.class);

        Mockito.when(cardsDao.getCardByNumber("1111222233334444")).thenReturn(new Card(1l, "1111222233334444", 1000L, TestUtil.getHash("1234")));

        cashMachineService.getMoney(cashMachine, "1111222233334444", "1234", new BigDecimal("100"));
        Mockito.verify(cardService).getMoney(cardNumCaptor.capture(), pinCaptor.capture(), amountCaptor.capture());

        Assertions.assertEquals("1111222233334444", cardNumCaptor.getValue());
        Assertions.assertEquals("1234", pinCaptor.getValue());
        Assertions.assertEquals(new BigDecimal("100"), amountCaptor.getValue());
    }

    @Test
    void putMoney() {

        ArgumentCaptor<String> cardNumCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> pinCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<BigDecimal> amountCaptor = ArgumentCaptor.forClass(BigDecimal.class);

        Mockito.when(cardsDao.getCardByNumber("1111222233334444")).thenReturn(new Card(1l, "1111222233334444", 1000L, TestUtil.getHash("1234")));

        cashMachineService.putMoney(cashMachine, "1111222233334444", "1234", List.of(1, 4, 1, 5));
        Mockito.verify(cardService).putMoney(cardNumCaptor.capture(), pinCaptor.capture(), amountCaptor.capture());

        Assertions.assertEquals("1111222233334444", cardNumCaptor.getValue());
        Assertions.assertEquals("1234", pinCaptor.getValue());
        Assertions.assertEquals(new BigDecimal("10000"), amountCaptor.getValue());
    }

    @Test
    void checkBalance() {

        ArgumentCaptor<String> cardNumCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> pinCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.when(cardsDao.getCardByNumber("1111222233334444")).thenReturn(new Card(1l, "1111222233334444", 1000L, TestUtil.getHash("1234")));
        Mockito.when(accountService.checkBalance(1l)).thenReturn(BigDecimal.ONE);

        var amount = cashMachineService.checkBalance(cashMachine, "1111222233334444", "1234");
        Mockito.verify(cardService).getBalance(cardNumCaptor.capture(), pinCaptor.capture());

        Assertions.assertEquals("1111222233334444", cardNumCaptor.getValue());
        Assertions.assertEquals("1234", pinCaptor.getValue());
        Assertions.assertEquals(BigDecimal.ONE, amount);
    }

    @Test
    void changePin() {

        ArgumentCaptor<String> cardNumCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> pinCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> newPinCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.when(cardsDao.getCardByNumber("1111222233334444")).thenReturn(new Card(1l, "1111222233334444", 1000L, TestUtil.getHash("0000")));
        Mockito.when(cardsDao.saveCard(Mockito.any())).thenReturn(new Card(1l, "1111222233334444", 1000L, TestUtil.getHash("1234")));

        var pinChanged = cashMachineService.changePin("1111222233334444", "0000", "1234");
        Mockito.verify(cardService).cnangePin(cardNumCaptor.capture(), pinCaptor.capture(), newPinCaptor.capture());

        Assertions.assertEquals("1111222233334444", cardNumCaptor.getValue());
        Assertions.assertEquals("0000", pinCaptor.getValue());
        Assertions.assertEquals("1234", newPinCaptor.getValue());
        Assertions.assertEquals(true, pinChanged);
    }

    @Test
    void changePinWithAnswer() {

        ArgumentCaptor<String> cardNumCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> pinCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> newPinCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.when(cardsDao.getCardByNumber("1111222233334444")).thenReturn(new Card(1l, "1111222233334444", 1000L, TestUtil.getHash("0000")));
        Mockito.when(cardsDao.saveCard(Mockito.any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        var pinChanged = cashMachineService.changePin("1111222233334444", "0000", "1234");
        Mockito.verify(cardService).cnangePin(cardNumCaptor.capture(), pinCaptor.capture(), newPinCaptor.capture());

        Assertions.assertEquals("1111222233334444", cardNumCaptor.getValue());
        Assertions.assertEquals("0000", pinCaptor.getValue());
        Assertions.assertEquals("1234", newPinCaptor.getValue());
        Assertions.assertEquals(true, pinChanged);
    }
}