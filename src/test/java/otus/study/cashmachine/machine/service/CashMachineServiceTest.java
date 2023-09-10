package otus.study.cashmachine.machine.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.data.Card;
import otus.study.cashmachine.bank.service.AccountService;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;
import otus.study.cashmachine.machine.data.CashMachine;
import otus.study.cashmachine.machine.data.MoneyBox;
import otus.study.cashmachine.machine.service.impl.CashMachineServiceImpl;
import otus.study.cashmachine.machine.service.impl.MoneyBoxServiceImpl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
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

    final long TEST_CARD_ID = 10L;
    final String TEST_CARD_NUMBER = "5555";
    final String TEST_CARD_PINCODE_OLD = "1111";
    final String TEST_CARD_PINCODE_NEW = "2222";
    final long TEST_ACCOUNT_ACCOUNTID = 10L;
    final int TEST_1_MONEY = 1;
    final BigDecimal TEST_AMOUNT = new BigDecimal(6600);
    final List<Integer> TEST_LIST_MONEY = new ArrayList<>(Arrays.asList(TEST_1_MONEY,TEST_1_MONEY,TEST_1_MONEY,TEST_1_MONEY));

    @BeforeEach
    void init() {
        cashMachineService = new CashMachineServiceImpl(cardService, accountService, moneyBoxService);
        when(cardsDao.getCardByNumber(any())).thenReturn(new Card(TEST_CARD_ID, TEST_CARD_NUMBER, TEST_ACCOUNT_ACCOUNTID, getHash(TEST_CARD_PINCODE_OLD)));
    }

    @Test
    void getMoney() {
// @TODO create get money test using spy as mock
        when(cardService.getMoney(TEST_CARD_NUMBER,TEST_CARD_PINCODE_OLD,TEST_AMOUNT)).thenReturn(TEST_AMOUNT);
        when(moneyBoxService.getMoney(cashMachine.getMoneyBox(), TEST_AMOUNT.intValue())).thenReturn(TEST_LIST_MONEY);
        assertEquals(TEST_LIST_MONEY, cashMachineService.getMoney(cashMachine, TEST_CARD_NUMBER,TEST_CARD_PINCODE_OLD,TEST_AMOUNT));
    }

    @Test
    void putMoney() {
        when(cardService.getBalance(TEST_CARD_NUMBER, TEST_CARD_PINCODE_OLD)).thenReturn(TEST_AMOUNT);
        when(cardService.putMoney(TEST_CARD_NUMBER, TEST_CARD_PINCODE_OLD, TEST_AMOUNT)).thenReturn(TEST_AMOUNT);
        assertEquals(TEST_AMOUNT,cashMachineService.putMoney(cashMachine, TEST_CARD_NUMBER, TEST_CARD_PINCODE_OLD, TEST_LIST_MONEY));
    }

    @Test
    void checkBalance() {
        when(cardService.getBalance(TEST_CARD_NUMBER, TEST_CARD_PINCODE_OLD)).thenReturn(TEST_AMOUNT);
        assertEquals(TEST_AMOUNT,cashMachineService.checkBalance(cashMachine, TEST_CARD_NUMBER, TEST_CARD_PINCODE_OLD));
    }

    @Test
    void changePin() {
// @TODO create change pin test using spy as implementation and ArgumentCaptor and thenReturn
        ArgumentCaptor<String> cardNumberCaptor = ArgumentCaptor.forClass(String.class);
        when(cardService.cnangePin(cardNumberCaptor.capture(), eq(TEST_CARD_PINCODE_OLD),eq(TEST_CARD_PINCODE_NEW))).thenReturn(true);
        assertTrue(cashMachineService.changePin(TEST_CARD_NUMBER, TEST_CARD_PINCODE_OLD, TEST_CARD_PINCODE_NEW));
    }

    @Test
    void changePinWithAnswer() {
// @TODO create change pin test using spy as implementation and mock an thenAnswer
        when(cardService.cnangePin(TEST_CARD_NUMBER, TEST_CARD_PINCODE_OLD,TEST_CARD_PINCODE_NEW)).thenAnswer(result->true);
        assertTrue(cashMachineService.changePin(TEST_CARD_NUMBER, TEST_CARD_PINCODE_OLD, TEST_CARD_PINCODE_NEW));
    }

    private String getHash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            digest.update(value.getBytes());
            String result = HexFormat.of().formatHex(digest.digest());
            return result;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}