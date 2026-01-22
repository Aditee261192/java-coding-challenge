package com.crewmeister.cmcodingchallenge.currency;


import com.crewmeister.cmcodingchallenge.currency.service.CurrencyConversionRateService;
import com.crewmeister.cmcodingchallenge.generated.model.CurrencyConversionRateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("/api/v1")
@Tag(name = "Currency Exchange Rate API", description = "Exchange rate for EUR ")
public class CurrencyController {

    private CurrencyConversionRateService currencyConversionRateService;

    @Autowired
    public CurrencyController(CurrencyConversionRateService currencyConversionRateService) {
        this.currencyConversionRateService = currencyConversionRateService;
    }

    @GetMapping("/currencies")
    @Operation(summary = "Get a list of all available currencies.", description = "Get already persisted currencies.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Currencies found.")})
    public ResponseEntity<List<String>> getCurrencies() {

        return
                new ResponseEntity<>(currencyConversionRateService.getAllAvailableCurrencies()
                        .orElseThrow(() -> new RuntimeException("Can not find list of all Currencies.")), HttpStatus.OK);


    }

    @GetMapping("/conversion-rates")
    @Operation(summary = "Get all EUR-FX conversion rates at all available dates .", description = "Get already persisted conversion rates.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Conversion rate found")})
    public ResponseEntity<List<CurrencyConversionRateResponse>> getConversionRates() {

        return
                new ResponseEntity<>(currencyConversionRateService.getAllAvailableConversionRates()
                        .orElseThrow(() -> new RuntimeException("Can not find list of conversion rates")), HttpStatus.OK);


    }



}
