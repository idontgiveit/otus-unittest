package otus.study.cashmachine.bank.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.db.Accounts; // убрать
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.mockito.Mockito.verify;


public class AccountServiceTest {

    AccountDao accountDao;

    AccountServiceImpl accountServiceImpl;

    @Test
    void createAccountMock() {
// @TODO test account creation with mock and ArgumentMatcher

        Account accBasic = new Account(15L, new BigDecimal(5000));

        AccountServiceImpl accountServiceImpl = Mockito.mock(AccountServiceImpl.class);
        Mockito.when(accountServiceImpl.createAccount(ArgumentMatchers.any())).thenReturn(new Account(15L, new BigDecimal(5000)));

        Account acc = accountServiceImpl.createAccount(new BigDecimal(5000));

        Assertions.assertEquals(acc, accBasic);
    }

    @Test
    void createAccountCaptor() {
//  @TODO test account creation with ArgumentCaptor
        Account accBasic = new Account(15L, new BigDecimal(5000));

        ArgumentCaptor<BigDecimal> captor = ArgumentCaptor.forClass(BigDecimal.class);
        AccountServiceImpl accountServiceImpl = Mockito.mock(AccountServiceImpl.class);

        Mockito.when(accountServiceImpl.createAccount(ArgumentMatchers.any())).thenReturn(new Account(15L, new BigDecimal(5000)));

        Account acc = accountServiceImpl.createAccount(new BigDecimal(5000));

        verify(accountServiceImpl).createAccount(captor.capture());

        Assertions.assertEquals(new BigDecimal(5000), captor.getValue());
        Assertions.assertEquals(acc, accBasic);
    }

    @Test
    void addSum() {
    }

    @Test
    void getSum() {
    }

    @Test
    void getAccount() {
    }

    @Test
    void checkBalance() {
    }
}
