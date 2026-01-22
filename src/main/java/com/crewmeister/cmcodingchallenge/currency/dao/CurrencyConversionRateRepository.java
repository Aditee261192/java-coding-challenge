package com.crewmeister.cmcodingchallenge.currency.dao;

import com.crewmeister.cmcodingchallenge.currency.model.CurrencyConversionRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyConversionRateRepository extends JpaRepository<CurrencyConversionRate, Long> {

    @Query("SELECT DISTINCT c.currencyCode FROM CurrencyConversionRate c ")
    List<String> getAvailableCurrencies();

    List<CurrencyConversionRate> findByRateDate(LocalDate date);

    CurrencyConversionRate findByCurrencyCodeAndRateDate(String currencyCode, LocalDate date);

}
