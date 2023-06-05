package otus.study.cashmachine.bank.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    BigDecimal expectedAmount;

    Account expectedAccount, unexpectedAccount;

    @Mock
    AccountDao accountDao;

    @InjectMocks
    AccountServiceImpl accountServiceImpl;

    @BeforeEach
    void init() {
        expectedAmount = new BigDecimal(10000);
        expectedAccount = new Account(10, expectedAmount);
        unexpectedAccount = new Account(0, new BigDecimal(1));
    }

    @Test
    void createAccountMock() {
// @TODO test account creation with mock and ArgumentMatcher
        ArgumentMatcher<Account> matcher = acc -> acc.getAmount().compareTo(expectedAmount) == 0;
        when(accountDao.saveAccount(any())).thenReturn(unexpectedAccount);          //Результат по умолчанию
        when(accountDao.saveAccount(argThat(matcher))).thenReturn(expectedAccount); //При определенных условиях

        Account outputAccount1 = accountServiceImpl.createAccount(new BigDecimal(10000));
        Assertions.assertEquals(outputAccount1.getAmount(), expectedAccount.getAmount());

        Account outputAccount2 = accountServiceImpl.createAccount(new BigDecimal(9999));
        Assertions.assertNotEquals(outputAccount2.getAmount(), expectedAccount.getAmount());
    }

    @Test
    void createAccountCaptor() {
//  @TODO test account creation with ArgumentCaptor
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
//        when(accountDao.saveAccount(any())).thenReturn(unexpectedAccount);          //Результат по умолчанию
        when(accountDao.saveAccount(captor.capture())).thenReturn(expectedAccount); //При определенных условиях

        Account outputAccount1 = accountServiceImpl.createAccount(new BigDecimal(10000));
        Assertions.assertEquals(outputAccount1.getAmount(), captor.getValue().getAmount());

        Account outputAccount2 = accountServiceImpl.createAccount(new BigDecimal(9999));
        Assertions.assertNotEquals(outputAccount2.getAmount(), captor.getValue().getAmount());
    }

    private void tryCreateAccount(BigDecimal expectedAmount) {
        Account outputAccount1 = accountServiceImpl.createAccount(new BigDecimal(10000));
        Assertions.assertEquals(outputAccount1.getAmount(), expectedAmount);

        Account outputAccount2 = accountServiceImpl.createAccount(new BigDecimal(9999));
        Assertions.assertNotEquals(outputAccount2.getAmount(), expectedAmount);
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
