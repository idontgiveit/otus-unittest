package otus.study.cashmachine.bank.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AccountServiceTest {

    @Mock
    private AccountDao accountDao;

    private AccountServiceImpl accountServiceImpl;

    @BeforeEach
    void setUp() {
        accountDao = Mockito.mock(AccountDao.class);
        accountServiceImpl = new AccountServiceImpl(accountDao);
    }

    @Test
    void createAccountMock() {
// @TODO test account creation with mock and ArgumentMatcher
        BigDecimal initialAmount = new BigDecimal(100);
        Account savedAccount = new Account(1L, initialAmount);

        when(accountDao.saveAccount(ArgumentMatchers.any(Account.class))).thenReturn(savedAccount);

        Account createdAccount = accountServiceImpl.createAccount(initialAmount);

        assertEquals(savedAccount, createdAccount);
    }

    @Test
    void createAccountCaptor() {
//  @TODO test account creation with ArgumentCaptor
        BigDecimal initialAmount = new BigDecimal(200);
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

        accountServiceImpl.createAccount(initialAmount);

        verify(accountDao).saveAccount(accountCaptor.capture());
        assertEquals(initialAmount, accountCaptor.getValue().getAmount());
    }

    @Test
    void addSum() {
        Long accountId = 1L;
        BigDecimal currentAmount = new BigDecimal(100);
        BigDecimal addedAmount = new BigDecimal(50);
        Account mockAccount = new Account(accountId, currentAmount);
        when(accountDao.getAccount(accountId)).thenReturn(mockAccount);

        BigDecimal newAmount = accountServiceImpl.putMoney(accountId, addedAmount);

        assertEquals(currentAmount.add(addedAmount), newAmount);
    }

    @Test
    void getSum() {
        Long accountId = 2L;
        BigDecimal currentAmount = new BigDecimal(200);
        BigDecimal withdrawnAmount = new BigDecimal(50);
        Account mockAccount = new Account(accountId, currentAmount);
        when(accountDao.getAccount(accountId)).thenReturn(mockAccount);

        BigDecimal newAmount = accountServiceImpl.getMoney(accountId, withdrawnAmount);

        assertEquals(currentAmount.subtract(withdrawnAmount), newAmount);
    }

    @Test
    void getAccount() {
        Long accountId = 3L;
        BigDecimal currentAmount = new BigDecimal(300);
        Account mockAccount = new Account(accountId, currentAmount);
        when(accountDao.getAccount(accountId)).thenReturn(mockAccount);

        Account retrievedAccount = accountServiceImpl.getAccount(accountId);

        assertEquals(mockAccount, retrievedAccount);
    }

    @Test
    void checkBalance() {
        Long accountId = 4L;
        BigDecimal currentAmount = new BigDecimal(400);
        Account mockAccount = new Account(accountId, currentAmount);
        when(accountDao.getAccount(accountId)).thenReturn(mockAccount);

        BigDecimal balance = accountServiceImpl.checkBalance(accountId);

        assertEquals(currentAmount, balance);
    }
}
