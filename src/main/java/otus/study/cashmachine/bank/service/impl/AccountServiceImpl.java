package otus.study.cashmachine.bank.service.impl;

import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.db.Accounts;
import otus.study.cashmachine.bank.service.AccountService;

import java.math.BigDecimal;

public class AccountServiceImpl implements AccountService {
    AccountDao accountDao;
    public AccountServiceImpl(final AccountDao accountDao) {
        this.accountDao = accountDao;
    }
    @Override
    public Account createAccount(BigDecimal amount) {
        Account newAccount = new Account(Accounts.getNexyId(), amount);
        Accounts.accounts.put(newAccount.getId(), newAccount);
        return newAccount;
    }

    @Override
    public Account getAccount(Long id) {
        return Accounts.accounts.get(id);
    }

    @Override
    public BigDecimal getMoney(Long id, BigDecimal amount) {
        Account account = Accounts.accounts.get(id);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        if (account.getAmount().subtract(amount).doubleValue() < 0) {
            throw new IllegalArgumentException("Not enough money");
        }
        account.setAmount(account.getAmount().subtract(amount));
        return account.getAmount();
    }

    @Override
    public BigDecimal putMoney(Long id, BigDecimal amount) {
        Account account = Accounts.accounts.get(id);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        account.setAmount(account.getAmount().add(amount));
        return account.getAmount();
    }

    @Override
    public BigDecimal checkBalance(Long id) {
        Account account = Accounts.accounts.get(id);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        return account.getAmount();
    }
}