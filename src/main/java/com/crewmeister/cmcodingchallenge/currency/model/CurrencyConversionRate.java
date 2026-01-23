package com.crewmeister.cmcodingchallenge.currency.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="currency_conversion_rate",
        uniqueConstraints = @UniqueConstraint(columnNames = {"currency_code", "rate_date"}))
public class CurrencyConversionRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "currency_id")
    private Long currencyId;

    @Column(name = "currency_code",nullable = false)
    private String currencyCode;

    @Column(name = "conversion_rate",nullable = false)
    private BigDecimal conversionRate;

    @Column(name = "rate_date",nullable = false)
    private LocalDate rateDate;

}
