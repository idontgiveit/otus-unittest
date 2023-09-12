package otus.study.cashmachine.bank.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    AccountDao accountDao;

    @Captor
    private ArgumentCaptor<Account> accountCaptor;
    @InjectMocks
    AccountServiceImpl accountServiceImpl;

    Account account;
    BigDecimal accountDecimal;
    BigDecimal bigDecimal;

    @BeforeEach
    void setUp() {
        accountDecimal = BigDecimal.valueOf(10000);
        account = new Account(0, accountDecimal);
        bigDecimal = BigDecimal.valueOf(500);
    }

    @Test
    void createAccountMock() {
        Account newAccount = new Account(0, bigDecimal);
        ArgumentMatcher<Account> accountArgumentMatcher = arg -> arg.getId() == 0;
        when(accountDao.saveAccount(argThat(accountArgumentMatcher))).thenReturn(newAccount);
        assertNotEquals(account.getAmount(), accountServiceImpl.createAccount(bigDecimal).getAmount());
    }

    @Test
    void createAccountCaptor() {
        Account newAccount = new Account(0, bigDecimal);
        when(accountDao.saveAccount(accountCaptor.capture())).thenReturn(newAccount);
        Account createdAccount = accountServiceImpl.createAccount(bigDecimal);
        Account caputredAccount = accountCaptor.getValue();
        assertEquals(newAccount, createdAccount);
        assertEquals(caputredAccount.getAmount(), bigDecimal);
    }

    @Test
    void getMoney() {
        BigDecimal subtractedDecimal = BigDecimal.TEN;
        BigDecimal overvaluedDecimal = BigDecimal.valueOf(1000000000L);
        when(accountDao.getAccount(0L)).thenReturn(account);
        assertEquals(BigDecimal.valueOf(9990L), accountServiceImpl.getMoney(0L, subtractedDecimal));
        assertThrows(IllegalArgumentException.class, () -> accountServiceImpl.getMoney(0L, overvaluedDecimal));
    }

    @Test
    void putMoney() {
        BigDecimal addedDecimal = BigDecimal.TEN;
        when(accountDao.getAccount(0L)).thenReturn(account);
        assertEquals(BigDecimal.valueOf(10010L), accountServiceImpl.putMoney(0L, addedDecimal));
    }

    @Test
    void getAccount() {
        when(accountDao.getAccount(0L)).thenReturn(account);
        assertEquals(account, accountServiceImpl.getAccount(0L));
    }

    @Test
    void checkBalance() {
        when(accountDao.getAccount(0L)).thenReturn(account);
        assertEquals(account.getAmount(), accountServiceImpl.checkBalance(0L));
    }
}
