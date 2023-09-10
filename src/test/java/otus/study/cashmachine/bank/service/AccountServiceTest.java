package otus.study.cashmachine.bank.service;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class AccountServiceTest {

    AccountDao accountDao;

    AccountServiceImpl accountServiceImpl;

    final long TEST_ACCOUNT_ACCOUNTID = 1L;
    final BigDecimal TEST_AMOUNT = new BigDecimal(5000);
    final BigDecimal TEST_AMOUNT_PUT = new BigDecimal(1000);
    final BigDecimal TEST_AMOUNT_RESULT = new BigDecimal(6000);
    final BigDecimal TEST_AMOUNT_ERROR = new BigDecimal(7000);
    @BeforeEach
    void init() {
        accountDao = mock(AccountDao.class);
        accountServiceImpl = new AccountServiceImpl(accountDao);
    }

    @Test
    void createAccountMock() {
// @TODO test account creation with mock and ArgumentMatcher
        when(accountDao.saveAccount(any(Account.class))).thenReturn(
                    new Account(TEST_ACCOUNT_ACCOUNTID,TEST_AMOUNT));
        Account account = accountServiceImpl.createAccount(TEST_AMOUNT);
        assertEquals(TEST_AMOUNT, account.getAmount());
    }

    @Test
    void createAccountCaptor() {
//  @TODO test account creation with ArgumentCaptor
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        when(accountDao.saveAccount(accountCaptor.capture())).thenReturn(
                new Account(TEST_ACCOUNT_ACCOUNTID,TEST_AMOUNT));
        Account account = accountServiceImpl.createAccount(TEST_AMOUNT);
        assertEquals(TEST_AMOUNT, account.getAmount());
    }

    @Test
    void addSum() {
        when(accountDao.getAccount(TEST_ACCOUNT_ACCOUNTID)).thenReturn(
                new Account(TEST_ACCOUNT_ACCOUNTID,TEST_AMOUNT));
        accountServiceImpl.putMoney(TEST_ACCOUNT_ACCOUNTID, TEST_AMOUNT_PUT);
        Account account = accountServiceImpl.getAccount(TEST_ACCOUNT_ACCOUNTID);
        assertEquals(TEST_AMOUNT_RESULT, account.getAmount());
    }

    @Test
    void getSum() {
        when(accountDao.getAccount(TEST_ACCOUNT_ACCOUNTID)).thenReturn(
                new Account(TEST_ACCOUNT_ACCOUNTID,TEST_AMOUNT));
        assertEquals(new BigDecimal(0),
                accountServiceImpl.getMoney(TEST_ACCOUNT_ACCOUNTID, TEST_AMOUNT));
    }

    @Test
    void getAccount() {
        when(accountDao.getAccount(TEST_ACCOUNT_ACCOUNTID)).thenReturn(
                new Account(TEST_ACCOUNT_ACCOUNTID,TEST_AMOUNT));
        Account account = accountServiceImpl.getAccount(TEST_ACCOUNT_ACCOUNTID);
        assertEquals(TEST_AMOUNT, account.getAmount());
    }

    @Test
    void checkBalance() {
        when(accountDao.getAccount(TEST_ACCOUNT_ACCOUNTID)).thenReturn(
                new Account(TEST_ACCOUNT_ACCOUNTID,TEST_AMOUNT));
        assertEquals(TEST_AMOUNT, accountServiceImpl.checkBalance(TEST_ACCOUNT_ACCOUNTID));
    }

    @Test
    void getThrowSum() {
        when(accountDao.getAccount(TEST_ACCOUNT_ACCOUNTID)).thenReturn(
                new Account(TEST_ACCOUNT_ACCOUNTID,TEST_AMOUNT));
        Exception thrown = assertThrows(IllegalArgumentException.class, () -> {
            accountServiceImpl.getMoney(TEST_ACCOUNT_ACCOUNTID, TEST_AMOUNT_ERROR);
        });
        assertEquals(thrown.getMessage(), "Not enough money");
    }

}
