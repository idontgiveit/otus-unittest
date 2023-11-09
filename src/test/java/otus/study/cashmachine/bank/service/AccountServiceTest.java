package otus.study.cashmachine.bank.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
//import otus.study.cashmachine.bank.dao.AccountDao;
import org.mockito.ArgumentCaptor;
import org.mockito.verification.VerificationMode;
import otus.study.cashmachine.TestUtil;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.data.Card;
import otus.study.cashmachine.bank.db.Accounts;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    AccountDao accountDao;
    AccountServiceImpl accountServiceImpl;

    @Test
    void createAccountMock() {
// @TODO test account creation with mock and ArgumentMatcher
        // Create a mock object
        Account myMock = mock(Account.class);

        // Define the behavior of the mock object
        myMock.setAmount(new BigDecimal(1000));
        when(myMock.getAmount()).thenReturn(BigDecimal.valueOf(1000));
    }

    @Test
    void createAccountCaptor() {
//  @TODO test account creation with ArgumentCaptor
        accountDao = mock(AccountDao.class);
        accountServiceImpl = mock(AccountServiceImpl.class);

        ArgumentCaptor<Long> accountArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<BigDecimal> amountArgumentCaptor = ArgumentCaptor.forClass(BigDecimal.class);

        when(accountDao.getAccount(1L))
                .thenReturn(new Account(1L, new BigDecimal(10000)));

        when(accountServiceImpl.getMoney(1L, new BigDecimal(10000)))
                .thenReturn(new BigDecimal(1000)
                );

        verify(accountServiceImpl).getMoney(accountArgumentCaptor.capture(), amountArgumentCaptor.capture());
        BigDecimal amount = accountServiceImpl.getMoney(1L, new BigDecimal(10000));
        assertEquals(new BigDecimal(1000), amount);
    }


    @Test
    void addSum() {
        accountServiceImpl = mock(AccountServiceImpl.class);
        verify(accountServiceImpl).putMoney(1L, new BigDecimal(1000));
    }

    @Test
    void getSum() {
        accountServiceImpl = mock(AccountServiceImpl.class);
        verify(accountServiceImpl).checkBalance(1L);
    }


    @Test
    void getAccount() {
        accountServiceImpl = mock(AccountServiceImpl.class);
        verify(accountServiceImpl).getAccount(1L);
    }

    @Test
    void checkBalance() {
        accountServiceImpl = mock(AccountServiceImpl.class);
        verify(accountServiceImpl).checkBalance(1L);
    }
}
