package com.crewmeister.cmcodingchallenge.currency.controller;

import com.crewmeister.cmcodingchallenge.currency.service.CurrencyConversionRateService;
import com.crewmeister.cmcodingchallenge.generated.model.CurrencyConversionRateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyConversionRateService currencyConversionRateService;

    @BeforeEach
    void setupMocks() {
        CurrencyConversionRateResponse mockResponse = new CurrencyConversionRateResponse();
        mockResponse.setCurrencyCode("INR");
        mockResponse.setRateDate(LocalDate.of(2026, 1, 22));
        mockResponse.setConversionRate(82.50);

        when(currencyConversionRateService.getAvailableRatesByCurrencyAndDate(anyString(), anyString()))
                .thenReturn(mockResponse);

        when(currencyConversionRateService.getAllAvailableConversionRates()).thenReturn(Collections.emptyList());
        when(currencyConversionRateService.getAvailableRatesByDate(anyString())).thenReturn(Collections.emptyList());
        when(currencyConversionRateService.getAllAvailableCurrencies()).thenReturn(Collections.emptyList());
    }


    @Test
    void should_get_list_of_currencies() throws Exception {

        mockMvc.perform(get("/api/v1/currencies")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void should_get_list_of_conversion_rates() throws Exception {

        mockMvc.perform(get("/api/v1/conversion-rates")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void should_get_list_of_conversion_rates_by_date() throws Exception {

        mockMvc.perform(get("/api/v1/conversion-rates/date/{date}", "2026-01-22")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void should_get_conversion_rates_by_currency_and_date() throws Exception {

        mockMvc.perform(get("/api/v1/conversion-rates/currency/{currencyCode}/date/{date}", "INR", "2026-01-22"))
                .andExpect(status().isOk());
    }
}
