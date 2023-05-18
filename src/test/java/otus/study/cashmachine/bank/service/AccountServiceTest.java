package otus.study.cashmachine.bank.service;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    AccountDao accountDao;

    @InjectMocks
    AccountServiceImpl accountServiceImpl;

    @Test
    @DisplayName("Test createAccount with mock and ArgumentMatcher")
    void createAccountMock() {
        // @TODO test account creation with mock and ArgumentMatcher

        // given
        Account expectedAccount = new Account(0, BigDecimal.ONE);

        ArgumentMatcher<Account> matcher = new ArgumentMatcher<>() {
            @Override
            public boolean matches(Account argument) {
                return argument.getAmount().compareTo(expectedAccount.getAmount()) == 0;
            }
        };

        when(accountDao.saveAccount(argThat(matcher))).thenReturn(new Account(0, BigDecimal.ONE));

        //when
        Account actualAccount = accountServiceImpl.createAccount(BigDecimal.ONE);

        //then
        assertEquals(expectedAccount.getAmount(), actualAccount.getAmount());
    }

    @Test
    @DisplayName("Test createAccount with ArgumentCaptor")
    void createAccountCaptor() {
        //  @TODO test account creation with ArgumentCaptor

        // given
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        when(accountDao.saveAccount(captor.capture())).thenReturn(new Account(0, BigDecimal.ONE));

        //when
        Account actualAccount = accountServiceImpl.createAccount(BigDecimal.ONE);

        //then
        assertEquals(actualAccount.getAmount(), captor.getValue().getAmount());
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
