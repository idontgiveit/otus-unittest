package otus.study.cashmachine.machine.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CashMachineServiceTest {
    private final static BigDecimal SUM = BigDecimal.valueOf(100);
    private final static BigDecimal REMINDER = BigDecimal.valueOf(100);
    private final static List<Integer> MONEY = List.of(100);
    private final static String CARD_NUMBER = "0000";
    private final static String PIN_CODE = "1234";
    private final static String NEW_PIN_CODE = "5678";
    private final static List<Integer> BANKNOTES = List.of(0, 0, 0, 1);


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

    private Card card;

    @BeforeEach
    void init() {
        cashMachineService = new CashMachineServiceImpl(cardService, accountService, moneyBoxService);
        card = new Card(1L, CARD_NUMBER, 1L, getHash(PIN_CODE));
    }


    @Test
    void getMoney() {
// @TODO create get money test using spy as mock
        doReturn(REMINDER).when(cardService).getMoney(CARD_NUMBER, PIN_CODE, SUM);
        when(moneyBoxService.getMoney(cashMachine.getMoneyBox(), SUM.intValue())).thenReturn(MONEY);

        List<Integer> actualMoney = cashMachineService.getMoney(cashMachine, CARD_NUMBER, PIN_CODE, SUM);

        assertThat(actualMoney).containsOnly(SUM.intValue());
    }

    @Test
    void getMoneyWithException() {
        when(cardsDao.getCardByNumber(CARD_NUMBER)).thenReturn(null);

        Exception thrown = assertThrows(
                RuntimeException.class, () -> cashMachineService.getMoney(cashMachine, CARD_NUMBER, PIN_CODE, SUM)
        );

        assertThat(thrown).isInstanceOf(RuntimeException.class);
        assertThat(thrown.getMessage()).isEqualTo("No card found");
    }

    @Test
    void putMoney() {
        doReturn(REMINDER).when(cardService).getBalance(CARD_NUMBER, PIN_CODE);

        ArgumentCaptor<Integer> note100 = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> note500 = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> note1000 = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> note5000 = ArgumentCaptor.forClass(Integer.class);

        doNothing().when(moneyBoxService).putMoney(
                any(MoneyBox.class), note100.capture(), note500.capture(), note1000.capture(), note5000.capture()
        );

        BigDecimal expectedBalance = SUM;
        doReturn(expectedBalance).when(cardService).putMoney(CARD_NUMBER, PIN_CODE, SUM);

        BigDecimal actualBalance = cashMachineService.putMoney(cashMachine, CARD_NUMBER, PIN_CODE, BANKNOTES);

        verify(moneyBoxService, times(1)).putMoney(
                cashMachine.getMoneyBox(), 1, 0, 0, 0
        );
        assertThat(actualBalance).isEqualTo(expectedBalance);
    }

    @Test
    void checkBalance() {
        doReturn(REMINDER).when(cardService).getBalance(CARD_NUMBER, PIN_CODE);
        BigDecimal actualBalance = cashMachineService.checkBalance(cashMachine, CARD_NUMBER, PIN_CODE);
        assertThat(actualBalance).isEqualTo(REMINDER);
    }

    @Test
    void changePin() {
// @TODO create change pin test using spy as implementation and ArgumentCaptor and thenReturn;
        ArgumentCaptor<Card> cardCaptor = ArgumentCaptor.forClass(Card.class);

        when(cardsDao.getCardByNumber(CARD_NUMBER)).thenReturn(card);
        when(cardsDao.saveCard(any(Card.class))).thenReturn(null);

        boolean isPinChanged = cashMachineService.changePin(CARD_NUMBER, PIN_CODE, NEW_PIN_CODE);
        assertThat(isPinChanged).isTrue();

        verify(cardsDao).saveCard(cardCaptor.capture());
        String actualPinAfterChange = cardCaptor.getValue().getPinCode();
        assertThat(actualPinAfterChange).isEqualTo(getHash(NEW_PIN_CODE));
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

        assertThat(actualPinAfterChange[0]).isEqualTo(getHash(NEW_PIN_CODE));
    }

    private String getHash(String pin) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            digest.update(pin.getBytes());
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}