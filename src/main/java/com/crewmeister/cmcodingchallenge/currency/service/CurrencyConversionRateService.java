package com.crewmeister.cmcodingchallenge.currency.service;

import java.util.List;
import java.util.Optional;

public interface CurrencyConversionRateService {

    Optional<List<String>> getAllAvailableCurrencies();
}
