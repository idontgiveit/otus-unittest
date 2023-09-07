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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    AccountDao accountDaoMock;

    @InjectMocks
    AccountServiceImpl accountServiceImpl;

    private final BigDecimal TEST_START_AMOUNT_VALUE = BigDecimal.valueOf(2000);
    private final BigDecimal TEST_ADDITIONAL_AMOUNT_VALUE = BigDecimal.valueOf(500);
    private final long TEST_ACCOUNT_ID = 10L;

    @Test
    @DisplayName("Testing creation of Account class using mock and ArgumentMatcher")
    void createAccountMock() {
    // @TODO test account creation with mock and ArgumentMatcher
        var expectedAccount = new Account(5, BigDecimal.valueOf(6000));
        ArgumentMatcher<Account> matcherEqualId = account -> account.getAmount().equals(expectedAccount.getAmount());
        when(accountDaoMock.saveAccount(argThat(matcherEqualId))).thenReturn(expectedAccount);
        var newAccount = accountServiceImpl.createAccount(new BigDecimal(6000));
        assertEquals(newAccount, expectedAccount);
        
    }

    @Test
    @DisplayName("Testing creation of Account class using ArgumentCaptor")
    void createAccountCaptor() {
    //  @TODO test account creation with ArgumentCaptor
        var accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);
        accountServiceImpl.createAccount(TEST_START_AMOUNT_VALUE);
        verify(accountDaoMock, Mockito.atLeastOnce()).saveAccount(accountArgumentCaptor.capture());
        assertEquals(TEST_START_AMOUNT_VALUE, accountArgumentCaptor.getValue().getAmount());
    }

    @Test
    @DisplayName("Testing addSum() method")
    void addSum() {
        var expectedAccount = new Account(TEST_ACCOUNT_ID, TEST_START_AMOUNT_VALUE);
        when(accountDaoMock.getAccount(TEST_ACCOUNT_ID)).thenReturn(expectedAccount);
        accountServiceImpl.putMoney(TEST_ACCOUNT_ID, TEST_ADDITIONAL_AMOUNT_VALUE);
        assertEquals(accountServiceImpl.checkBalance(TEST_ACCOUNT_ID), TEST_START_AMOUNT_VALUE.add(TEST_ADDITIONAL_AMOUNT_VALUE));
    }

    @Test
    @DisplayName("Testing getSum() method")
    void getSum() {
        var expectedAccount = new Account(TEST_ACCOUNT_ID, TEST_START_AMOUNT_VALUE);
        when(accountDaoMock.getAccount(TEST_ACCOUNT_ID)).thenReturn(expectedAccount);
        assertEquals(accountServiceImpl.checkBalance(TEST_ACCOUNT_ID), TEST_START_AMOUNT_VALUE);
    }

    @Test
    @DisplayName("Testing getAccount() method")
    void getAccount() {
        var expectedAccount = new Account(TEST_ACCOUNT_ID, TEST_START_AMOUNT_VALUE);
        when(accountDaoMock.getAccount(TEST_ACCOUNT_ID)).thenReturn(expectedAccount);
        assertEquals(accountServiceImpl.getAccount(TEST_ACCOUNT_ID), expectedAccount);
    }

    @Test
    @DisplayName("Testing checkBalance() method")
    void checkBalance() {
        var expectedAccount = new Account(TEST_ACCOUNT_ID, TEST_START_AMOUNT_VALUE);
        when(accountDaoMock.getAccount(TEST_ACCOUNT_ID)).thenReturn(expectedAccount);
        assertEquals(accountServiceImpl.checkBalance(TEST_ACCOUNT_ID), TEST_START_AMOUNT_VALUE);
    }

}
