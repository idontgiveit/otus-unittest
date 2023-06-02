package otus.study.cashmachine.bank.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    AccountDao accountDao;
    @InjectMocks
    AccountServiceImpl accountServiceImpl;

    @Test
    @DisplayName("Test account creation with mock and ArgumentMatcher")
    void createAccountMock() {
        Account accountExpected = new Account(0, BigDecimal.TEN);
        ArgumentMatcher<Account> matcher = argument -> argument.getAmount().compareTo(accountExpected.getAmount()) == 0;
        when(accountDao.saveAccount(argThat(matcher))).thenReturn(new Account(10,BigDecimal.TEN));

        Account accountActual = accountServiceImpl.createAccount(BigDecimal.TEN);
        Assertions.assertNotEquals(accountExpected.getId(),accountActual.getId());
        Assertions.assertEquals(accountExpected.getAmount(),accountActual.getAmount());
    }

    @Test
    @DisplayName("Test account creation with ArgumentCaptor")
    void createAccountCaptor() {
        ArgumentCaptor<Account> argumentCaptor = ArgumentCaptor.forClass(Account.class);

        when(accountDao.saveAccount(argumentCaptor.capture())).thenReturn(new Account(0,BigDecimal.TEN));

        Account accountActual = accountServiceImpl.createAccount(BigDecimal.TEN);
        Assertions.assertEquals(accountActual.getAmount(),argumentCaptor.getValue().getAmount());
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