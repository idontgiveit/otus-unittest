package otus.study.cashmachine.bank.service;


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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    AccountDao accountDao;
    @InjectMocks
    AccountServiceImpl accountServiceImpl;

    @Test
    void createAccountMock() {
// @TODO test account creation with mock and ArgumentMatcher
        Account expectedAccount = new Account(0, BigDecimal.TEN);
        when(accountDao.saveAccount(new Account(0, BigDecimal.TEN))).thenReturn(expectedAccount);
        Account testAccount = accountServiceImpl.createAccount(BigDecimal.TEN);

        assertEquals(expectedAccount, testAccount);

        ArgumentMatcher<Account> matcher = account -> account != null && account.getId() == account.getId();

        assertEquals(true, matcher.matches(testAccount));
    }

    @Test
    void createAccountCaptor() {
//  @TODO test account creation with ArgumentCaptor
        ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);
        when(accountDao.saveAccount(accountArgumentCaptor.capture())).thenReturn(new Account(1L, BigDecimal.TEN));
        accountDao.saveAccount(new Account(1L, BigDecimal.TEN));
        verify(accountDao, only()).saveAccount(accountArgumentCaptor.capture());
        assertEquals( BigDecimal.TEN, accountArgumentCaptor.getValue().getAmount());
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
