package otus.study.cashmachine.bank.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;


@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    AccountDao accountDao;

    @InjectMocks
    AccountServiceImpl accountService;

    @Test
    void createAccountMock() {

        var amount = new BigDecimal("100");
        Mockito.when(accountDao.saveAccount(Mockito.any())).thenAnswer(invocation -> invocation.getArguments()[0]);
        Account account = accountService.createAccount(amount);

        Assertions.assertEquals(amount, account.getAmount());
    }

    @Test
    void createAccountCaptor() {

        var amount = new BigDecimal("100");
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

        Account account = accountService.createAccount(amount);

        Mockito.verify(accountDao).saveAccount(accountCaptor.capture());

        Assertions.assertEquals(amount, accountCaptor.getValue().getAmount());
    }

    @Test
    void addSum() {

        var amount = new BigDecimal("100");
        var addAmount = new BigDecimal("20");
        Mockito.when(accountDao.getAccount(Mockito.anyLong())).thenReturn(new Account(1L, amount));
        var actualAmount = accountService.putMoney(1l, addAmount);

        Assertions.assertEquals(amount.add(addAmount), actualAmount);
    }

    @Test
    void getSum() {

        var amount = new BigDecimal("100");
        var getAmount = new BigDecimal("20");
        Mockito.when(accountDao.getAccount(Mockito.anyLong())).thenReturn(new Account(1L, amount));
        var actualAmount = accountService.getMoney(1l, getAmount);

        Assertions.assertEquals(amount.subtract(getAmount), actualAmount);
    }

    @Test
    void getMoreThanAmountOnAccount() {

        var amount = new BigDecimal("100");
        var getAmount = new BigDecimal("120");
        Mockito.when(accountDao.getAccount(Mockito.anyLong())).thenReturn(new Account(1L, amount));

        Exception thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            accountService.getMoney(1L, getAmount);
        });
        Assertions.assertEquals("Not enough money", thrown.getMessage());
    }

    @Test
    void getAccount() {

        var account = new Account(1l, new BigDecimal("100"));

        Mockito.when(accountDao.getAccount(1L)).thenReturn(new Account(1L, new BigDecimal("100")));
        var extractedAccount = accountService.getAccount(1L);

        Assertions.assertEquals(account, extractedAccount);
    }

    @Test
    void checkBalance() {

        var amount = new BigDecimal("100");
        Mockito.when(accountDao.getAccount(Mockito.anyLong())).thenReturn(new Account(1L, amount));
        var actualAmount = accountService.checkBalance(Mockito.anyLong());

        Assertions.assertEquals(amount, actualAmount);
    }
}
