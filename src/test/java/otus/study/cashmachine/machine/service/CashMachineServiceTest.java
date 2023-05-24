package otus.study.cashmachine.machine.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import otus.study.cashmachine.TestUtil;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.data.Account;
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
    private ArgumentCaptor<Card> cardCaptor;

    private CashMachineServiceImpl cashMachineService;

    private CashMachine cashMachine = new CashMachine(new MoneyBox());

    @BeforeEach
    void init() {
        cashMachineService = new CashMachineServiceImpl(cardService, accountService, moneyBoxService);
    }


    @Test
    void testGetMoney() {
// @TODO create get money test using spy as mock
        doReturn(BigDecimal.TEN).when(cardService).getMoney("12345", "1111", BigDecimal.TEN);

        when(moneyBoxService.getMoney(any(),anyInt())).thenReturn(List.of(1,1,1,1));

        CashMachine cashMachine = new CashMachine(new MoneyBox());
        List<Integer> result = cashMachineService.getMoney(cashMachine, "12345", "1111", BigDecimal.TEN);

        assertEquals((List.of(1,1,1,1)), result);

    }

    @Test
    void putMoney() {
    }

    @Test
    void checkBalance() {

    }

    @Test
    void changePin() {
// @TODO create change pin test using spy as implementation and ArgumentCaptor and thenReturn
        when(cardsDao.getCardByNumber("12345")).thenReturn(new Card(1, "0000", 1L, TestUtil.getHash("1111")) );
        when(cardsDao.saveCard((cardCaptor.capture()))).thenReturn(new Card( 1, "", 1L, "0000"));
        cashMachineService.changePin("12345", "1111", "0000" );

        assertEquals( TestUtil.getHash("0000"), cardCaptor.getValue().getPinCode());



    }

    @Test
    void changePinWithAnswer() {
// @TODO create change pin test using spy as implementation and mock an thenAnswer
        when(cardsDao.getCardByNumber("12345")).thenReturn(new Card(1, "0000", 1L, TestUtil.getHash("1111")) );

        List<Card> cards = new ArrayList<>();

        when(cardsDao.saveCard(any())).thenAnswer(new Answer<Card>() {
            @Override
            public Card answer(InvocationOnMock invocation) throws Throwable {
                cards.add(invocation.getArgument(0));
                return invocation.getArgument(0);
            }
        });

        cashMachineService.changePin("12345", "1111", "0000" );
        assertEquals( "0000", cards.get(0).getNumber());

    }
}