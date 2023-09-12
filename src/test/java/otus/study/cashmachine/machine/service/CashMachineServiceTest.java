package otus.study.cashmachine.machine.service;

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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private String pin;
    private String newPin;
    private String cardNumber;

    private Card testedCard;

    @BeforeEach
    void init() {
        pin = "1111";
        newPin = "5555";
        cardNumber = "1234";
        testedCard = new Card(1L, cardNumber, 25L, TestUtil.getHash(pin));
        cashMachineService = new CashMachineServiceImpl(cardService, accountService, moneyBoxService);
    }


    @Test
    void getMoney() {
        BigDecimal returnedDecimal = BigDecimal.TEN;
        doReturn(returnedDecimal).when(cardService).getMoney(cardNumber, pin, BigDecimal.TEN);

        assertDoesNotThrow(() -> cashMachineService.getMoney(cashMachine, cardNumber, pin, BigDecimal.TEN));

        when(cardService.getMoney(cardNumber, pin, BigDecimal.TEN)).thenThrow(RuntimeException.class);

        doReturn(returnedDecimal).when(cardService).putMoney(cardNumber, pin, BigDecimal.TEN);
        assertThrows(RuntimeException.class,
                () -> cashMachineService.getMoney(cashMachine, cardNumber, pin, BigDecimal.TEN));
    }

    @Test
    void putMoney() {
        BigDecimal expected = BigDecimal.valueOf(6600L);
        when(cardsDao.getCardByNumber(cardNumber)).thenReturn(testedCard);
        when(cardService.putMoney(cardNumber, pin, expected)).thenReturn(expected);
        BigDecimal realDecimal = cashMachineService.putMoney(cashMachine, cardNumber, pin, List.of(1, 1, 1, 1));
        assertEquals(expected, realDecimal);
    }

    @Test
    void checkBalance() {
        BigDecimal expected = BigDecimal.valueOf(6600L);
        when(cardsDao.getCardByNumber(cardNumber)).thenReturn(testedCard);
        when(accountService.checkBalance(1L)).thenReturn(expected);
        assertEquals(expected, cashMachineService.checkBalance(cashMachine, cardNumber, pin));
    }

    @Test
    void changePin() {

        ArgumentCaptor<Card> cardArgumentCaptor = ArgumentCaptor.forClass(Card.class);
        when(cardsDao.getCardByNumber(cardNumber)).thenReturn(testedCard);
        when(cardsDao.saveCard(cardArgumentCaptor.capture())).thenReturn(testedCard);
        cashMachineService.changePin(cardNumber, pin, newPin);
        assertEquals(TestUtil.getHash(newPin), cardArgumentCaptor.getValue().getPinCode());
    }

    @Test
    void changePinWithAnswer() {
        when(cardsDao.getCardByNumber(cardNumber)).thenReturn(testedCard);

        final Card[] savedCards = new Card[1];
        when(cardsDao.saveCard(any())).thenAnswer((Answer<Card>) invocation ->
                savedCards[0] = invocation.getArgument(0));

        cashMachineService.changePin(cardNumber, pin, newPin);

        assertEquals(TestUtil.getHash(newPin), savedCards[0].getPinCode());
    }
}