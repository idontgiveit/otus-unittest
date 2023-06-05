package otus.study.cashmachine.bank.service;

import org.mockito.ArgumentMatcher;
import otus.study.cashmachine.bank.data.Account;

public class AccountMatcher implements ArgumentMatcher<otus.study.cashmachine.bank.data.Account> {
    Account account;

    public AccountMatcher(Account account) {
        this.account = account;
    }

    @Override
    public boolean matches(Account argument) {
        return  account.getId()==argument.getId()&&
                account!=null;
    }
}
