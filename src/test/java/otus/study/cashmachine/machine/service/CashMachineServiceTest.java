package otus.study.cashmachine.machine.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.TestUtil;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.data.Card;
import otus.study.cashmachine.bank.service.AccountService;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;
import otus.study.cashmachine.machine.data.CashMachine;
import otus.study.cashmachine.machine.data.MoneyBox;
import otus.study.cashmachine.machine.service.impl.CashMachineServiceImpl;
import otus.study.cashmachine.machine.service.impl.MoneyBoxServiceImpl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        when(cardsDao.getCardByNumber("1111"))
                .thenReturn(new Card(1L, "1111", 1L, TestUtil.getHash("0000")));

        when(accountService.getMoney(1L, new BigDecimal(1000)))
                .thenReturn(new BigDecimal(0));

        when(moneyBoxService.getMoney(any(MoneyBox.class), anyInt())).thenReturn(Arrays.asList(10,10,10,10));


        assertEquals(Arrays.asList(10,10,10,10), cashMachineService.getMoney(cashMachine, "1111", "0000", new BigDecimal(1000)));
    }

    @Test
    void getMoneyIncorrectPin() {
        when(cardsDao.getCardByNumber("1111"))
                .thenReturn(new Card(1L, "1111", 1L, TestUtil.getHash("0000")));

        
        Exception thrown = assertThrows(RuntimeException.class, () -> {
            cashMachineService.getMoney(cashMachine, "1111", "0001", new BigDecimal(1000));
        });


        assertEquals("Pincode is incorrect", thrown.getMessage());
    }

    @Test
    void putMoney() {

        when(cardsDao.getCardByNumber("1111"))
                .thenReturn(new Card(1L, "1111", 1L, TestUtil.getHash("0000")));

        when(cardService.getBalance("1111", "0000"))
               .thenReturn(new BigDecimal(11600));

        when(accountService.putMoney(1L, new BigDecimal(6600)))
                .thenReturn(new BigDecimal(11600));

        doNothing().when(moneyBoxService).putMoney(any(), anyInt(), anyInt(), anyInt(), anyInt());

        cashMachineService.putMoney(cashMachine, "1111", "0000", List.of(1,1,1,1));


        assertEquals(new BigDecimal(11600), cashMachineService.checkBalance(cashMachine, "1111", "0000"));
    }

    @Test
    void checkBalance() {

    }

    @Test
    void changePin() {
// @TODO create change pin test using spy as implementation and ArgumentCaptor and thenReturn
    }

    @Test
    void changePinWithAnswer() {
// @TODO create change pin test using spy as implementation and mock an thenAnswer
    }
}