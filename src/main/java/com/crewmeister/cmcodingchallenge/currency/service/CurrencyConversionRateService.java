package com.crewmeister.cmcodingchallenge.currency.service;

import com.crewmeister.cmcodingchallenge.currency.dao.CurrencyConversionRateRepository;
import com.crewmeister.cmcodingchallenge.currency.model.CurrencyConversionRate;
import com.crewmeister.cmcodingchallenge.external.client.BundesbankSdmxWebClient;
import com.crewmeister.cmcodingchallenge.external.parser.BundesbankSdmxStaxParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

@Service
public class CurrencyConversionRateService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(CurrencyConversionRateService.class);

    private final BundesbankSdmxWebClient webClient;
    private final CurrencyConversionRateRepository currencyConversionRateRepository;

    public CurrencyConversionRateService(BundesbankSdmxWebClient webClient,
                                         CurrencyConversionRateRepository currencyConversionRateRepository ) {
        this.webClient = webClient;
        this.currencyConversionRateRepository=currencyConversionRateRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void fetchAndParseCurrencies() {

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(5);

        try {
            InputStream inputStream = DataBufferUtils.join(
                            webClient.fetchExchangeRatesStream(startDate, endDate))
                    .map(dataBuffer -> dataBuffer.asInputStream(true))
                    .block();

            if (inputStream == null) {
                LOGGER.warn("No data received from Bundesbank");
                return;
            }

            List<CurrencyConversionRate> rates =
                    BundesbankSdmxStaxParser.extractCurrencyRates(
                            inputStream, startDate, endDate);

            LOGGER.info("Parsed {} unique currency rates", rates.size());

             currencyConversionRateRepository.saveAll(rates); // ← ready for persistence

        } catch (Exception e) {
            LOGGER.error("Failed to fetch or parse currency rates", e);
        }
    }
}
