package com.crewmeister.cmcodingchallenge.currency.service;

import com.crewmeister.cmcodingchallenge.generated.model.CurrencyConversionRateResponse;

import java.util.List;

public interface CurrencyConversionRateService {

    List<String> getAllAvailableCurrencies();

    List<CurrencyConversionRateResponse> getAllAvailableConversionRates();

    List<CurrencyConversionRateResponse> getAvailableRatesByDate(String date);

    CurrencyConversionRateResponse getAvailableRatesByCurrencyAndDate(String currencyCode, String date);
}
