package otus.study.cashmachine.bank.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountDao accountDao;

    @InjectMocks
    private AccountServiceImpl accountServiceImpl;

    @Test
    void createAccountMock() {
        Account account = new Account(0, ONE);
        when(accountDao.saveAccount(account)).thenReturn(account);

        assertEquals(account, accountServiceImpl.createAccount(ONE));
    }

    @Test
    void createAccountCaptor() {
        ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);

        accountServiceImpl.createAccount(ONE);

        verify(accountDao, only()).saveAccount(accountArgumentCaptor.capture());
        Account actual = accountArgumentCaptor.getValue();
        assertEquals(ONE, actual.getAmount());
        assertEquals(0L, actual.getId());
    }

    @Test
    void addSum() {
        long ID = 3L;
        Account account = new Account(ID, ONE);
        when(accountDao.getAccount(ID)).thenReturn(account);

        BigDecimal actual = accountServiceImpl.putMoney(ID, ONE);

        assertEquals(BigDecimal.valueOf(2), actual);
    }

    @Test
    void getSum() {
        long ID = 3L;
        Account account = new Account(ID, TEN);
        when(accountDao.getAccount(ID)).thenReturn(account);

        BigDecimal actual = accountServiceImpl.getMoney(ID, ONE);

        assertEquals(BigDecimal.valueOf(9), actual);
    }

    @Test
    void getSumWhenNotMoney() {
        long ID = 3L;
        Account account = new Account(ID, ONE);
        when(accountDao.getAccount(ID)).thenReturn(account);

        Exception thrown = assertThrows(IllegalArgumentException.class, () -> {
            accountServiceImpl.getMoney(ID, TEN);
        });

        assertEquals("Not enough money", thrown.getMessage());
    }

    @Test
    void getAccount() {
        long ID = 3L;
        Account account = new Account(ID, TEN);
        when(accountDao.getAccount(ID)).thenReturn(account);

        Account actual = accountServiceImpl.getAccount(ID);

        assertEquals(account, actual);
    }

    @Test
    void checkBalance() {
        long ID = 3L;
        Account account = new Account(ID, TEN);
        when(accountDao.getAccount(ID)).thenReturn(account);

        BigDecimal actual = accountServiceImpl.checkBalance(ID);

        assertEquals(TEN, actual);
    }
}
