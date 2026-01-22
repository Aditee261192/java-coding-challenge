package com.crewmeister.cmcodingchallenge.currency;


import com.crewmeister.cmcodingchallenge.currency.exception.ConversionRateNotFoundException;
import com.crewmeister.cmcodingchallenge.currency.exception.CurrencyNotFoundException;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController()
@RequestMapping("/api/v1")
@Tag(name = "Currency Exchange Rate API", description = "Exchange rate for EUR ")
public class CurrencyController {

    private final CurrencyConversionRateService currencyConversionRateService;

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
                        .orElseThrow(() -> new CurrencyNotFoundException("Can not find list of all Currencies.")), HttpStatus.OK);
    }

    @GetMapping("/conversion-rates")
    @Operation(summary = "Get all EUR-FX conversion rates at all available dates .", description = "Get already persisted conversion rates.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Conversion rate found")})
    public ResponseEntity<List<CurrencyConversionRateResponse>> getConversionRates() {

        return
                new ResponseEntity<>(currencyConversionRateService.getAllAvailableConversionRates()
                        .orElseThrow(() -> new ConversionRateNotFoundException("Can not find list of conversion rates")), HttpStatus.OK);
    }

    @GetMapping("/conversion-rates/{date}")
    @Operation(summary = "Get all EUR-FX conversion rates for given date .", description = "Get already persisted conversion rates for given day.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Conversion rate found")})
    public ResponseEntity<List<CurrencyConversionRateResponse>> getConversionRatesByDate(@PathVariable String date) {

        LocalDate inputDate=LocalDate.parse(date);
        return
                new ResponseEntity<>(currencyConversionRateService.getAvailableRatesByDate(inputDate)
                        .orElseThrow(() -> new ConversionRateNotFoundException("Can not find list of conversion rates")), HttpStatus.OK);
    }

    @GetMapping("/conversion-rates/{currencyCode}/{date}")
    @Operation(summary = "Get EUR-FX conversion rates for given currency and  date .", description = "Get already persisted conversion rates for given currency and day.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Conversion rate found")})
    public ResponseEntity<CurrencyConversionRateResponse> getConversionRatesByCurrencyAndDate(@PathVariable String currencyCode,@PathVariable String date) {

        LocalDate inputDate=LocalDate.parse(date);
        return
                new ResponseEntity<>(currencyConversionRateService.getAvailableRatesByCurrencyAndDate(currencyCode,inputDate)
                        .orElseThrow(() -> new ConversionRateNotFoundException("Can not find conversion rate")), HttpStatus.OK);
    }


}
