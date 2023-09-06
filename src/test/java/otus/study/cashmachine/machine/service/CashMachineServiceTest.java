package otus.study.cashmachine.machine.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.data.Card;
import otus.study.cashmachine.bank.service.AccountService;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;
import otus.study.cashmachine.machine.data.CashMachine;
import otus.study.cashmachine.machine.data.MoneyBox;
import otus.study.cashmachine.machine.service.impl.CashMachineServiceImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static otus.study.cashmachine.TestUtil.getHash;

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
// @TODO create get money test using spy as mock
        BigDecimal amount = BigDecimal.valueOf(100);
        String cardNum = "1234";
        String pin = "0000";

        doReturn(amount).when(cardService).getMoney(cardNum, pin, amount);
        when(moneyBoxService.getMoney(any(), anyInt())).thenReturn(List.of(1, 0, 0, 0));

        List<Integer> actual = cashMachineService.getMoney(cashMachine, "1234", "0000", amount);

        verify(cardService).getMoney(eq(cardNum), eq(pin), eq(amount));

        List<Integer> expected = List.of(1, 0, 0, 0);
        assertEquals(expected, actual);

    }

    @Test
    void putMoney() {
        String pin = "0000";
        Card card = new Card(1L, "1234", 1L, getHash(pin));
        when(cardsDao.getCardByNumber(eq(card.getNumber()))).thenReturn(card);

        List<Integer> notes = List.of(1, 2, 0, 0);
        BigDecimal expectedAmount = BigDecimal.valueOf(7000L);
        when(accountService.putMoney(eq(card.getAccountId()), any())).thenReturn(expectedAmount);

        cashMachineService.putMoney(cashMachine, card.getNumber(), pin, notes);

        verify(moneyBoxService).putMoney(any(),
                eq(notes.get(3)),
                eq(notes.get(2)),
                eq(notes.get(1)),
                eq(notes.get(0)));

        ArgumentCaptor<BigDecimal> captor = ArgumentCaptor.forClass(BigDecimal.class);
        verify(cardService).putMoney(anyString(), anyString(), captor.capture());
        assertEquals(expectedAmount, captor.getValue());
    }

    @Test
    void checkBalance() {
        String pin = "0000";
        Card card = new Card(1L, "1234", 1L, getHash(pin));
        when(cardsDao.getCardByNumber(any())).thenReturn(card);

        BigDecimal expectedAmount = BigDecimal.TEN;
        when(accountService.checkBalance(eq(card.getAccountId()))).thenReturn(expectedAmount);

        BigDecimal actualAmount = cashMachineService.checkBalance(cashMachine, card.getNumber(), pin);

        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void changePin() {
// @TODO create change pin test using spy as implementation and ArgumentCaptor and thenReturn
        String cardNum = "1234";
        String pin = "0000";
        String newPin = "0001";
        String oldPinHash = getHash(pin);

        ArgumentCaptor<Card> cardCaptor = ArgumentCaptor.forClass(Card.class);
        when(cardsDao.getCardByNumber(cardNum))
                .thenReturn(new Card(0L, cardNum, 0L, oldPinHash));

        cashMachineService.changePin(cardNum, pin, newPin);

        verify(cardService).cnangePin(anyString(), anyString(), anyString());
        verify(cardsDao).saveCard(cardCaptor.capture());

        assertEquals(cardNum, cardCaptor.getValue().getNumber());
        assertEquals(getHash(newPin), cardCaptor.getValue().getPinCode());
    }

    @Test
    void changePinWithAnswer() {
// @TODO create change pin test using spy as implementation and mock an thenAnswer
        String cardNum = "1234";
        String pin = "0000";
        String newPin = "0001";
        String oldPinHash = getHash(pin);
        String newPinHash = getHash(newPin);

        when(cardsDao.getCardByNumber(cardNum))
                .thenReturn(new Card(0L, cardNum, 0L, oldPinHash));

        when(cardsDao.saveCard(any())).thenAnswer((Answer<Card>) invocation -> {
            Card argument = invocation.getArgument(0);
            assertEquals(cardNum, argument.getNumber());
            assertEquals(newPinHash, argument.getPinCode());
            return argument;
        });

        boolean actualResult = cashMachineService.changePin(cardNum, pin, newPin);

        verify(cardService).cnangePin(anyString(), anyString(), anyString());
        verify(cardsDao).saveCard(any());

        assertTrue(actualResult);
    }
}