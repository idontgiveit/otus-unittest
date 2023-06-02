package otus.study.cashmachine.machine.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import java.util.ArrayList;
import java.util.List;

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

    @BeforeEach
    void init() {
        cashMachineService = new CashMachineServiceImpl(cardService, accountService, moneyBoxService);
    }


    @Test
    @DisplayName("Create get money test using spy as mock")
    void getMoney() {

        doReturn(BigDecimal.TEN).when(cardService).getMoney("1234", "0000", BigDecimal.TEN);

        when(moneyBoxService.getMoney(Mockito.any(), Mockito.anyInt())).thenReturn(List.of(0, 0, 0, 0));

        CashMachine machine = new CashMachine(new MoneyBox());

        List<Integer> result = cashMachineService.getMoney(machine, "1234", "0000", BigDecimal.TEN);

        Assertions.assertEquals(List.of(0, 0, 0, 0), result);
    }

    @Test
    void putMoney() {
    }

    @Test
    void checkBalance() {

    }

    @Test
    @DisplayName("Create change pin test using spy as implementation and ArgumentCaptor and thenReturn")
    void changePin() {
        when(cardsDao.getCardByNumber("1234")).thenReturn(new Card(1L, "1234", 1L, TestUtil.getHash("1234")));

        ArgumentCaptor<Card> argumentCaptor = ArgumentCaptor.forClass(Card.class);

        when(cardsDao.saveCard(argumentCaptor.capture())).thenReturn(new Card(1L, "1234", 1L, "0000"));

        cardService.cnangePin("1234", "1234", "0000");

        Assertions.assertEquals(TestUtil.getHash("0000"), argumentCaptor.getValue().getPinCode());
    }

    @Test
    @DisplayName("Create change pin test using spy as implementation and mock an thenAnswer")
    void changePinWithAnswer() {
        when(cardsDao.getCardByNumber("1234")).thenReturn(new Card(1L, "1234", 1L, TestUtil.getHash("1234")));

        List<Card> cards = new ArrayList<>();
        when(cardsDao.saveCard(any())).then((Answer<Card>) invocation -> {
            cards.add(invocation.getArgument(0));
            return invocation.getArgument(0);
        });
        cardService.cnangePin("1234", "1234", "0000");

        Assertions.assertEquals(TestUtil.getHash("0000"), cards.get(0).getPinCode());
    }
}