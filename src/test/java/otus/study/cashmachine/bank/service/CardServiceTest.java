package otus.study.cashmachine.bank.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.TestUtil;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.data.Card;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @InjectMocks
    AccountServiceImpl accountService;
    @Mock
    CardsDao cardsDao;

    CardService cardService;
    @Mock
    AccountDao accountDao;

    @BeforeEach
    void init() {

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
        when(accountDao.getAccount(1L)).thenReturn(new Account(1L,new BigDecimal(1000)));

        BigDecimal sum = cardService.getBalance("1234", "0000");
        assertEquals(0, sum.compareTo(new BigDecimal(1000)));
    }

    @Test
    void getMoney() {
        when(cardsDao.getCardByNumber("1111")).thenReturn(new Card(1L, "1111", 1L, TestUtil.getHash("0000")));
        when(accountDao.getAccount(1L)).thenReturn(new Account(1L, new BigDecimal(1000)));
        BigDecimal cardExt = cardService.getMoney("1111", "0000", BigDecimal.ONE);
        BigDecimal result = BigDecimal.valueOf(1000).subtract(BigDecimal.ONE);
        assertEquals(cardExt, result);
    }

    @Test
    void putMoney() {
        when(cardsDao.getCardByNumber("1111")).thenReturn(new Card(1L, "1111", 1L, TestUtil.getHash("0000")));
        when(accountDao.getAccount(1L)).thenReturn(new Account(1L, new BigDecimal(1000)));
        BigDecimal cardExt = cardService.putMoney("1111", "0000", BigDecimal.ONE);
        BigDecimal result = BigDecimal.valueOf(1000).add(BigDecimal.ONE);
        assertEquals(cardExt, result);

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
}
