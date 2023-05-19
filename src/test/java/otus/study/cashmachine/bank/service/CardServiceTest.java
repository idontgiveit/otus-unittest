package otus.study.cashmachine.bank.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.data.Card;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static otus.study.cashmachine.TestUtil.getHash;

public class CardServiceTest {
    AccountService accountService;

    CardsDao cardsDao;

    CardService cardService;

    @BeforeEach
    void init() {
        cardsDao = mock(CardsDao.class);
        accountService = mock(AccountService.class);
        cardService = new CardServiceImpl(accountService, cardsDao);
    }

    @Test
    void testCreateCard() {
        when(cardsDao.createCard("5555", 1L, "0123")).thenReturn(
                new Card(1L, "5555", 1L, "0123"));

        Card newCard = cardService.createCard("5555", 1L, "0123");
        assertNotEquals(0, newCard.getId());
        assertEquals("5555", newCard.getNumber());
        assertEquals(1L, newCard.getAccountId());
        assertEquals("0123", newCard.getPinCode());
    }

    @Test
    void checkBalance() {
        Card card = new Card(1L, "1234", 1L, getHash("0000"));
        when(cardsDao.getCardByNumber(anyString())).thenReturn(card);
        when(accountService.checkBalance(1L)).thenReturn(new BigDecimal(1000));

        BigDecimal sum = cardService.getBalance("1234", "0000");
        assertEquals(0, sum.compareTo(new BigDecimal(1000)));
    }

    @Test
    void getMoney() {
        ArgumentCaptor<BigDecimal> amountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

        when(cardsDao.getCardByNumber("1111"))
                .thenReturn(new Card(1L, "1111", 100L, getHash("0000")));

        when(accountService.getMoney(idCaptor.capture(), amountCaptor.capture()))
                .thenReturn(BigDecimal.TEN);

        cardService.getMoney("1111", "0000", BigDecimal.ONE);

        verify(accountService, only()).getMoney(anyLong(), any());
        assertEquals(BigDecimal.ONE, amountCaptor.getValue());
        assertEquals(100L, idCaptor.getValue().longValue());
    }

    @Test
    void getMoney_numberNull() {
        String number = "number";
        String pin = "pin";
        BigDecimal sum = BigDecimal.TEN;

        when(cardsDao.getCardByNumber(anyString())).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.getMoney(number, pin, sum));

        String expectedExceptionMessage = "No card found";
        assertEquals(expectedExceptionMessage, exception.getMessage());
    }

    @Test
    void getMoney_incorrectPin() {
        String number = "number";
        String correctPin = "correctPin";
        String incorrectPin = "incorrectPin";
        BigDecimal sum = BigDecimal.TEN;

        long id = 1;
        Long accountId = 1L;

        Card card = new Card(id, number, accountId, getHash(correctPin));
        when(cardsDao.getCardByNumber(anyString())).thenReturn(card);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.getMoney(number, incorrectPin, sum));

        String expectedExceptionMessage = "Pincode is incorrect";
        assertEquals(expectedExceptionMessage, exception.getMessage());
    }
    
    @Test
    void putMoney() {
        String number = "number";
        String correctPin = "correctPin";
        BigDecimal sum = BigDecimal.TEN;

        long id = 1;
        Long accountId = 1L;

        Card card = new Card(id, number, accountId, getHash(correctPin));
        when(cardsDao.getCardByNumber(anyString())).thenReturn(card);
        BigDecimal moneyToReturn = BigDecimal.TEN;
        when(accountService.putMoney(anyLong(), any(BigDecimal.class))).thenReturn(moneyToReturn);

        BigDecimal returnedMoney = cardService.putMoney(number, correctPin, sum);

        assertEquals(moneyToReturn, returnedMoney);
    }

    @Test
    void putMoney_numberNull() {
        String number = "number";
        String pin = "pin";
        BigDecimal sum = BigDecimal.TEN;

        when(cardsDao.getCardByNumber(anyString())).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.putMoney(number, pin, sum));

        String expectedExceptionMessage = "No card found";
        assertEquals(expectedExceptionMessage, exception.getMessage());
    }

    @Test
    void putMoney_incorrectPin() {
        String number = "number";
        String correctPin = "correctPin";
        String incorrectPin = "incorrectPin";
        BigDecimal sum = BigDecimal.TEN;

        long id = 1;
        Long accountId = 1L;

        Card card = new Card(id, number, accountId, getHash(correctPin));
        when(cardsDao.getCardByNumber(anyString())).thenReturn(card);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.putMoney(number, incorrectPin, sum));

        String expectedExceptionMessage = "Pincode is incorrect";
        assertEquals(expectedExceptionMessage, exception.getMessage());
    }

    @Test
    void checkIncorrectPin() {
        Card card = new Card(1L, "1234", 1L, "0000");
        when(cardsDao.getCardByNumber(eq("1234"))).thenReturn(card);

        Exception thrown = assertThrows(IllegalArgumentException.class, () -> {
            cardService.getBalance("1234", "0012");
        });
        assertEquals(thrown.getMessage(), "Pincode is incorrect");
    }


    @Test
    void getBalance() {
        String number = "number";
        String correctPin = "correctPin";

        long id = 1;
        Long accountId = 1L;

        Card card = new Card(id, number, accountId, getHash(correctPin));
        when(cardsDao.getCardByNumber(anyString())).thenReturn(card);

        BigDecimal moneyOnBalance = BigDecimal.TEN;
        when(accountService.checkBalance(anyLong())).thenReturn(moneyOnBalance);

        BigDecimal resultBalance = cardService.getBalance(number, correctPin);

        assertEquals(moneyOnBalance, resultBalance);
    }

    @Test
    void getBalance_numberNull() {
        String number = "number";
        String pin = "pin";

        when(cardsDao.getCardByNumber(anyString())).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.getBalance(number, pin));

        String expectedExceptionMessage = "No card found";
        assertEquals(expectedExceptionMessage, exception.getMessage());
    }

    @Test
    void getBalance_incorrectPin() {
        String number = "number";
        String correctPin = "correctPin";
        String incorrectPin = "incorrectPin";

        long id = 1;
        Long accountId = 1L;

        Card card = new Card(id, number, accountId, getHash(correctPin));
        when(cardsDao.getCardByNumber(anyString())).thenReturn(card);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.getBalance(number, incorrectPin));

        String expectedExceptionMessage = "Pincode is incorrect";
        assertEquals(expectedExceptionMessage, exception.getMessage());
    }

    @Test
    void changePin() {
        //create change pin test using spy as implementation and ArgumentCaptor and thenReturn
        String number = "number";
        String oldPin = "oldPin";
        String newPin = "newPin";

        long id = 1;
        Long accountId = 1L;

        ArgumentCaptor<Card> cardCaptor = ArgumentCaptor.forClass(Card.class);

        Card card = new Card(id, number, accountId, getHash(oldPin));
        when(cardsDao.getCardByNumber(anyString())).thenReturn(card);

        Card savedCard = new Card(id, number, accountId, getHash(newPin));
        when(cardsDao.saveCard(cardCaptor.capture())).thenReturn(savedCard);

        boolean result = cardService.changePin(number, oldPin, newPin);

        assertTrue(result);
        Card capturedCard = cardCaptor.getValue();
        assertEquals(getHash(newPin), capturedCard.getPinCode());
    }

    @Test
    void changePin_numberNull() {
        //create change pin test using spy as implementation and ArgumentCaptor and thenReturn
        String number = "number";
        String oldPin = "oldPin";
        String newPin = "newPin";

        when(cardsDao.getCardByNumber(anyString())).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cardService.changePin(number, oldPin, newPin));

        String expectedExceptionMessage = "No card found";
        assertEquals(expectedExceptionMessage, exception.getMessage());
    }

    @Test
    void changePin_incorrectPin() {
        //create change pin test using spy as implementation and ArgumentCaptor and thenReturn
        String number = "number";
        String oldPin = "oldPin";
        String newPin = "newPin";
        String incorrectPin = "incorrectPin";

        long id = 1;
        Long accountId = 1L;

        Card card = new Card(id, number, accountId, getHash(oldPin));
        when(cardsDao.getCardByNumber(anyString())).thenReturn(card);

        boolean result = cardService.changePin(number, incorrectPin, newPin);

        assertFalse(result);
    }

    @Test
    void changePinWithAnswer() {
        //create change pin test using spy as implementation and mock an thenAnswer
        String number = "number";
        String oldPin = "oldPin";
        String newPin = "newPin";

        long id = 1;
        Long accountId = 1L;

        List<Card> customCardCaptor = new ArrayList<>();

        Card card = new Card(id, number, accountId, getHash(oldPin));
        when(cardsDao.getCardByNumber(anyString())).thenReturn(card);

        when(cardsDao.saveCard(any(Card.class))).thenAnswer((Answer<Card>) invocation -> {
            customCardCaptor.add(invocation.getArgument(0));
            return new Card(id, number, accountId, getHash(newPin));
        });

        boolean result = cardService.changePin(number, oldPin, newPin);

        assertTrue(result);
        Card capturedCard = customCardCaptor.get(0);
        assertEquals(getHash(newPin), capturedCard.getPinCode());
    }

}