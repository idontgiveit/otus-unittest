package otus.study.cashmachine.bank.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    AccountService accountService;
    Account testAccount;
    @Mock
    AccountDao accountDao;

    @BeforeEach
    void init() {
        accountService = new AccountServiceImpl(accountDao);
        testAccount = accountService.createAccount(new BigDecimal(10000));
    }

    @Test
    void createAccount() {
        BigDecimal initialAmount = new BigDecimal(100);
        Account savedAccount = new Account(1L, initialAmount);
        when(accountDao.saveAccount(ArgumentMatchers.any(Account.class))).thenReturn(savedAccount);
        Account createdAccount = accountService.createAccount(initialAmount);
        assertEquals(savedAccount, createdAccount);

    }

    @Test
    void addSum() {
        BigDecimal currentAmount = new BigDecimal(100);
        BigDecimal addedAmount = new BigDecimal(50);
        Account mockAccount = new Account(1L, currentAmount);
        when(accountDao.getAccount(1L)).thenReturn(mockAccount);
        BigDecimal newAmount = accountService.putMoney(1L, addedAmount);
        assertEquals(currentAmount.add(addedAmount), newAmount);
    }

    @Test
    void getSum() {

        BigDecimal currentAmount = new BigDecimal(200);
        BigDecimal withdrawnAmount = new BigDecimal(50);
        Account mockAccount = new Account(2L, currentAmount);
        when(accountDao.getAccount(2L)).thenReturn(mockAccount);
        BigDecimal newAmount = accountService.getMoney(2L, withdrawnAmount);
        assertEquals(currentAmount.subtract(withdrawnAmount), newAmount);
    }

    @Test
    void getAccount() {
        BigDecimal currentAmount = new BigDecimal(100);
        Account mockAccount = new Account(3L, currentAmount);
        when(accountDao.getAccount(3L)).thenReturn(mockAccount);
        Account retrievedAccount = accountService.getAccount(3L);
        assertEquals(mockAccount, retrievedAccount);
    }

    @Test
    void checkBalance() {
        BigDecimal currentAmount = new BigDecimal(100);
        Account mockAccount = new Account(4L, currentAmount);
        when(accountDao.getAccount(4L)).thenReturn(mockAccount);
        BigDecimal balance = accountService.checkBalance(4L);
        assertEquals(currentAmount, balance);
    }
}
