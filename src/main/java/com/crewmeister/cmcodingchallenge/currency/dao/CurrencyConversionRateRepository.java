package com.crewmeister.cmcodingchallenge.currency.dao;

import com.crewmeister.cmcodingchallenge.currency.model.CurrencyConversionRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CurrencyConversionRateRepository extends JpaRepository<CurrencyConversionRate, Long> {

    @Query("SELECT DISTINCT c.currencyCode FROM CurrencyConversionRate c ")
    List<String> getAvailableCurrencies();

    List<CurrencyConversionRate> findByRateDate(LocalDate date);

    CurrencyConversionRate findByCurrencyCodeAndRateDate(String currencyCode, LocalDate date);

    boolean existsByCurrencyCode(String currencyCode);

    @Modifying
    @Transactional
    @Query(
            value = "INSERT INTO currency_conversion_rate " +
                    "(currency_code, rate_date, conversion_rate) " +
                    "VALUES (:code, :date, :rate) " +
                    "ON CONFLICT (currency_code, rate_date) DO NOTHING",
            nativeQuery = true
    )
    void insertIgnore(
            @Param("code") String currencyCode,
            @Param("date") LocalDate rateDate,
            @Param("rate") BigDecimal conversionRate
    );

}
