package otus.study.cashmachine.bank.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountServiceTest {

    AccountService accountService;
    Account testAccount;
    AccountDao accountDao;
    @BeforeEach
    void init() {
        accountDao = new AccountDao();
        accountService = new AccountServiceImpl(accountDao);
        testAccount = accountService.createAccount(new BigDecimal(10000));
    }

    @Test
    void createAccount() {
        Account newAccount = accountService.createAccount(new BigDecimal(10000));
        assertEquals(0, new BigDecimal(10000).compareTo(newAccount.getAmount()));
    }

    @Test
    void addSum() {
        BigDecimal sum = testAccount.getAmount();
        accountService.putMoney(testAccount.getId(), new BigDecimal(100));

        BigDecimal bigDecimal = sum.add(new BigDecimal(100));
        assertEquals(0, bigDecimal.compareTo(accountService.checkBalance(testAccount.getId())));
    }

    @Test
    void getSum() {
        BigDecimal sum = testAccount.getAmount();
        BigDecimal newSum = accountService.getMoney(testAccount.getId(), new BigDecimal(100));

        BigDecimal expectedSum = sum.subtract(new BigDecimal(100));
        assertEquals(0, expectedSum.compareTo(accountService.checkBalance(testAccount.getId())));
    }

    @Test
    void getAccount() {
        assertEquals(testAccount, accountService.getAccount(testAccount.getId()));
    }

    @Test
    void checkBalance() {
        assertEquals(0, testAccount.getAmount()
                .compareTo(accountService.checkBalance(testAccount.getId())));
    }
}
