package otus.study.cashmachine.bank.service;


import net.bytebuddy.asm.MemberSubstitution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.data.Card;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;


public class AccountServiceTest {

    AccountDao accountDao;

    AccountServiceImpl accountServiceImpl;
    AccountService accountService;

    @BeforeEach
    void init() {
        accountDao = mock(AccountDao.class);
        accountServiceImpl = new AccountServiceImpl(accountDao);
        accountService = new AccountServiceImpl(accountDao);
    }

    @Test
    void createAccountMock() {
//  @TODO test account creation with mock and ArgumentMatcher
        when(accountDao.saveAccount(ArgumentMatchers.any(Account.class))).thenReturn(
                new Account(1L, new BigDecimal(1000)));

        Account newAccount = accountService.createAccount(new BigDecimal(1000));

        assertNotEquals(0, newAccount.getId());
        assertEquals(new BigDecimal(1000), newAccount.getAmount());
        assertEquals(1L, newAccount.getId());

    }

    @Test
    void createAccountCaptor() {
//  @TODO test account creation with ArgumentCaptor
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        when(accountDao.saveAccount(captor.capture())).thenReturn(
                new Account(1L, new BigDecimal(1000)));

        Account newAccount = accountServiceImpl.createAccount(new BigDecimal(1000));

        verify(accountDao).saveAccount(any(Account.class));

        assertNotEquals(0, newAccount.getId());
        assertEquals(new BigDecimal(1000), newAccount.getAmount());
        assertEquals(1L, newAccount.getId());
    }

    @Test
    void addSum() {
        when(accountDao.getAccount(anyLong())).thenReturn(
                new Account(1L, new BigDecimal(1000)));

        accountServiceImpl.putMoney(1L, new BigDecimal(2000));

        Account account = accountServiceImpl.getAccount(1L);

        assertEquals(new BigDecimal(3000), account.getAmount());

    }

    @Test
    void getSum() {
        when(accountDao.getAccount(anyLong())).thenReturn(
                new Account(1L, new BigDecimal(1000)));


        assertEquals(new BigDecimal(0), accountServiceImpl.getMoney(1L, new BigDecimal(1000)));
    }

    @Test
    void getAccount() {
        when(accountDao.getAccount(anyLong())).thenReturn(
                new Account(1L, new BigDecimal(1000)));

        Account account = accountServiceImpl.getAccount(1L);

        assertNotEquals(0, account.getId());
        assertEquals(new BigDecimal(1000), account.getAmount());
        assertEquals(1L, account.getId());
    }

    @Test
    void checkBalance() {
        when(accountDao.getAccount(anyLong())).thenReturn(
                new Account(1L, new BigDecimal(1000)));

        assertEquals(new BigDecimal(1000), accountServiceImpl.checkBalance(1L));
    }
}
