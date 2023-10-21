package otus.study.cashmachine.bank.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

public class AccountServiceTest {
    public static final Long DEFAULT_ACCOUNT_ID = 0L;
    public static final Long EXIST_ACCOUNT_ID = 1L;
    public static final BigDecimal AMOUNT = BigDecimal.valueOf(100);
    public static final BigDecimal EXPECTED_AMOUNT = BigDecimal.valueOf(200);


    private AccountDao accountDao;

    private AccountService accountService;

    @Captor
    private ArgumentCaptor<Account> accountCaptor;

    @BeforeEach
    void init() {
        accountDao = mock(AccountDao.class);
        accountService = new AccountServiceImpl(accountDao);
        Account expectedAccount = new Account(EXIST_ACCOUNT_ID, AMOUNT);
        when(accountDao.saveAccount(argThat(acc -> acc.getAmount().equals(AMOUNT)))).thenReturn(expectedAccount);
        when(accountDao.getAccount(EXIST_ACCOUNT_ID)).thenReturn(expectedAccount);
        accountCaptor = ArgumentCaptor.forClass(Account.class);
    }

    @Test
    void createAccountMock() {
// @TODO test account creation with mock and ArgumentMatcher
        Account actual = accountService.createAccount(AMOUNT);
        assertThat(actual.getId()).isEqualTo(1);
        assertThat(actual.getAmount()).isEqualTo(AMOUNT);
    }

    @Test
    void createAccountCaptor() {
 //  @TODO test account creation with ArgumentCaptor
        accountService.createAccount(AMOUNT);

        verify(accountDao).saveAccount(accountCaptor.capture());
        Account accountCaptorValue = accountCaptor.getValue();

        assertThat(accountCaptorValue.getId()).isEqualTo(DEFAULT_ACCOUNT_ID);
        assertThat(accountCaptorValue.getAmount()).isEqualTo(AMOUNT);
    }

    @Test
    void addSum() {
        BigDecimal actualAmount = accountService.putMoney(EXIST_ACCOUNT_ID, AMOUNT);
        assertThat(actualAmount).isEqualTo(EXPECTED_AMOUNT);
    }

    @Test
    void getSum() {
        BigDecimal reminder = accountService.getMoney(EXIST_ACCOUNT_ID, AMOUNT);
        assertThat(reminder).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void getAccount() {
        Account account = accountService.getAccount(EXIST_ACCOUNT_ID);
        assertThat(account.getId()).isEqualTo(EXIST_ACCOUNT_ID);
        assertThat(account.getAmount()).isEqualTo(AMOUNT);
    }

    @Test
    void checkBalance() {
        BigDecimal balance = accountService.checkBalance(EXIST_ACCOUNT_ID);
        assertThat(balance).isEqualTo(AMOUNT);
    }
}
