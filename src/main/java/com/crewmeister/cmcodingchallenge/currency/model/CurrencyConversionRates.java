package com.crewmeister.cmcodingchallenge.currency.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="currency_conversion_rate",
        uniqueConstraints = @UniqueConstraint(columnNames = {"currency_code", "rate_date"}))
public class CurrencyConversionRates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long currencyId;

    @Column(name = "currency_code",nullable = false)
    private String currencyCode;

    @Column(name = "conversion_rate",nullable = false)
    private double conversionRate;

    @Column(name = "rate_date",nullable = false)
    private LocalDate rateDate;

}
