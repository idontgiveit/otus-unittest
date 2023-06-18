package otus.study.cashmachine.bank.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import otus.study.cashmachine.TestUtil;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.data.Card;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        Card card = new Card(1L, "1234", 1L, TestUtil.getHash("0000"));
        when(cardsDao.getCardByNumber(anyString())).thenReturn(card);
        when(accountService.checkBalance(1L)).thenReturn(new BigDecimal(1000));

        BigDecimal sum = cardService.getBalance("1234", "0000");
        assertEquals(0, sum.compareTo(new BigDecimal(1000)));
    }

    @Test
    void shouldThrowWhenCheckBalanceAndCardIsNull() {
        when(cardsDao.getCardByNumber(anyString())).thenReturn(null);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> cardService.getBalance("1234", "0000"));
        assertEquals("No card found", ex.getMessage());
    }

    @Test
    void getMoney() {
        ArgumentCaptor<BigDecimal> amountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

        when(cardsDao.getCardByNumber("1111"))
                .thenReturn(new Card(1L, "1111", 100L, TestUtil.getHash("0000")));

        when(accountService.getMoney(idCaptor.capture(), amountCaptor.capture()))
                .thenReturn(BigDecimal.TEN);

        cardService.getMoney("1111", "0000", BigDecimal.ONE);

        verify(accountService, only()).getMoney(anyLong(), any());
        assertEquals(BigDecimal.ONE, amountCaptor.getValue());
        assertEquals(100L, idCaptor.getValue().longValue());
    }

    @Test
    void shouldThrowWhenGetMoneyAndCardIsNull() {
        when(cardsDao.getCardByNumber(anyString())).thenReturn(null);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> cardService.getMoney("1234", "0000", BigDecimal.ONE));
        assertEquals("No card found", ex.getMessage());
    }

    @Test
    void putMoney() {

        ArgumentCaptor<BigDecimal> amountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

        when(cardsDao.getCardByNumber("1111"))
                .thenReturn(new Card(1L, "1111", 100L, TestUtil.getHash("0000")));

        when(accountService.putMoney(idCaptor.capture(), amountCaptor.capture())).thenReturn(new BigDecimal("2"));

        var amount = cardService.putMoney("1111", "0000", BigDecimal.ONE);


        verify(accountService, only()).putMoney(100L, BigDecimal.ONE);
        assertEquals(BigDecimal.ONE, amountCaptor.getValue());
        assertEquals(100L, idCaptor.getValue().longValue());
        assertEquals(new BigDecimal("2"),amount);
    }

    @Test
    void shouldThrowWhenPutMoneyAndCardIsNull() {
        when(cardsDao.getCardByNumber(anyString())).thenReturn(null);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> cardService.putMoney("1234", "0000", BigDecimal.TEN));
        assertEquals("No card found", ex.getMessage());
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
    void testChangePin() {

        ArgumentCaptor<Card> cardCaptor = ArgumentCaptor.forClass(Card.class);

        Card card = new Card(1L, "1111", 1L, TestUtil.getHash("0000"));
        when(cardsDao.getCardByNumber(eq("1111"))).thenReturn(card);

        var pinChanged = cardService.cnangePin("1111", "0000", "1234");
        verify(cardsDao).saveCard(cardCaptor.capture());

        var savedCard = cardCaptor.getValue();

        assertEquals(true, pinChanged);
        assertEquals(1L, savedCard.getAccountId());
        assertEquals(TestUtil.getHash("1234"), savedCard.getPinCode());
    }
}