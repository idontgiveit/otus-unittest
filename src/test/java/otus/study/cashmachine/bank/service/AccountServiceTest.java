package otus.study.cashmachine.bank.service;


import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    AccountDao accountDao;

    @InjectMocks
    AccountServiceImpl accountServiceImpl;

    @Captor
    private ArgumentCaptor<Account> accountCaptor;
    
    @Test
    void createAccountMock() {
        //test resultAccount creation with mock and ArgumentMatcher
        BigDecimal entryAmount = BigDecimal.TEN;
        Account expectedAccount = new Account(0, entryAmount);
        ArgumentMatcher<Account> accountArgumentMatcher = argument -> argument.getId() == 0;
        when(accountDao.saveAccount(argThat(accountArgumentMatcher))).thenReturn(expectedAccount);
        Account resultAccount = accountServiceImpl.createAccount(entryAmount);
        assertEquals(expectedAccount, resultAccount);
    }

    @Test
    void createAccountCaptor() {
        //test account creation with ArgumentCaptor
        BigDecimal entryAmount = BigDecimal.TEN;
        Account expectedAccount = new Account(0, entryAmount);
        when(accountDao.saveAccount(accountCaptor.capture())).thenReturn(expectedAccount);
        Account resultAccount = accountServiceImpl.createAccount(entryAmount);
        Account capturedAccount = accountCaptor.getValue();
        assertEquals(expectedAccount, capturedAccount);
        assertEquals(resultAccount, capturedAccount);
        assertEquals(capturedAccount.getAmount(), entryAmount);
    }

    @Test
    void getAccount() {
        long id = 1L;
        BigDecimal amount = BigDecimal.TEN;
        Account account = new Account(id, amount);
        when(accountDao.getAccount(eq(id))).thenReturn(account);
        Account result = accountServiceImpl.getAccount(id);
        assertEquals(id, account.getId());
        assertEquals(amount, account.getAmount());
    }
    
    @Test
    void putMoney() {
        long id = 1L;
        BigDecimal amount = BigDecimal.TEN;
        Account account = new Account(id, amount);
        when(accountDao.getAccount(eq(id))).thenReturn(account);

        BigDecimal moneyToPut = BigDecimal.valueOf(20);

        BigDecimal result = accountServiceImpl.putMoney(id, moneyToPut);
        assertEquals(amount.add(moneyToPut), result);
    }

    @Test
    void getMoney_notEnoughMoney() {
        long id = 1L;
        BigDecimal amount = BigDecimal.TEN;
        Account account = new Account(id, amount);
        when(accountDao.getAccount(eq(id))).thenReturn(account);

        BigDecimal moneyToGet = BigDecimal.valueOf(20);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> accountServiceImpl.getMoney(id, moneyToGet));
        
        assertEquals("Not enough money", exception.getMessage());
    }


    @Test
    void getMoney() {
        long id = 1L;
        BigDecimal amount = BigDecimal.valueOf(30);
        Account account = new Account(id, amount);
        when(accountDao.getAccount(eq(id))).thenReturn(account);

        BigDecimal moneyToGet = BigDecimal.valueOf(20);

        BigDecimal result = accountServiceImpl.getMoney(id, moneyToGet);

        assertEquals(BigDecimal.valueOf(10), result);
    }

    @Test
    void checkBalance() {
        long id = 1L;
        BigDecimal amount = BigDecimal.TEN;
        Account account = new Account(id, amount);
        when(accountDao.getAccount(eq(id))).thenReturn(account);

        BigDecimal result = accountServiceImpl.checkBalance(id);
        assertEquals(amount, result);
    }
}
