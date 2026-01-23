package com.crewmeister.cmcodingchallenge.currency.controller;

import com.crewmeister.cmcodingchallenge.currency.service.CurrencyConversionRateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyConversionRateService currencyConversionRateService;

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
    void should_get_list_of_conversion_rates_by_currency_and_date() throws Exception {

        mockMvc.perform(get("/api/v1/conversion-rates/currency/{currencyCode}/date/{date}", "INR", "2026-01-22"))
                .andExpect(status().isOk());
    }
}
