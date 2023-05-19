package otus.study.cashmachine.machine.service;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.data.Card;
import otus.study.cashmachine.bank.service.AccountService;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;
import otus.study.cashmachine.machine.data.CashMachine;
import otus.study.cashmachine.machine.data.MoneyBox;
import otus.study.cashmachine.machine.service.impl.CashMachineServiceImpl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
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

    private CashMachine cashMachine = new CashMachine(new MoneyBox());
    
    @BeforeEach
    void init() {
        cashMachineService = new CashMachineServiceImpl(cardService, accountService, moneyBoxService);
    }


    @Test
    void getMoney() {
        //create get money test using spy as mock
        String cardNumber = "cardNumber";
        String pin = "pin";
        BigDecimal amount = BigDecimal.valueOf(2000);

        BigDecimal moneyToReturn = BigDecimal.valueOf(1000);
        doReturn(moneyToReturn).when(cardService).getMoney(cardNumber, pin, amount);

        cashMachineService.getMoney(cashMachine, cardNumber, pin, amount);
    }

    @Test
    void getMoney_exception() {
        String cardNumber = "cardNumber";
        String pin = "pin";
        BigDecimal amount = BigDecimal.valueOf(1400);

        BigDecimal moneyToReturn = BigDecimal.ONE;
        doThrow(IllegalArgumentException.class).when(cardService).getMoney(cardNumber, pin, amount);

        assertThrows(Exception.class, () -> cashMachineService.getMoney(cashMachine, cardNumber, pin, amount));
    }

    @ParameterizedTest
    @MethodSource("putMoney")
    void putMoney(int note5000, int note1000, int note500, int note100, int expectedAmount) {
        long id = 1;
        Long accountId = 1L;
        String cardNumber = "cardNumber";
        String pin = "pin";
        List<Integer> notes = List.of(note5000, note1000, note500, note100);

        Card card = new Card(id, cardNumber, accountId, getHash(pin));
        doReturn(BigDecimal.ONE).when(cardService).getBalance(cardNumber, pin);

        ArgumentCaptor<Integer> note100Captor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> note500Captor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> note1000Captor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> note5000Captor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<BigDecimal> capturedAmount = ArgumentCaptor.forClass(BigDecimal.class);

        doNothing().when(moneyBoxService)
                .putMoney(any(MoneyBox.class), note100Captor.capture(), note500Captor.capture(),
                        note1000Captor.capture(), note5000Captor.capture());
        doReturn(BigDecimal.ONE).when(cardService).putMoney(eq(cardNumber), eq(pin), capturedAmount.capture());
        
        cashMachineService.putMoney(cashMachine, cardNumber, pin, notes);
     
        assertEquals(note100, note100Captor.getValue());
        assertEquals(note500, note500Captor.getValue());
        assertEquals(note1000, note1000Captor.getValue());
        assertEquals(note5000, note5000Captor.getValue());
        
        assertEquals(BigDecimal.valueOf(expectedAmount), capturedAmount.getValue());
    }
    
    static Stream<Arguments> putMoney() {
        return Stream.of(
                Arguments.of(1, 0, 0, 0, 5000),
                Arguments.of(0, 1, 0, 0, 1000),
                Arguments.of(0, 0, 1, 0, 500),
                Arguments.of(0, 0, 0, 1, 100),
                Arguments.of(1, 0, 0, 5, 5500),
                Arguments.of(1, 1, 1, 1, 6600)
        );
    }

    @Test
    void checkBalance() {
        String cardNumber = "cardNumber";
        String pin = "pin";

        BigDecimal toBeReturned = BigDecimal.ONE;
        doReturn(toBeReturned).when(cardService).getBalance(eq(cardNumber), eq(pin));
        BigDecimal result = cashMachineService.checkBalance(cashMachine, cardNumber, pin);

        assertEquals(toBeReturned, result);
    }

    @Test
    void changePin() {
        //create change pin test using spy as implementation and ArgumentCaptor and thenReturn
        String cardNumber = "cardNumber";
        String oldPin = "oldPin";
        String newPin = "newPin";

        ArgumentCaptor<String> newPinCaptor = ArgumentCaptor.forClass(String.class);
        doReturn(true).when(cardService).changePin(eq(cardNumber), eq(oldPin), newPinCaptor.capture());
        
        cashMachineService.changePin(cardNumber, oldPin, newPin);
        
        assertEquals(newPin, newPinCaptor.getValue());
    }

    @Test
    void changePinWithAnswer() {
        //create change pin test using spy as implementation and mock an thenAnswer
        String cardNumber = "cardNumber";
        String oldPin = "oldPin";
        String newPin = "newPin";

        ArgumentCaptor<String> newPinCaptor = ArgumentCaptor.forClass(String.class);
        doReturn(true).when(cardService).changePin(eq(cardNumber), eq(oldPin), newPinCaptor.capture());

        cashMachineService.changePin(cardNumber, oldPin, newPin);

        assertEquals(newPin, newPinCaptor.getValue());
    }
    
    @Test
    void getMoney_spyAsMock() {
        //Немного не понял смысла теста на метод в котором можно замокать только тестируемый метод
        //Было желание сделать мок метода getHash(), но он является приватным методом
        String number = "number";
        String correctPin = "correctPin";
        BigDecimal sum = BigDecimal.TEN;
        BigDecimal enteredSum = BigDecimal.ONE;
        doReturn(enteredSum).when(cardService).getMoney(number, correctPin, sum);
        BigDecimal money = cardService.getMoney(number, correctPin, sum);
        assertEquals(enteredSum, money);
    }
    
    @Test
    void getMoney_old() {
        //create get money test using spy as mock
        String number = "number";
        String correctPin = "correctPin";
        BigDecimal sum = BigDecimal.TEN;

        long id = 1;
        Long accountId = 1L;

        Card card = new Card(id, number, accountId, getHash(correctPin));
        when(cardsDao.getCardByNumber(anyString())).thenReturn(card);
        BigDecimal moneyToReturn = BigDecimal.TEN;
        when(accountService.getMoney(anyLong(), any(BigDecimal.class))).thenReturn(moneyToReturn);

        BigDecimal returnedMoney = cardService.getMoney(number, correctPin, sum);

        assertEquals(moneyToReturn, returnedMoney);
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