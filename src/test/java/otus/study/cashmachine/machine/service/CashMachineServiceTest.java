package otus.study.cashmachine.machine.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.service.AccountService;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;
import otus.study.cashmachine.machine.data.CashMachine;
import otus.study.cashmachine.machine.data.MoneyBox;
import otus.study.cashmachine.machine.service.impl.CashMachineServiceImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
// @TODO create get money test using spy as mock
        //ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        //создайте тест на получение денег, используя spy в качестве мока
        doReturn(new BigDecimal(1000)).when(cardService).getMoney(any(), any(), any());
        when(moneyBoxService.getMoney(cashMachine.getMoneyBox(), 1000)).thenReturn(List.of(1000));


        List<Integer> money = cashMachineService.getMoney(cashMachine, "1111", "0000", new BigDecimal(1000));
        assertNotNull(money);
        assertEquals(1, money.size());
        assertEquals(1000, money.get(0));
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
        //создайте тест на изменение pin-кода, используя spy в качестве реализации и ArgumentCaptor и thenReturn

        ArgumentCaptor<String> cardNumCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> oldPinCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> newPinCaptor = ArgumentCaptor.forClass(String.class);

        doReturn(true).when(cardService).cnangePin(any(), any(), any());

        cashMachineService.changePin("11111", "0000", "1234");
        verify(cardService, times(1))
                .cnangePin(cardNumCaptor.capture(), oldPinCaptor.capture(), newPinCaptor.capture());
        assertEquals("11111", cardNumCaptor.getValue());
        assertEquals("0000", oldPinCaptor.getValue());
        assertEquals("1234", newPinCaptor.getValue());
    }

    @Test
    void changePinWithAnswer() {
// @TODO create change pin test using spy as implementation and mock an thenAnswer
        //создайте тест на изменение pin-кода, используя spy в качестве реализации, и смоделируйте ответ thenAnswer
        doAnswer(invocation -> {
            Object arg0 = invocation.getArgument(0);
            Object arg1 = invocation.getArgument(1);

            assertEquals("11111", arg0);
            assertEquals("0000", arg1);
            return null;
        }).when(cardService).cnangePin(any(),any(),any());
        cardService.cnangePin("11111","0000","11111");
    }
}