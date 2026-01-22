package com.crewmeister.cmcodingchallenge.currency.service;

import com.crewmeister.cmcodingchallenge.currency.model.CurrencyConversionRate;
import com.crewmeister.cmcodingchallenge.generated.model.CurrencyConversionRateResponse;

import java.util.List;
import java.util.Optional;

public interface CurrencyConversionRateService {

    Optional<List<String>> getAllAvailableCurrencies();

    Optional<List<CurrencyConversionRateResponse>> getAllAvailableConversionRates();
}
