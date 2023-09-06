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
    }

    @Test
    void getSum() {
    }

    @Test
    void getAccount() {
    }

    @Test
    void checkBalance() {
    }
}
