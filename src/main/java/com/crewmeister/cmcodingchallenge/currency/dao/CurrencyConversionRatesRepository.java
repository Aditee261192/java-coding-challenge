package com.crewmeister.cmcodingchallenge.currency.dao;

import com.crewmeister.cmcodingchallenge.currency.model.CurrencyConversionRates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyConversionRatesRepository extends JpaRepository<CurrencyConversionRates,Long> {
}
