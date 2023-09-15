package otus.study.cashmachine.bank.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountDao accountDao;

    @InjectMocks
    private AccountServiceImpl accountServiceImpl;

    @Test
    void createAccountMock() {
        BigDecimal amountToAccount = new BigDecimal(10);
        Account account = new Account(0, amountToAccount);

        ArgumentMatcher<Account> amountMatcher = new ArgumentMatcher<Account>() {
            @Override
            public boolean matches(Account argument) {
                return account.getId() == 0 && account.getAmount().equals(amountToAccount);
            }
        };

        when(accountDao.saveAccount(any())).thenReturn(account);

        Account account2 = accountServiceImpl.createAccount(amountToAccount);

        verify(accountDao).saveAccount(Mockito.argThat(amountMatcher));
        assertSame(account, account2);
    }

    @Test
    void createAccountCaptor() {
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        BigDecimal amountToAccount = new BigDecimal(1000);
        Account account = new Account(0, amountToAccount);

        when(accountDao.saveAccount(any())).thenReturn(account);

        accountServiceImpl.createAccount(amountToAccount);

        verify(accountDao).saveAccount(accountCaptor.capture());
        assertEquals(amountToAccount, accountCaptor.getValue().getAmount());
    }

    @Test
    void addSum() {
        Long id = 1L;
        BigDecimal amountToAdd = new BigDecimal(1500);
        BigDecimal accountAmount = new BigDecimal(3000);
        Account account = new Account(id, accountAmount);

        when(accountDao.getAccount(any())).thenReturn(account);

        BigDecimal newAmount = accountServiceImpl.putMoney(id, amountToAdd);

        assertEquals(new BigDecimal(4500), newAmount);
    }

    @Test
    void getSum() {
        Long id = 1L;
        BigDecimal amountToGet = new BigDecimal(1500);
        BigDecimal accountAmount = new BigDecimal(3000);
        Account account = new Account(id, accountAmount);

        when(accountDao.getAccount(any())).thenReturn(account);

        BigDecimal money = accountServiceImpl.getMoney(id, amountToGet);
        assertEquals(new BigDecimal(1500), money);
    }

    @Test
    void getSum2() {
        Long id = 1L;
        BigDecimal amountToGet = new BigDecimal(1500);
        BigDecimal accountAmount = new BigDecimal(1000);
        Account account = new Account(id, accountAmount);

        when(accountDao.getAccount(any())).thenReturn(account);
        assertThrows(IllegalArgumentException.class, () -> accountServiceImpl.getMoney(id, amountToGet));
    }

    @Test
    void getAccount() {
        Long id = 1L;
        BigDecimal accountAmount = new BigDecimal(1000);
        Account account = new Account(id, accountAmount);

        when(accountDao.getAccount(any())).thenReturn(account);

        Account resultAccount = accountServiceImpl.getAccount(id);

        verify(accountDao, times(1)).getAccount(id);
        assertEquals(account.getId(), resultAccount.getId());
    }

    @Test
    void checkBalance() {
        Long id = 1L;
        BigDecimal accountAmount = new BigDecimal(5000);
        Account account = new Account(id, accountAmount);

        when(accountDao.getAccount(any())).thenReturn(account);

        BigDecimal balance = accountServiceImpl.checkBalance(id);

        verify(accountDao, times(1)).getAccount(id);
        assertEquals(accountAmount, balance);
    }
}
