package otus.study.cashmachine.bank.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class AccountServiceTest {

    @InjectMocks
    private AccountServiceImpl accountServiceImpl;
    @Mock
    private AccountDao accountDao;

    @Captor
    private ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

    private Account accountExpected;

    private final long ACCOUNT_ID = 1L;
    private final BigDecimal AMOUNT = new BigDecimal("1000");
    private final BigDecimal ADDED_AMOUNT = new BigDecimal("2000");
    private final BigDecimal WITHDRAW_AMOUNT = new BigDecimal("500");
    private final BigDecimal EXCEED_AMOUNT = new BigDecimal("5000");
    @BeforeEach
    void init() {
        accountExpected = new Account(ACCOUNT_ID, AMOUNT);
    }

    @Test
    void createAccountMock() {
        when(accountDao.saveAccount(any()))
                .thenReturn(accountExpected);

        var result = accountServiceImpl.createAccount(AMOUNT);
        Assertions.assertThat(result).isEqualTo(accountExpected);
    }

    @Test
    void createAccountCaptor() {
        accountServiceImpl.createAccount(AMOUNT);
        verify(accountDao, times(1)).saveAccount(accountCaptor.capture());
        Assertions.assertThat(accountCaptor.getValue().getId()).isZero();
    }

    @Test
    void addSum() {
        when(accountDao.getAccount(anyLong()))
                .thenReturn(accountExpected);

        var result = accountServiceImpl.putMoney(ACCOUNT_ID, AMOUNT);
        Assertions.assertThat(result).isEqualTo(ADDED_AMOUNT);
    }

    @Test
    void getSum() {
        when(accountDao.getAccount(anyLong()))
                .thenReturn(accountExpected);

        var result = accountServiceImpl.getMoney(ACCOUNT_ID, WITHDRAW_AMOUNT);
        Assertions.assertThat(result).isEqualTo(WITHDRAW_AMOUNT);
    }

    @Test
    void getSumFailed() {
        when(accountDao.getAccount(anyLong()))
                .thenReturn(accountExpected);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> accountServiceImpl.getMoney(ACCOUNT_ID, EXCEED_AMOUNT))
                .withMessage("Not enough money");
    }

    @Test
    void getAccount() {
        when(accountDao.getAccount(anyLong()))
                .thenReturn(accountExpected);

        var result = accountServiceImpl.getAccount(ACCOUNT_ID);
        Assertions.assertThat(result).isEqualTo(accountExpected);
    }

    @Test
    void checkBalance() {
        when(accountDao.getAccount(anyLong()))
                .thenReturn(accountExpected);

        var result = accountServiceImpl.checkBalance(ACCOUNT_ID);
        Assertions.assertThat(result).isEqualTo(AMOUNT);
    }
}
