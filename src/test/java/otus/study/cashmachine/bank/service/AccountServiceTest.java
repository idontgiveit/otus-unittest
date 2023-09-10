package otus.study.cashmachine.bank.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class AccountServiceTest {

    AccountDao accountDao;

    AccountServiceImpl accountServiceImpl;

    Account accountMock;

    final long TEST_ID = 88888888L;
    final BigDecimal TEST_AMOUNT = new BigDecimal("6600.00");

    @BeforeEach
    void init() {
        accountMock = mock(Account.class);
        when(accountMock.getId()).thenReturn(TEST_ID);
        when(accountMock.getAmount()).thenReturn(TEST_AMOUNT);

        accountDao = mock(AccountDao.class);
        accountServiceImpl = new AccountServiceImpl(accountDao);
    }

    @Test
    void createAccountMockId() {
        when(accountDao.saveAccount(any())).thenReturn(accountMock);

        Account account = accountServiceImpl.createAccount(TEST_AMOUNT);
        assertEquals(TEST_ID, account.getId());
    }

    @Test
    void createAccountMockAmount() {
        when(accountDao.saveAccount(any())).thenReturn(accountMock);

        Account account = accountServiceImpl.createAccount(TEST_AMOUNT);
        assertEquals(TEST_AMOUNT, account.getAmount());
    }

    @Test
    void createAccountCaptor() {
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        when(accountDao.saveAccount(accountCaptor.capture())).thenReturn(accountMock);

        Account account = accountServiceImpl.createAccount(TEST_AMOUNT);
        assertEquals(TEST_ID, account.getId());
        assertEquals(TEST_AMOUNT, account.getAmount());
    }

    @Test
    void putMoney() {
        when(accountDao.getAccount(anyLong())).thenReturn(new Account(TEST_ID, TEST_AMOUNT));
        assertEquals(TEST_AMOUNT.add(BigDecimal.TEN), accountServiceImpl.putMoney(TEST_ID, BigDecimal.TEN));
    }

    @Test
    void getMoney() {
        when(accountDao.getAccount(anyLong())).thenReturn(new Account(TEST_ID, TEST_AMOUNT));
        assertEquals(new BigDecimal("0.00"), accountServiceImpl.getMoney(TEST_ID, TEST_AMOUNT));

        BigDecimal tooBigAmount = TEST_AMOUNT.add(BigDecimal.ONE);
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> accountServiceImpl.getMoney(TEST_ID, tooBigAmount));
        assertEquals(exception.getMessage(), "Not enough money");
    }

    @Test
    void getAccount() {
        when(accountDao.getAccount(anyLong())).thenReturn(accountMock);
        Account account = accountServiceImpl.getAccount(TEST_ID);
        assertEquals(TEST_AMOUNT, account.getAmount());
        assertEquals(TEST_ID, account.getId());
    }

    @Test
    void checkBalance() {
        when(accountDao.getAccount(anyLong())).thenReturn(accountMock);
        BigDecimal amount = accountServiceImpl.checkBalance(TEST_ID);
        assertEquals(TEST_AMOUNT, amount);
    }
}
