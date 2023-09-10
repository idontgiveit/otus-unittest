package otus.study.cashmachine.machine.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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
        BigDecimal amountToGet = new BigDecimal(100);
        BigDecimal balanceAfterGetting = new BigDecimal(1400);
        String cardNum = "1111";
        String pinCode = "1111";
        List<Integer> expectedReceiveBanknotes = List.of(1, 0, 0, 0);
        doReturn(balanceAfterGetting).when(cardService).getMoney(cardNum, pinCode, amountToGet);
        when(moneyBoxService.getMoney(cashMachine.getMoneyBox(), amountToGet.intValue())).thenReturn(expectedReceiveBanknotes);


        List<Integer> actualReceiveBanknotes = cashMachineService.getMoney(cashMachine, cardNum, pinCode, amountToGet);


        Assertions.assertEquals(expectedReceiveBanknotes, actualReceiveBanknotes);
    }

    @Test
    void getMoney_cardNotFoundException() {
        BigDecimal amountToGet = new BigDecimal(100);
        BigDecimal balance = new BigDecimal(1400);
        String cardNum = "1111";
        String pinCode = "1111";
        doReturn(balance).when(cardService).putMoney(cardNum, pinCode, amountToGet);


        Assertions.assertThrows(RuntimeException.class, () -> cashMachineService.getMoney(cashMachine, cardNum, pinCode, amountToGet));
    }

    @Test
    void putMoney() {
        String cardNum = "1111";
        String pinCode = "1111";
        BigDecimal putAmount = new BigDecimal(5000 + 1000 + 500 + 100);
        ArgumentCaptor<Integer> note100Captor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> note500Captor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> note1000Captor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> note5000Captor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> cardNumCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> pinCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<BigDecimal> amount = ArgumentCaptor.forClass(BigDecimal.class);
        doReturn(null).when(cardService).getBalance(cardNum, pinCode);
        doReturn(putAmount).when(cardService).putMoney(cardNum, pinCode, putAmount);


        cashMachineService.putMoney(cashMachine, cardNum, pinCode, List.of(1, 1, 1, 1));
        verify(moneyBoxService).putMoney(any(MoneyBox.class), note100Captor.capture(), note500Captor.capture(), note1000Captor.capture(), note5000Captor.capture());
        verify(cardService).putMoney(cardNumCaptor.capture(), pinCaptor.capture(), amount.capture());


        Assertions.assertEquals(cardNum, cardNumCaptor.getValue());
        Assertions.assertEquals(pinCode, pinCaptor.getValue());
        Assertions.assertEquals(putAmount, amount.getValue());
    }

    @Test
    void checkBalance() {
        String cardNum = "1111";
        String pin = "1111";
        BigDecimal expectedBalance = new BigDecimal(100);
        ArgumentCaptor<String> cardNumCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> pinCaptor = ArgumentCaptor.forClass(String.class);
        doReturn(expectedBalance).when(cardService).getBalance(cardNum, pin);


        BigDecimal actualBalance = cashMachineService.checkBalance(cashMachine, cardNum, pin);
        verify(cardService).getBalance(cardNumCaptor.capture(), pinCaptor.capture());

        Assertions.assertEquals(expectedBalance, actualBalance);
        Assertions.assertEquals(cardNum, cardNumCaptor.getValue());
        Assertions.assertEquals(pin, pinCaptor.getValue());

    }

    @Test
    void changePin() {
// @TODO create change pin test using spy as implementation and ArgumentCaptor and thenReturn
        String cardNum = "1111";
        String oldPin = "1111";
        String newPin = "9999";
        Card cardBeforeChangePin = new Card(1L, cardNum, 1L, "011c945f30ce2cbafc452f39840f025693339c42");
        Card expectedCardAfterChangePin = new Card(1L, cardNum, 1L, "4170ac2a2782a1516fe9e13d7322ae482c1bd594");
        ArgumentCaptor<Card> cardArgumentCaptor = ArgumentCaptor.forClass(Card.class);
        when(cardsDao.getCardByNumber(cardNum)).thenReturn(cardBeforeChangePin);
        when(cardsDao.saveCard(any(Card.class))).thenReturn(expectedCardAfterChangePin);


        boolean successChangedPin = cashMachineService.changePin(cardNum, oldPin, newPin);
        verify(cardsDao).saveCard(cardArgumentCaptor.capture());


        Card actualCardAfterChangePin = cardArgumentCaptor.getValue();
        Assertions.assertEquals(actualCardAfterChangePin, expectedCardAfterChangePin);
        Assertions.assertTrue(successChangedPin);
    }

    @Test
    void changePinWithAnswer() {
// @TODO create change pin test using spy as implementation and mock an thenAnswer
        String cardNum = "1111";
        String oldPin = "1111";
        String newPin = "9999";
        Card cardBeforeChangePin = new Card(1L, cardNum, 1L, "011c945f30ce2cbafc452f39840f025693339c42");
        Card expectedCardAfterChangePin = new Card(1L, cardNum, 1L, "4170ac2a2782a1516fe9e13d7322ae482c1bd594");
        when(cardsDao.getCardByNumber(cardNum)).thenAnswer(i -> cardBeforeChangePin);
        when(cardsDao.saveCard(any(Card.class))).thenAnswer(i -> expectedCardAfterChangePin);


        boolean successChangedPin = cashMachineService.changePin(cardNum, oldPin, newPin);


        Assertions.assertTrue(successChangedPin);
    }
}