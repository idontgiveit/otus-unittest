package otus.study.cashmachine.machine.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.mockito.verification.VerificationMode;
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

    private final BigDecimal TEST_START_AMOUNT_VALUE = BigDecimal.valueOf(2000);

    private final String TEST_CARD_NUMBER = "1111 2222 3333 4444";
    private final String TEST_PIN = "0000";
    private final Card TEST_CARD = new Card(0, TEST_CARD_NUMBER, 0L, getHash(TEST_PIN));
    private final String NEW_TEST_PIN = "1111";

    @BeforeEach
    void init() {
        cashMachineService = new CashMachineServiceImpl(cardService, accountService, moneyBoxService);
    }


    @Test
    @DisplayName("Testing getMoney() method using spy as mock")
    void getMoney() {
        // @TODO create get money test using spy as mock
        BigDecimal amountToIssue = BigDecimal.valueOf(1600);
        var banknotes = List.of(0,1,1,1);
        doReturn(amountToIssue).when(cardService).getMoney(TEST_CARD_NUMBER, TEST_PIN, amountToIssue);
                when(moneyBoxService.getMoney(any(), anyInt())).thenReturn(banknotes);
        var result= Assertions.assertDoesNotThrow(
                () -> cashMachineService.getMoney(cashMachine, TEST_CARD_NUMBER, TEST_PIN, amountToIssue));
        assertEquals(result, banknotes);
    }

    @Test
    @DisplayName("Testing putMoney() method")
    void putMoney() {
        var banknotes = List.of(0,1,1,1);
        BigDecimal amountToPut = BigDecimal.valueOf(1000 + 500 + 100);
        doReturn(TEST_START_AMOUNT_VALUE).when(cardService).getBalance(TEST_CARD_NUMBER, TEST_PIN);
        doReturn(amountToPut).when(cardService).putMoney(TEST_CARD_NUMBER, TEST_PIN, amountToPut);
        var result= cashMachineService.putMoney(cashMachine, TEST_CARD_NUMBER, TEST_PIN, banknotes);
        assertEquals(result, amountToPut);
    }

    @Test
    @DisplayName("Testing checkBalance()")
    void checkBalance() {
        doReturn(TEST_START_AMOUNT_VALUE).when(cardService).getBalance(TEST_CARD_NUMBER, TEST_PIN);
        var result= cashMachineService.checkBalance(cashMachine, TEST_CARD_NUMBER, TEST_PIN);
        assertEquals(result, TEST_START_AMOUNT_VALUE);
    }

    @Test
    @DisplayName("Testing changePin() method using spy as implementation and ArgumentCaptor and thenReturn")
    void changePin() {
        // @TODO create change pin test using spy as implementation and ArgumentCaptor and thenReturn
        ArgumentCaptor<Card> argumentCaptor = ArgumentCaptor.forClass(Card.class);
        when(cardsDao.getCardByNumber(TEST_CARD_NUMBER)).thenReturn(TEST_CARD);

        cashMachineService.changePin(TEST_CARD_NUMBER, TEST_PIN, NEW_TEST_PIN);

        verify(cardsDao).saveCard(argumentCaptor.capture());

        assertEquals(TEST_CARD_NUMBER, argumentCaptor.getValue().getNumber());
        assertEquals(getHash(NEW_TEST_PIN), argumentCaptor.getValue().getPinCode());
    }

    @Test
    @DisplayName("Testing changePin() method using spy as implementation and mock an thenAnswer")
    void changePinWithAnswer() {
    // @TODO create change pin test using spy as implementation and mock an thenAnswer

        ArgumentCaptor<Card> argumentCaptor = ArgumentCaptor.forClass(Card.class);
        when(cardsDao.getCardByNumber(TEST_CARD_NUMBER)).thenReturn(TEST_CARD);
        when(cardsDao.saveCard(TEST_CARD)).thenAnswer((Answer<Card>) invocation -> {
            Card card = invocation.getArgument(0);
            Assertions.assertEquals(TEST_CARD_NUMBER, card.getNumber());
            Assertions.assertEquals(getHash(NEW_TEST_PIN), card.getPinCode());
            return card;
        });
        cashMachineService.changePin(TEST_CARD_NUMBER, TEST_PIN, NEW_TEST_PIN);

        verify(cardsDao).saveCard(argumentCaptor.capture());

        assertEquals(TEST_CARD_NUMBER, argumentCaptor.getValue().getNumber());
        assertEquals(getHash(NEW_TEST_PIN), argumentCaptor.getValue().getPinCode());
    }
}