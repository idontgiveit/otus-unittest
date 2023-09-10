package otus.study.cashmachine.machine.service;

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

import java.util.Arrays;
import java.util.List;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

    final private CashMachine cashMachine = new CashMachine(new MoneyBox());

    final String TEST_CARD_NUMBER = "1234_5678_9009_8765";
    final long TEST_ACC_ID = 88888888L;
    final String TEST_PIN = "0000";
    final BigDecimal TEST_AMOUNT = new BigDecimal(6600);

    @BeforeEach
    void init() {
        cashMachineService = new CashMachineServiceImpl(cardService, accountService, moneyBoxService);
        when(cardsDao.getCardByNumber(any())).thenReturn(new Card(0, TEST_CARD_NUMBER, TEST_ACC_ID, TestUtil.getHash(TEST_PIN)));
    }


    @Test
    void getMoney() {
        when(cardService.getMoney(TEST_CARD_NUMBER, TEST_PIN, TEST_AMOUNT)).thenReturn(TEST_AMOUNT);
        when(moneyBoxService.getMoney(any(),anyInt())).thenReturn(Arrays.asList(1,1,1,1));
        List<Integer> banknotes = cashMachineService.getMoney(cashMachine, TEST_CARD_NUMBER, TEST_PIN, TEST_AMOUNT);
        assertEquals(1, banknotes.get(0));
        assertEquals(1, banknotes.get(1));
        assertEquals(1, banknotes.get(2));
        assertEquals(1, banknotes.get(3));

    }

    @Test
    void putMoney() {
        when(cardService.getBalance(TEST_CARD_NUMBER, TEST_PIN)).thenReturn(TEST_AMOUNT);
        when(cardService.putMoney(TEST_CARD_NUMBER, TEST_PIN, TEST_AMOUNT)).thenReturn(TEST_AMOUNT);

        BigDecimal balance = cashMachineService.putMoney(cashMachine, TEST_CARD_NUMBER, TEST_PIN, Arrays.asList(1, 1, 1, 1));
        assertEquals(TEST_AMOUNT, balance);
    }

    @Test
    void checkBalance() {
        when(cardService.getBalance(TEST_CARD_NUMBER, TEST_PIN)).thenReturn(TEST_AMOUNT);
        assertEquals(TEST_AMOUNT, cashMachineService.checkBalance(cashMachine, TEST_CARD_NUMBER, TEST_PIN));
    }

    @Test
    void changePin() {
        ArgumentCaptor<String> cardNumberCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> oldPinCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> newPinCaptor = ArgumentCaptor.forClass(String.class);

        when(cardService.cnangePin(cardNumberCaptor.capture(), oldPinCaptor.capture(), newPinCaptor.capture())).thenReturn(true);
        assertTrue(cashMachineService.changePin(TEST_CARD_NUMBER, TEST_PIN, "2023"));

    }

    @Test
    void changePinWithAnswer() {
        when(cardService.cnangePin(TEST_CARD_NUMBER, TEST_PIN, "2023")).thenAnswer(input-> true);
        when(cardService.cnangePin(TEST_CARD_NUMBER, TEST_PIN, "2024")).thenAnswer(input->false);

        assertTrue(cashMachineService.changePin(TEST_CARD_NUMBER, TEST_PIN, "2023"));
        assertFalse(cashMachineService.changePin(TEST_CARD_NUMBER, TEST_PIN, "2024"));
    }
}