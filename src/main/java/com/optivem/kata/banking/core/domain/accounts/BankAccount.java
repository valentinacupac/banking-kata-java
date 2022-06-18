package com.optivem.kata.banking.core.domain.accounts;

import com.optivem.kata.banking.core.domain.exceptions.ValidationException;
import com.optivem.kata.banking.core.domain.exceptions.ValidationMessages;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.optivem.kata.banking.core.domain.common.Guard.guard;

@EqualsAndHashCode
public class BankAccount {
    private final AccountNumber accountNumber;
    private final AccountHolderName accountHolderName;

    private final LocalDate openingDate;
    private Balance balance;

    public BankAccount(AccountNumber accountNumber, AccountHolderName accountHolderName, LocalDate openingDate, Balance balance) {
        guard(accountNumber).againstNull(ValidationMessages.ACCOUNT_NUMBER_EMPTY);
        guard(accountHolderName).againstNull(ValidationMessages.ACCOUNT_HOLDER_NAME_EMPTY);
        guard(openingDate).againstNull(ValidationMessages.OPENING_DATE_EMPTY);
        guard(balance).againstNull(ValidationMessages.BALANCE_EMPTY);

        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.openingDate = openingDate;
        this.balance = balance;
    }

    public BankAccount(BankAccount other) {
        this(other.getAccountNumber(), other.getAccountHolderName(), other.getOpeningDate(), other.getBalance());
    }

    public AccountNumber getAccountNumber() {
        return accountNumber;
    }

    public AccountHolderName getAccountHolderName() {
        return accountHolderName;
    }

    public LocalDate getOpeningDate() { return openingDate; }

    public Balance getBalance() {
        return balance;
    }

    public void deposit(TransactionAmount amount) {
        balance = balance.add(amount);
    }

    public void withdraw(TransactionAmount amount) {
        if (amount.greaterThan(balance)) {
            throw new ValidationException(ValidationMessages.INSUFFICIENT_FUNDS);
        }

        balance = balance.subtract(amount);
    }
}
