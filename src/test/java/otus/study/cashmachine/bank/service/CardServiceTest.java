package otus.study.cashmachine.bank.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.data.Card;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
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
    void putMoney() {
        Card card = new Card(1L, "1234", 1L, getHash("0050"));
        when(cardsDao.getCardByNumber(anyString())).thenReturn(card);
        when(accountService.putMoney(1L, BigDecimal.valueOf(400))).thenReturn(BigDecimal.valueOf(600));

        BigDecimal actual = cardService.putMoney("1234", "0050", BigDecimal.valueOf(400));

        assertEquals(BigDecimal.valueOf(600), actual);
    }

    @Test
    void putMoneyException() {
        Card card = null;
        when(cardsDao.getCardByNumber(anyString())).thenReturn(card);

        Exception thrown = assertThrows(IllegalArgumentException.class, () -> cardService.putMoney("1234", "0050", BigDecimal.valueOf(400)));

        assertEquals("No card found", thrown.getMessage());
    }

    @Test
    void checkIncorrectPin() {
        Card card = new Card(1L, "1234", 1L, "0000");
        when(cardsDao.getCardByNumber(eq("1234"))).thenReturn(card);

        Exception thrown = assertThrows(IllegalArgumentException.class, () -> {
            cardService.getBalance("1234", "0012");
        });

        assertEquals("Pincode is incorrect", thrown.getMessage());
    }

    @Test
    void changePinException() {
        Exception thrown = assertThrows(IllegalArgumentException.class, () -> cardService.cnangePin("123", "5555", "6666"));

        assertEquals("No card found", thrown.getMessage());
    }

    @Test
    void changePinSuccess() {
        Card card = new Card(1L, "1234", 1L, getHash("0050"));
        when(cardsDao.getCardByNumber(anyString())).thenReturn(card);

        boolean actual = cardService.cnangePin("123", "0050", "6666");

        assertTrue(actual);
        verify(cardsDao, times(1)).saveCard(card);
    }

    @Test
    void changePinFalse() {
        Card card = new Card(1L, "1234", 1L, getHash("0050"));
        when(cardsDao.getCardByNumber(anyString())).thenReturn(card);
        when(accountService.putMoney(1L, BigDecimal.valueOf(400))).thenReturn(BigDecimal.valueOf(600));

        boolean actual = cardService.cnangePin("123", "00501", "6666");

        assertFalse(actual);
    }
}