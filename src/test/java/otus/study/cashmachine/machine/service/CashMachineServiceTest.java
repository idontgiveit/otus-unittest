package otus.study.cashmachine.machine.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CashMachineServiceTest {

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
    @DisplayName("Testing  getMoney using spy as mock")
    public void getMoney() {
        // @TODO create get money test using spy as mock

        //given
        doReturn(BigDecimal.ONE)
                .when(cardService)
                .getMoney("1111", "1234", BigDecimal.ONE);

        when(moneyBoxService.getMoney(any(), anyInt()))
                .thenReturn(List.of(1, 1, 1, 1));

        CashMachine machine = new CashMachine(new MoneyBox());

        //when
        List<Integer> result = cashMachineService.getMoney(machine, "1111", "1234", BigDecimal.ONE);

        //then
        assertEquals(List.of(1,1,1,1), result);
    }

    @Test
    void putMoney() {
    }

    @Test
    void checkBalance() {

    }

    @Test
    @DisplayName("Testing changePin using spy as implementation and ArgumentCaptor and thenReturn")
    public void changePin() {
        // @TODO create change pin test using spy as implementation and ArgumentCaptor and thenReturn

        // given
        when(cardsDao.getCardByNumber("1111"))
                .thenReturn(new Card(1L, "1111", 1L, TestUtil.getHash("1234")));

        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
        when(cardsDao.saveCard(captor.capture()))
                .thenReturn(new Card(0, "0000", 1L, "0000"));

        //when
        cardService.cnangePin("1111", "1234", "4321");

        //then
        assertEquals(TestUtil.getHash("4321"), captor.getValue().getPinCode());
    }

    @Test
    @DisplayName("Testing changePin using spy as implementation and mock an thenAnswer")
    public void changePinWithAnswer() {
        // @TODO create change pin test using spy as implementation and mock an thenAnswer

        // given
        when(cardsDao.getCardByNumber("1111"))
                .thenReturn(new Card(1L, "1111", 1L, TestUtil.getHash("1234")));

        List<Card> cards = new ArrayList<>();
        when(cardsDao.saveCard(any()))
                .thenAnswer(new Answer<Card>() {
                    @Override
                    public Card answer(InvocationOnMock invocation) throws Throwable {
                        cards.add(invocation.getArgument(0));
                        return cards.get(0);
                    }
                });

        //when
        cardService.cnangePin("1111", "1234", "4321");

        //then
        assertEquals(cards.get(0).getPinCode(), TestUtil.getHash("4321"));
    }
}