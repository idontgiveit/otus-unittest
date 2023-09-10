package otus.study.cashmachine.bank.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    private static final BigDecimal AMOUNT = BigDecimal.TEN;
    private static final Long ACCOUNT_ID = 0l;
    private Account defaultAccount;

    @Mock
    private AccountDao accountDao;

    @InjectMocks
    private AccountServiceImpl accountServiceImpl;

    @BeforeEach
    void init() {
        defaultAccount = new Account(ACCOUNT_ID, AMOUNT);
    }

    @Test
    void createAccountMock() {
        when(accountDao.saveAccount(any())).thenReturn(defaultAccount);

        Account result = accountServiceImpl.createAccount(AMOUNT);

        assertEquals(defaultAccount, result);
        verify(accountDao, only()).saveAccount(argThat(argument -> argument.getAmount().compareTo(AMOUNT) == 0));
    }

    @Test
    void createAccountCaptor() {
        when(accountDao.saveAccount(any())).thenReturn(defaultAccount);

        Account result = accountServiceImpl.createAccount(AMOUNT);

        ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountDao).saveAccount(accountArgumentCaptor.capture());
        assertEquals(result, accountArgumentCaptor.getValue());
    }

    @Test
    void putMoney() {
        when(accountDao.getAccount(any())).thenReturn(defaultAccount);
        BigDecimal complement = BigDecimal.ONE;
        BigDecimal expectedResult = defaultAccount.getAmount().add(complement);

        BigDecimal result = accountServiceImpl.putMoney(ACCOUNT_ID, complement);

        assertEquals(expectedResult, result);
    }

    @Test
    void getMoney() {
        when(accountDao.getAccount(any())).thenReturn(defaultAccount);
        BigDecimal amountToSubtract = BigDecimal.ONE;
        BigDecimal expectedResult = AMOUNT.subtract(amountToSubtract);

        BigDecimal result = accountServiceImpl.getMoney(ACCOUNT_ID, amountToSubtract);

        assertEquals(result, expectedResult);
    }

    @Test
    void getAccount() {
        when(accountDao.getAccount(any())).thenReturn(defaultAccount);

        Account result = accountServiceImpl.getAccount(ACCOUNT_ID);

        assertEquals(defaultAccount, result);
    }

    @Test
    void checkBalance() {
        when(accountDao.getAccount(any())).thenReturn(defaultAccount);

        BigDecimal result = accountServiceImpl.checkBalance(ACCOUNT_ID);

        assertEquals(AMOUNT, result);
    }
}
