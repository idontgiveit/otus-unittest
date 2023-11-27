package otus.study.cashmachine.machine.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import otus.study.cashmachine.TestUtil;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.data.Card;
import otus.study.cashmachine.bank.service.AccountService;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;
import otus.study.cashmachine.machine.data.CashMachine;
import otus.study.cashmachine.machine.data.MoneyBox;
import otus.study.cashmachine.machine.service.impl.CashMachineServiceImpl;
import java.util.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static otus.study.cashmachine.TestUtil.getHash;

//@ExtendWith(MockitoExtension.class)
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

    private Card card;

    private final static String CARD_NUMBER = "0000";
    private final static String PIN_CODE = "1234";
    private final static String NEW_PIN_CODE = "5678";

    @BeforeEach
    void init() {
        cashMachineService = new CashMachineServiceImpl(cardService, accountService, moneyBoxService);
        card = new Card(1L, CARD_NUMBER, 1L, getHash(PIN_CODE));
        cardsDao = mock(CardsDao.class);
    }


    @Test
    void getMoney() {
// @TODO create get money test using spy as mock
        when(cardsDao.getCardByNumber("1111"))
                .thenReturn(new Card(1L, "1111", 1L, getHash("0000")));

        when(accountService.getMoney(1L, new BigDecimal(1000)))
                .thenReturn(new BigDecimal(0));

        when(moneyBoxService.getMoney(any(MoneyBox.class), anyInt())).thenReturn(Arrays.asList(10,10,10,10));


        assertEquals(Arrays.asList(10,10,10,10), cashMachineService.getMoney(cashMachine, "1111", "0000", new BigDecimal(1000)));
    }

    @Test
    void putMoney() {
        List<Integer> notes = new ArrayList<Integer>();
        notes.add(0);
        notes.add(1000);
        notes.add(0);
        notes.add(0);
        cashMachineService.putMoney(cashMachine, "1111", "0000", notes);
        assertEquals("1000", cashMachineService.getMoney(cashMachine, "1111", "0000", new BigDecimal(1000)));
    }

    @Test
    void checkBalance() {
        verify(cashMachineService).checkBalance(cashMachine, "1111", "0000");
    }

    @Test
    void changePin() {
// @TODO create change pin test using spy as implementation and ArgumentCaptor and thenReturn
        ArgumentCaptor<Card> cardCaptor = ArgumentCaptor.forClass(Card.class);
        cardService = mock(CardServiceImpl.class);
        when(cardsDao.getCardByNumber(CARD_NUMBER)).thenReturn(card);
        when(cardsDao.saveCard(any(Card.class))).thenReturn(null);

        boolean isPinChanged = cashMachineService.changePin(CARD_NUMBER, PIN_CODE, NEW_PIN_CODE);
        Assertions.assertTrue(isPinChanged);

        verify(cardsDao).saveCard(cardCaptor.capture());
        String actualPinAfterChange = cardCaptor.getValue().getPinCode();
        assertEquals(actualPinAfterChange,getHash(NEW_PIN_CODE));
    }

    @Test
    void changePinWithAnswer() {
// @TODO create change pin test using spy as implementation and mock an thenAnswer
        when(cardsDao.getCardByNumber(CARD_NUMBER)).thenReturn(card);

        final String[] actualPinAfterChange = {null};
        when(cardsDao.saveCard(any(Card.class))).thenAnswer((Answer<String>) invocation -> {
            Card savedCard = invocation.getArgument(0);
            actualPinAfterChange[0] = savedCard.getPinCode();
            return null;
        });

        cashMachineService.changePin(CARD_NUMBER, PIN_CODE, NEW_PIN_CODE);

        assertEquals(actualPinAfterChange[0],getHash(NEW_PIN_CODE));
    }
}