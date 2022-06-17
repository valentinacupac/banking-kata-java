package com.optivem.kata.banking.infra.fake.time;

import com.optivem.kata.banking.core.domain.time.DateTimeService;
import com.optivem.kata.banking.infra.fake.base.FakeGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FakeDateTimeService implements DateTimeService {

    private final FakeGenerator<LocalDateTime> dateGenerator;

    public FakeDateTimeService() {
        this.dateGenerator = new FakeGenerator<>();
    }

    public void add(LocalDateTime dateTime) {
        dateGenerator.add(dateTime);
    }

    @Override
    public LocalDateTime getCurrent() {
        return dateGenerator.next();
    }
}