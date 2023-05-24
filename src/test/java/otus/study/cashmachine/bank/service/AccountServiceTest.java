package otus.study.cashmachine.bank.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;
import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    AccountDao accountDaoMock;

    @Captor
    private ArgumentCaptor<Account> accountCaptor;

    AccountServiceImpl accountServiceImpl;

    @Test
    void createAccountMock() {
// @TODO test account creation with mock and ArgumentMatcher

        Account expectedAccount = new Account( 1, BigDecimal.valueOf(5000));
        Account account = new Account( 1, BigDecimal.valueOf(5000));

        when(accountDaoMock.saveAccount(account)).thenReturn(account);

        assertEquals( expectedAccount, accountDaoMock.saveAccount(account));



    }

    @Test
    void createAccountCaptor() {
//  @TODO test account creation with ArgumentCaptor

        Account account = new Account( 1, BigDecimal.valueOf(5000));

        when(accountDaoMock.saveAccount(accountCaptor.capture())).thenReturn(account);
        accountDaoMock.saveAccount(account);

        verify(accountDaoMock).saveAccount(accountCaptor.capture());
        assertEquals( BigDecimal.valueOf(5000), accountCaptor.getValue().getAmount());

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
