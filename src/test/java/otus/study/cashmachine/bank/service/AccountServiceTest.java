package otus.study.cashmachine.bank.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;


import java.math.BigDecimal;


import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;


public class AccountServiceTest {

    @Mock
    AccountDao accountDao;
    @Mock
    AccountServiceTest accountService;
    @Mock
    AccountServiceImpl accountServiceImpl;

    @BeforeEach
    void unit() {
    accountDao = mock(AccountDao.class);
        MockitoAnnotations.openMocks(this);
    }
    @BeforeEach
    void setUp() {
        accountServiceImpl = new AccountServiceImpl(accountDao);
    }
    @Test
    void createAccountMock() {
// @TODO test account creation with mock and ArgumentMatcher
        // Arrange
        BigDecimal amount = new BigDecimal("100");
        Account newAccount = new Account(1, amount);
        when(accountDao.saveAccount(argThat(account -> account.getAmount().compareTo(amount) == 0))).thenReturn(newAccount);
    }

    @Test
    void createAccountCaptor() {
//  @TODO test account creation with ArgumentCaptor
        BigDecimal amount = new BigDecimal("100");
        Account expectedAccount = new Account(1, amount);
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        when(accountDao.saveAccount(accountCaptor.capture())).thenReturn(expectedAccount);
    }

    @Test
    void addSum() {
        Account account = new Account(1L, BigDecimal.ZERO);
        when(accountDao.getAccount(1L)).thenReturn(account);
        BigDecimal amountToAdd = new BigDecimal("100");
        BigDecimal expectedBalance = BigDecimal.ZERO.add(amountToAdd);
        BigDecimal actualBalance = accountServiceImpl.putMoney(1L, amountToAdd);
        verify(accountDao).getAccount(1L);
        assertEquals(expectedBalance, actualBalance);
    }


    @Test
    void getSum() {
        Long accountId = 1L;
        BigDecimal initialAmount = new BigDecimal("100.00");
        BigDecimal withdrawalAmount = new BigDecimal("50.00");
        BigDecimal expectedRemainingAmount = new BigDecimal("50.00");

        Account account = new Account(accountId, initialAmount);
        when(accountDao.getAccount(accountId)).thenReturn(account);

        AccountServiceImpl AccountServiceImpl = new AccountServiceImpl(accountDao);
        when(accountDao.getAccount(accountId)).thenReturn(new Account(accountId, initialAmount));
        BigDecimal actualRemainingAmount = AccountServiceImpl.getMoney(accountId, withdrawalAmount);

        assertEquals(expectedRemainingAmount, actualRemainingAmount);
        verify(accountDao, times(1)).getAccount(accountId);
    }

    @Test
    void getAccount() {
        AccountService accountService = new AccountServiceImpl(accountDao);

        // Определяем ожидаемый результат
        Long accountId = 123L;
        BigDecimal BigDecimal = new BigDecimal("100");
        Account expectedAccount = new Account(accountId,  BigDecimal);
        when(accountDao.getAccount(accountId)).thenReturn(expectedAccount);
        Account actualAccount = accountService.getAccount(accountId);
        assertEquals(expectedAccount, actualAccount);
        verify(accountDao).getAccount(accountId);


    }

    @Test
    void testCheckBalance() {
        Long id = 1L;
        BigDecimal expectedBalance = new BigDecimal("1000.00");
        Account account = new Account(id, new BigDecimal("100.00"));
        account.setAmount(expectedBalance);
        when(accountDao.getAccount(id)).thenReturn(account);
        AccountServiceImpl AccountServiceImpl = new AccountServiceImpl(accountDao);
        BigDecimal actualBalance = AccountServiceImpl.checkBalance(id);
        assertEquals(expectedBalance, actualBalance);
        verify(accountDao).getAccount(id);
    }
}
