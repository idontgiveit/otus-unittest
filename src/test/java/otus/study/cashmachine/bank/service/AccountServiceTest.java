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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    AccountDao accountDao;
    @InjectMocks
    AccountServiceImpl accountServiceImpl;

    @Test
    void createAccountMock() {
// @TODO test account creation with mock and ArgumentMatcher
        BigDecimal amount = BigDecimal.valueOf(10000);
        accountServiceImpl.createAccount(amount);
        verify(accountDao).saveAccount(any(Account.class));
    }

    @Test
    void createAccountCaptor() {
//  @TODO test account creation with ArgumentCaptor
        ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);
        BigDecimal amount = BigDecimal.valueOf(10000);
        accountServiceImpl.createAccount(amount);
        verify(accountDao).saveAccount(accountArgumentCaptor.capture());
        assertEquals(
                accountArgumentCaptor.getValue().getAmount(),
                amount
        );
    }

    @Test
    void addSum() {
        long id = 1L;
        long startAmount = 100;
        long addAmount = 100;
        Account account = new Account(id, BigDecimal.valueOf(startAmount));

        when(accountDao.getAccount(id)).thenReturn(account);
        accountServiceImpl.putMoney(id, BigDecimal.valueOf(addAmount));
        assertEquals(account.getAmount(), BigDecimal.valueOf(startAmount + addAmount));
    }

    @Test
    void getSum() {
        long id = 1L;
        long initialAmount = 400;
        long subtractAmount = 100;

        Account account = new Account(id, BigDecimal.valueOf(initialAmount));
        when(accountDao.getAccount(id)).thenReturn(account);
        accountServiceImpl.getMoney(id, BigDecimal.valueOf(subtractAmount));
        assertEquals(account.getAmount(), BigDecimal.valueOf(initialAmount - subtractAmount));
    }

    @Test
    void getSumException() {
        long id = 1L;
        long initialAmount = 400;
        long subtractAmount = 500;

        Account account = new Account(id, BigDecimal.valueOf(initialAmount));
        when(accountDao.getAccount(id)).thenReturn(account);
        assertThrows(
                RuntimeException.class,
                () -> accountServiceImpl.getMoney(id, BigDecimal.valueOf(subtractAmount))
        );
    }

    @Test
    void getAccount() {
        long id = 1L;
        Account account = new Account(id, BigDecimal.valueOf(400));
        when(accountDao.getAccount(id)).thenReturn(account);
        assertEquals(
                accountServiceImpl.getAccount(id),
                account
        );
    }

    @Test
    void checkBalance() {
        long id = 1L;
        var balance = BigDecimal.valueOf(500);
        Account account = new Account(id, balance);
        when(accountDao.getAccount(id)).thenReturn(account);

        assertEquals(
                accountServiceImpl.checkBalance(id),
                balance
        );
    }
}
