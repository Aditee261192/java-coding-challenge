package com.crewmeister.cmcodingchallenge.currency.controller;

import com.crewmeister.cmcodingchallenge.currency.model.CurrencyConversionRate;
import com.crewmeister.cmcodingchallenge.currency.service.CurrencyConversionRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController()
@RequestMapping("/api/v1")
public class CurrencyController {

    private CurrencyConversionRateService currencyConversionRateService;

    @Autowired
    public CurrencyController(CurrencyConversionRateService currencyConversionRateService){
        this.currencyConversionRateService=currencyConversionRateService;
    }

    @GetMapping("/currencies")
    @Operation(summary = "Get a list of all available currencies.", description = "Get already persisted currencies.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Currencies found.")})
    public ResponseEntity<List<String>> getCurrencies() {

        return
                new ResponseEntity<List<String>>(currencyConversionRateService.getAllAvailableCurrencies()
                .orElseThrow(() -> new RuntimeException("Can not find list of all Currencies")), HttpStatus.OK);


    }


}
