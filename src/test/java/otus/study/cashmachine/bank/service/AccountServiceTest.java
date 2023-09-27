package otus.study.cashmachine.bank.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class AccountServiceTest {
    private AccountDao accountDao;
    private AccountServiceImpl accountServiceImpl;

    @BeforeEach
    void setUp() {
        accountDao = mock(AccountDao.class);
        accountServiceImpl = new AccountServiceImpl(accountDao);
    }

    @Test
    void testCreateAccount() {
        // Создаем заглушку для сохраненного аккаунта
        Account savedAccount = new Account(1L, BigDecimal.ZERO);

        // Когда accountDao.saveAccount вызывается с любым аккаунтом, вернуть заглушку
        when(accountDao.saveAccount(any(Account.class))).thenReturn(savedAccount);

        BigDecimal initialAmount = new BigDecimal("0");
        Account createdAccount = accountServiceImpl.createAccount(initialAmount);

        // Проверяем, что созданный аккаунт имеет правильный баланс и ID
        assertEquals(savedAccount.getId(), createdAccount.getId());
        assertEquals(initialAmount, createdAccount.getAmount());
    }

    @Test
    void testGetMoney() {
        Long accountId = 1L;
        BigDecimal initialAmount = new BigDecimal("100.00");
        Account account = new Account(accountId, initialAmount);

        // Заглушка для метода getAccount
        when(accountDao.getAccount(accountId)).thenReturn(account);

        BigDecimal amountToWithdraw = new BigDecimal("50.00");
        BigDecimal expectedBalance = initialAmount.subtract(amountToWithdraw);

        // Вызываем метод getMoney и проверяем, что баланс уменьшился на правильную сумму
        BigDecimal newBalance = accountServiceImpl.getMoney(accountId, amountToWithdraw);
        assertEquals(expectedBalance, newBalance);
    }

    @Test
    void testPutMoney() {
        Long accountId = 1L;
        BigDecimal initialAmount = new BigDecimal("100.00");
        Account account = new Account(accountId, initialAmount);
        // Заглушка для метода getAccount
        when(accountDao.getAccount(accountId)).thenReturn(account);
        BigDecimal amountToDeposit = new BigDecimal("50.00");
        BigDecimal expectedBalance = initialAmount.add(amountToDeposit);

        // Вызываем метод putMoney и проверяем, что баланс увеличился на правильную сумму
        BigDecimal newBalance = accountServiceImpl.putMoney(accountId, amountToDeposit);
        assertEquals(expectedBalance, newBalance);
    }

    @Test
    void testCheckBalance() {
        Long accountId = 1L;
        BigDecimal initialAmount = new BigDecimal("100.00");
        Account account = new Account(accountId, initialAmount);

        // Заглушка для метода getAccount
        when(accountDao.getAccount(accountId)).thenReturn(account);

        // Вызываем метод checkBalance и проверяем, что он возвращает правильный баланс
        BigDecimal balance = accountServiceImpl.checkBalance(accountId);
        assertEquals(initialAmount, balance);
    }
}
