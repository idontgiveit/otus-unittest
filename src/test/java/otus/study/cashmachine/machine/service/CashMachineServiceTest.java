package otus.study.cashmachine.machine.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    private CashMachine cashMachine = new CashMachine(new MoneyBox());

    @BeforeEach
    void init() {
        cashMachineService = new CashMachineServiceImpl(cardService, accountService, moneyBoxService);
    }


    @Test
    void getMoney() {
        String number = "1111";
        String pin = "1234";
        BigDecimal amount = new BigDecimal(100);

        Card card = new Card(1, number, 1L, getHash(pin));

        when(cardsDao.getCardByNumber(any())).thenReturn(card);
        when(cardService.getMoney(number, pin, amount)).thenReturn(BigDecimal.valueOf(100));
        when(moneyBoxService.getMoney(any(), anyInt()))
                .thenReturn(Arrays.asList(10, 10, 10, 10, 10));

        List<Integer> result = cashMachineService.getMoney(cashMachine, number, pin, amount);

        verify(cardService).getMoney(number, pin, amount);
        verify(moneyBoxService).getMoney(cashMachine.getMoneyBox(), amount.intValue());

        assertEquals(5, result.size());
    }

    @Test
    void putMoney() {
        String number = "1111";
        String pin = "1234";
        List<Integer> notes = List.of(1, 2, 3, 4);
        BigDecimal amount = new BigDecimal(6600);
        CashMachine machine = new CashMachine(new MoneyBox(1, 1, 1, 1));
        Card card = new Card(1, number, 11L, getHash(pin));

        when(cardsDao.getCardByNumber(number)).thenReturn(card);
        when(cardService.getBalance(number, pin)).thenReturn(amount);
        doNothing().when(moneyBoxService).putMoney(any(), anyInt(), anyInt(), anyInt(), anyInt());

        when(accountService.putMoney(any(), any())).thenReturn(amount);

        BigDecimal addMoney = cashMachineService.putMoney(machine, number, pin, notes);

        assertEquals(addMoney, amount);
        verify(cardService).getBalance(number, pin);

    }

    @Test
    void checkBalance() {
        String number = "1111";
        String pin = "0000";
        MoneyBox moneyBox = new MoneyBox(1, 1, 1, 1);
        CashMachine machine = new CashMachine(moneyBox);
        Card card = new Card(1, number, 11L, getHash(pin));

        when(cardsDao.getCardByNumber(number)).thenReturn(card);
        when(cardService.getBalance(number, pin)).thenReturn(new BigDecimal(6600));

        BigDecimal balance = cashMachineService.checkBalance(machine, number, pin);
        BigDecimal expectedBalance = new BigDecimal(6600);

        assertEquals(balance, expectedBalance);
    }

    @Test
    void changePin() {
        String number = "1111";
        String pin = "1234";
        String newPin = "4321";
        Long accountId = 1L;

        Card card = new Card(1L, number, accountId, getHash(pin));
        ArgumentCaptor<Card> cardArgumentCaptor = ArgumentCaptor.forClass(Card.class);

        when(cardsDao.getCardByNumber(number)).thenReturn(card);

        boolean changedPin = cashMachineService.changePin(number, pin, newPin);
        verify(cardsDao).saveCard(cardArgumentCaptor.capture());

        assertTrue(changedPin);
    }

    @Test
    void changePinWithAnswer() {
        String number = "1111";
        String pin = "1234";
        String newPin = "4321";
        Long accountId = 1L;

        Card card = new Card(1L, number, accountId, getHash(pin));

        when(cardsDao.getCardByNumber(number)).thenReturn(card);
        when(cardService.cnangePin(number, getHash(pin), getHash(newPin)))
                .thenAnswer(new Answer<Boolean>() {
                    @Override
                    public Boolean answer(InvocationOnMock invocation) throws Throwable {
                        Object[] arguments = invocation.getArguments();
                        String cardNumber = (String) arguments[0];
                        String oldPin = (String) arguments[1];
                        String newPin = (String) arguments[2];

                        return (!getHash(oldPin).equals(card.getPinCode()));
                    }
                });

        assertTrue(cashMachineService.changePin(number, getHash(pin), getHash(newPin)));
        verify(cardService).cnangePin(number, getHash(pin), getHash(newPin));
    }
}