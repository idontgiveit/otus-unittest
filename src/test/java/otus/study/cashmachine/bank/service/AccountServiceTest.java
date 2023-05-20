package otus.study.cashmachine.bank.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    AccountDao accountDao;
    @InjectMocks
    AccountServiceImpl accountServiceImpl;

    @Test
    @MockitoSettings(strictness = Strictness.WARN)
    void createAccountMock() {
        //test account creation with mock and ArgumentMatcher
        Account expectedAccount = new Account(0,BigDecimal.TEN);
        ArgumentMatcher<Account> matcher = argument -> argument.getAmount().compareTo(expectedAccount.getAmount()) == 0;
        when(accountDao.saveAccount(any())).thenReturn(expectedAccount);
        when(accountDao.saveAccount(argThat(matcher))).thenReturn(expectedAccount);
        Account testAccount = accountServiceImpl.createAccount(BigDecimal.TEN);
        assertEquals(expectedAccount.getAmount(),testAccount.getAmount());
    }

    @Test
    void createAccountCaptor() {
        //test account creation with ArgumentCaptor
        Account expectedAccount = new Account(1,BigDecimal.TEN);
        ArgumentCaptor<Account> argumentCaptor = ArgumentCaptor.forClass(Account.class);
        when(accountDao.saveAccount(argumentCaptor.capture())).thenReturn(new Account(1, BigDecimal.TEN));
        accountServiceImpl.createAccount(BigDecimal.TEN);
        assertEquals(expectedAccount.getAmount(),argumentCaptor.getValue().getAmount());
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
