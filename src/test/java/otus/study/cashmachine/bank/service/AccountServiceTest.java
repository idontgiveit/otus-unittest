package otus.study.cashmachine.bank.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountDao accountDao;

    @Spy
    @InjectMocks
    private AccountServiceImpl accountServiceImpl;

    @Test
    @DisplayName("Создание мока аккаунта")
    void createAccountMock() {
        Account account = accountServiceImpl.createAccount(ArgumentMatchers.any());
        System.out.println(account);
    }

    @Test
    void createAccountCaptor() {
        Account account = accountServiceImpl.createAccount(ArgumentCaptor.forClass(BigDecimal.class).capture());
        assertNull(account);
    }

    @Test
    void testGetSumSuccess() {
        when(accountDao.getAccount(1L)).thenReturn(new Account(1L, BigDecimal.TEN));
        BigDecimal money = accountServiceImpl.getMoney(1L, BigDecimal.valueOf(2));
        assertEquals(money, BigDecimal.valueOf(8));
    }

    @Test
    void getSumNotEnoughMoney() {
        when(accountDao.getAccount(1L)).thenReturn(new Account(1L, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> accountServiceImpl.getMoney(1L, BigDecimal.valueOf(2)));
    }

    @Test
    void getAccount() {
        when(accountDao.getAccount(1L)).thenReturn(new Account(1L, BigDecimal.TEN));
        Account account = accountServiceImpl.getAccount(1L);
        assertNotNull(account);
    }

    @Test
    void checkBalance() {
        when(accountDao.getAccount(1L)).thenReturn(new Account(1L, BigDecimal.TEN));
        BigDecimal money = accountServiceImpl.checkBalance(1L);
        assertEquals(money, BigDecimal.valueOf(10));
    }
}
