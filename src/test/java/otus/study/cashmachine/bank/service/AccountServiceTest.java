package otus.study.cashmachine.bank.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


public class AccountServiceTest {

    AccountDao accountDao = Mockito.mock(AccountDao.class);

    AccountServiceImpl accountServiceImpl = new AccountServiceImpl(accountDao);



    @Test
    void createAccountMock() {
// @TODO test account creation with mock and ArgumentMatcher
   when(accountDao.saveAccount(any(Account.class))).thenReturn(new Account(0, new BigDecimal(999999999999999999L)));
   Account accountExpected = new Account(0,new BigDecimal(999999999999999999L));
   Account accountActual = accountServiceImpl.createAccount(new BigDecimal(999999999999999999L));
        assertEquals(accountExpected, accountActual);
    }

    @Test
    void createAccountCaptor() {
//  @TODO test account creation with ArgumentCaptor
        ArgumentCaptor <Account> argumentCaptor = ArgumentCaptor.forClass(Account.class);
        when(accountDao.saveAccount(argumentCaptor.capture())).thenReturn(new Account(0, new BigDecimal(999999999999999999L)));
   Account accountExpected = new Account(0,new BigDecimal(999999999999999999L));
   Account accountActual = accountServiceImpl.createAccount(new BigDecimal(999999999999999999L));
        assertEquals(accountExpected, accountActual);
        Mockito.verify(accountDao).saveAccount(argumentCaptor.getValue());
    }
    @Test
    void addSum() {
        Account actualAccount = new Account(1, new BigDecimal(777));
        when(accountDao.getAccount(anyLong())).thenReturn(actualAccount);
        accountServiceImpl.putMoney(1L, new BigDecimal(123));
        Account accountExpected = new Account(1L, new BigDecimal(900));
        assertEquals(accountExpected, actualAccount);
    }

    @Test
    void getSum() {
        Account actualAccount = new Account(1, new BigDecimal(777));
        when(accountDao.getAccount(anyLong())).thenReturn(actualAccount);
        accountServiceImpl.getMoney(1L, new BigDecimal(123));
        Account accountExpected = new Account(1L, new BigDecimal(654));
        assertEquals(accountExpected, actualAccount);
    }

    @Test
    void getSumException () {
        Account account = new Account(1, new BigDecimal(777));
        when(accountDao.getAccount(1L)).thenReturn(account);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountServiceImpl.getMoney(1L, new BigDecimal(100_000));
        });
        String expectedMessage = "Not enough money";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    @Test
    void getAccount() {
        Long id = 1L;
        Account account = new Account(id, new BigDecimal(777));
        when(accountDao.getAccount(id)).thenReturn(account);
        accountServiceImpl.getAccount(id);
        verify(accountDao).getAccount(id);
    }

    @Test
    void checkBalance() {
        BigDecimal amountExpected = new BigDecimal(777);
        Long id = 1L;
        Account account = new Account(id, amountExpected);
        when(accountDao.getAccount(id)).thenReturn(account);
        BigDecimal amountActual = accountServiceImpl.checkBalance(id);
        assertEquals(amountExpected, amountActual);
    }
}
