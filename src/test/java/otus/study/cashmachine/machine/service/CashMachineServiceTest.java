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

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CashMachineServiceTest {

    public static final String CARD_NUMBER = "2222";
    public static final String PIN = "1234";
    public static final String NEW_PIN = "1111";
    public static final int CORRECT_NUMBER = 150;
    @Captor
    private ArgumentCaptor<String> captor;

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
        when(cardsDao.getCardByNumber(CARD_NUMBER))
                .thenThrow(IllegalArgumentException.class);

        assertThrows(RuntimeException.class, () ->
                cashMachineService.getMoney(cashMachine, CARD_NUMBER, PIN, BigDecimal.valueOf(CORRECT_NUMBER)));

    }


    @Test
    void getMoney2() {
        when(cardsDao.getCardByNumber(CARD_NUMBER))
                .thenReturn(new Card(1, CARD_NUMBER, 2L, TestUtil.getHash(PIN)));

        List<Integer> money = cashMachineService.getMoney(cashMachine, CARD_NUMBER, PIN, BigDecimal.valueOf(CORRECT_NUMBER));

        assertTrue(money.isEmpty());
    }

    @Test
    void putMoney() {
        when(cardsDao.getCardByNumber(CARD_NUMBER))
                .thenReturn(new Card(1, CARD_NUMBER, 2L, TestUtil.getHash(PIN)));

        BigDecimal bigDecimal = cashMachineService.putMoney(cashMachine, CARD_NUMBER, PIN, List.of(10, 10, 10, 10));

        assertNull(bigDecimal);
    }

    @Test
    void checkBalance() {
        Card card = new Card(1L, "2222", 1L, TestUtil.getHash("1234"));
        when(cardsDao.getCardByNumber(anyString())).thenReturn(card);
        BigDecimal bigDecimal = cashMachineService.checkBalance(cashMachine, "2222", "1234");
        assertNull(bigDecimal);
    }

    @Test
    void changePin() {
        when(cardsDao.getCardByNumber(captor.capture()))
                .thenReturn(new Card(1, PIN, 1L, TestUtil.getHash(CARD_NUMBER)));

        assertTrue(cashMachineService.changePin(PIN, CARD_NUMBER, NEW_PIN));
    }

    @Test
    void changePinWithAnswer() {
        when(cardsDao.getCardByNumber(captor.capture()))
                .thenAnswer(i -> new Card(1, String.valueOf(i.getArguments()[0]), 1L, TestUtil.getHash(CARD_NUMBER)));

        assertTrue(cashMachineService.changePin(PIN, CARD_NUMBER, NEW_PIN));
    }
}