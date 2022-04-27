package com.optivem.kata.banking.core.usecases;

import com.optivem.kata.banking.core.domain.exceptions.ValidationMessages;
import com.optivem.kata.banking.core.usecases.withdrawfunds.WithdrawFundsResponse;
import com.optivem.kata.banking.core.usecases.withdrawfunds.WithdrawFundsUseCase;
import com.optivem.kata.banking.infra.fake.accounts.FakeBankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.optivem.kata.banking.core.common.assertions.Assertions.assertThatRepository;
import static com.optivem.kata.banking.core.common.assertions.Assertions.assertThatUseCase;
import static com.optivem.kata.banking.core.common.builders.requests.WithdrawFundsRequestBuilder.aWithdrawFundsRequest;
import static com.optivem.kata.banking.core.common.data.MethodSources.NON_POSITIVE_INTEGERS;
import static com.optivem.kata.banking.core.common.data.MethodSources.NULL_EMPTY_WHITESPACE;
import static com.optivem.kata.banking.core.common.givens.Givens.givenThatRepository;

class WithdrawFundsUseCaseTest {

    private FakeBankAccountRepository repository;
    private WithdrawFundsUseCase useCase;

    private static Stream<Arguments> should_withdraw_funds_given_valid_request() {
        return Stream.of(Arguments.of("GB10BARC20040184197751", 70, 30, 40),
                Arguments.of("GB36BMFK75394735916876", 100, 100, 0));
    }

    @BeforeEach
    void init() {
        this.repository = new FakeBankAccountRepository();
        this.useCase = new WithdrawFundsUseCase(repository);
    }

    @ParameterizedTest
    @MethodSource
    void should_withdraw_funds_given_valid_request(String accountNumber, int initialBalance, int amount, int expectedFinalBalance) {
        givenThatRepository(repository).containsBankAccount(accountNumber, initialBalance);

        var request = aWithdrawFundsRequest()
                .accountNumber(accountNumber)
                .amount(amount)
                .build();

        var expectedResponse = new WithdrawFundsResponse();
        expectedResponse.setBalance(expectedFinalBalance);

        assertThatUseCase(useCase).withRequest(request).assertResponse(expectedResponse);

        assertThatRepository(repository).containsBankAccount(accountNumber, expectedFinalBalance);
    }

    @ParameterizedTest
    @MethodSource(NULL_EMPTY_WHITESPACE)
    void should_throw_exception_given_empty_account_number(String accountNumber) {
        var request = aWithdrawFundsRequest()
                .accountNumber(accountNumber)
                .build();

        assertThatUseCase(useCase).withRequest(request).throwsValidationException(ValidationMessages.ACCOUNT_NUMBER_EMPTY);
    }

    @Test
    void should_throw_exception_given_non_existent_account_number() {
        var request = aWithdrawFundsRequest()
                .build();

        assertThatUseCase(useCase).withRequest(request).throwsValidationException(ValidationMessages.ACCOUNT_NUMBER_NOT_EXIST);
    }

    @ParameterizedTest
    @MethodSource(NON_POSITIVE_INTEGERS)
    void should_throw_exception_given_non_positive_amount(int amount) {
        var request = aWithdrawFundsRequest()
                .amount(amount)
                .build();

        assertThatUseCase(useCase).withRequest(request).throwsValidationException(ValidationMessages.NON_POSITIVE_TRANSACTION_AMOUNT);
    }

    @Test
    void should_throw_exception_given_insufficient_funds() {
        var accountNumber = "GB10BARC20040184197751";
        var balance = 140;
        var amount = 141;

        givenThatRepository(repository).containsBankAccount(accountNumber, balance);

        var request = aWithdrawFundsRequest()
                .accountNumber(accountNumber)
                .amount(amount)
                .build();

        assertThatUseCase(useCase).withRequest(request).throwsValidationException(ValidationMessages.INSUFFICIENT_FUNDS);

        assertThatRepository(repository).containsBankAccount(accountNumber, balance);
    }
}
