package otus.study.cashmachine.machine.service;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.data.Card;
import otus.study.cashmachine.bank.service.AccountService;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;
import otus.study.cashmachine.machine.data.CashMachine;
import otus.study.cashmachine.machine.data.MoneyBox;
import otus.study.cashmachine.machine.service.impl.CashMachineServiceImpl;


import java.math.BigDecimal;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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

    @Captor
    private ArgumentCaptor<Card> cardCaptor = ArgumentCaptor.forClass(Card.class);

    private CashMachineServiceImpl cashMachineService;

    private CashMachine cashMachine = new CashMachine(new MoneyBox());

    private List<Integer> banknoteListExpected;
    private Card card;

    private final String CARD_NUMBER = "2343";
    private final String PIN = "1111";
    private final long CARD_ID = 1L;
    private final String CARD_PIN = "011c945f30ce2cbafc452f39840f025693339c42";
    private final long ACCOUNT_ID = 1L;
    private final BigDecimal SUM = new BigDecimal("10000");
    private final BigDecimal AMOUNT = new BigDecimal("10000");
    private final String OLD_PIN = "1111";
    private final String NEW_PIN = "2222";
    private final String EXPECTED_NEW_PIN = "fea7f657f56a2a448da7d4b535ee5e279caf3d9a";


    @BeforeEach
    void init() {
        cashMachineService = new CashMachineServiceImpl(cardService, accountService, moneyBoxService);
        banknoteListExpected = List.of(0, 0, 0, 0);
        card = new Card(CARD_ID,CARD_NUMBER, ACCOUNT_ID, CARD_PIN);
    }


    @Test
    void getMoney() {
        when(moneyBoxService.getMoney(cashMachine.getMoneyBox(), AMOUNT.intValue()))
                .thenReturn(banknoteListExpected);
        when(cardsDao.getCardByNumber(CARD_NUMBER))
                .thenReturn(card);
        when(accountService.getMoney(any(), any()))
                .thenReturn(SUM);

        cashMachineService.getMoney(cashMachine, CARD_NUMBER, PIN, AMOUNT);
        var sum = cardService.getMoney(CARD_NUMBER, PIN, AMOUNT);
        Assertions.assertThat(sum).isEqualTo(SUM);
    }

    @Test
    void putMoney() {
    }

    @Test
    void checkBalance() {

    }

    @Test
    void changePin() {
        when(cardsDao.getCardByNumber(CARD_NUMBER))
                .thenReturn(card);

        cashMachineService.changePin(CARD_NUMBER, OLD_PIN, NEW_PIN);
        verify(cardsDao, Mockito.times(1)).saveCard(cardCaptor.capture());
        Assertions.assertThat(cardCaptor.getValue().getPinCode()).isEqualTo(EXPECTED_NEW_PIN);
    }

    @Test
    void changePinWithAnswer() {
        when(cardsDao.getCardByNumber(CARD_NUMBER))
                .thenAnswer(i -> card);

        cashMachineService.changePin(CARD_NUMBER, OLD_PIN, NEW_PIN);
        verify(cardsDao, Mockito.times(1)).saveCard(cardCaptor.capture());
        Assertions.assertThat(cardCaptor.getValue().getPinCode()).isEqualTo(EXPECTED_NEW_PIN);
    }
}