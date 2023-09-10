package otus.study.cashmachine.bank.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    AccountDao accountDao;

    AccountServiceImpl accountServiceImpl;

    @Captor
    ArgumentCaptor<Account> argCaptor;

    @BeforeEach
    void init() {
        accountServiceImpl = new AccountServiceImpl(accountDao);
    }

    @Test
    void createAccountMock() {
// @TODO test account creation with mock and ArgumentMatcher
        int accountAmount = 100;
        Account expectedAccount = new Account(4, new BigDecimal(accountAmount));
        when(accountDao.saveAccount(ArgumentMatchers.any(Account.class))).thenReturn(expectedAccount);


        Account savedAccount = accountServiceImpl.createAccount(new BigDecimal(accountAmount));


        Assertions.assertEquals(expectedAccount, savedAccount);
    }

    @Test
    void createAccountCaptor() {
//  @TODO test account creation with ArgumentCaptor
        int accountAmount = 100;
        Account expectedAccount = new Account(0, new BigDecimal(accountAmount));


        accountServiceImpl.createAccount(new BigDecimal(accountAmount));
        verify(accountDao, times(1)).saveAccount(argCaptor.capture());


        Account savedAccount = argCaptor.getValue();
        Assertions.assertEquals(expectedAccount.getId(), savedAccount.getId());
        Assertions.assertEquals(expectedAccount.getAmount(), savedAccount.getAmount());
    }

    @Test
    void addSum() {
    }

    @Test
    void getSum() {
    }

    @Test
    void getMoney() {
        long accountId = 1L;
        BigDecimal accountAmount = new BigDecimal(1000);
        Account expectedAccount = new Account(accountId, accountAmount);
        when(accountDao.getAccount(accountId)).thenReturn(expectedAccount);


        BigDecimal actualBalance = accountServiceImpl.getMoney(accountId, accountAmount);


        Assertions.assertEquals(new BigDecimal(0), actualBalance);
    }

    @Test
    void getMoney_notEnoughMoneyException() {
        long accountId = 1L;
        BigDecimal accountAmount = new BigDecimal(1000);
        BigDecimal tryToGet = new BigDecimal(9999);
        Account expectedAccount = new Account(accountId, accountAmount);
        when(accountDao.getAccount(accountId)).thenReturn(expectedAccount);


        Assertions.assertThrows(IllegalArgumentException.class, () -> accountServiceImpl.getMoney(accountId, tryToGet));
    }

    @Test
    void getAccount() {
        long accountId = 1L;
        BigDecimal accountAmount = new BigDecimal(1000);
        Account expectedAccount = new Account(accountId, accountAmount);
        when(accountDao.getAccount(accountId)).thenReturn(expectedAccount);


        Account actualAccount = accountServiceImpl.getAccount(accountId);


        Assertions.assertEquals(expectedAccount, actualAccount);
    }

    @Test
    void checkBalance() {
        long accountId = 1L;
        BigDecimal expectedBalance = new BigDecimal(1000);
        Account expectedAccount = new Account(accountId, expectedBalance);
        when(accountDao.getAccount(accountId)).thenReturn(expectedAccount);


        BigDecimal actualBalance = accountServiceImpl.checkBalance(accountId);


        Assertions.assertEquals(expectedBalance, actualBalance);
    }
}
