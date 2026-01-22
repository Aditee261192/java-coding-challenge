package com.crewmeister.cmcodingchallenge.currency.dao;

import com.crewmeister.cmcodingchallenge.currency.model.CurrencyConversionRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyConversionRateRepository extends JpaRepository<CurrencyConversionRate,Long> {
}
