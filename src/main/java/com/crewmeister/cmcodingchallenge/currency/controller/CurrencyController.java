package com.crewmeister.cmcodingchallenge.currency.controller;

import com.crewmeister.cmcodingchallenge.currency.model.CurrencyConversionRate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController()
@RequestMapping("/api/v1")
public class CurrencyController {

    @GetMapping("/currencies")
    public ResponseEntity<ArrayList<CurrencyConversionRate>> getCurrencies() {
        ArrayList<CurrencyConversionRate> currencyConversionRates = new ArrayList<CurrencyConversionRate>();

        return new ResponseEntity<ArrayList<CurrencyConversionRate>>(currencyConversionRates, HttpStatus.OK);
    }
}
