package otus.study.cashmachine.bank.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;

import java.math.BigDecimal;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    private AccountDao accountDao;
    @Mock
    private Account account;

    @Test
    void createAccountMock() {
        //@TODO test account creation with mock and ArgumentMatcher
        when(accountDao.saveAccount(argThat(argument ->
                Objects.equals(argument, new Account(1L, new BigDecimal(1000))))))
                .thenReturn(new Account(1L, new BigDecimal(1000)));

        Account newAccount = accountDao.saveAccount(new Account(1L,new BigDecimal(1000)));

        assertNotEquals(0, newAccount.getId());
        assertEquals(1L, newAccount.getId());
        assertEquals(new BigDecimal(1000), newAccount.getAmount());
    }

    @Test
    void createAccountCaptor() {
        // @TODO test account creation with ArgumentCaptor
        //Создание тестовой учетной записи с помощью ArgumentCaptor
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

        when(accountDao.saveAccount(accountCaptor.capture())).thenReturn(
                new Account(1L,new BigDecimal(2000)));

        Account newAccount = accountDao.saveAccount(account);

        assertNotEquals(0, newAccount.getId());
        assertEquals(1L, newAccount.getId());
        assertEquals(new BigDecimal(2000), newAccount.getAmount());
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
