package com.crewmeister.cmcodingchallenge.currency.service;

import com.crewmeister.cmcodingchallenge.currency.dao.CurrencyConversionRateRepository;
import com.crewmeister.cmcodingchallenge.currency.model.CurrencyConversionRate;
import com.crewmeister.cmcodingchallenge.external.client.BundesbankSdmxWebClient;
import com.crewmeister.cmcodingchallenge.external.parser.BundesbankSdmxStaxParser;
import com.crewmeister.cmcodingchallenge.generated.model.CurrencyConversionRateResponse;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DefaultCurrencyConversionRateService implements CurrencyConversionRateService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(DefaultCurrencyConversionRateService.class);

    private final BundesbankSdmxWebClient webClient;
    private final CurrencyConversionRateRepository currencyConversionRateRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public DefaultCurrencyConversionRateService(BundesbankSdmxWebClient webClient,
                                                CurrencyConversionRateRepository currencyConversionRateRepository,
                                                ModelMapper modelMapper) {
        this.webClient = webClient;
        this.currencyConversionRateRepository = currencyConversionRateRepository;
        this.modelMapper=modelMapper;
    }


    @Override
    public Optional<List<String>> getAllAvailableCurrencies() {
        return
                Optional.of(currencyConversionRateRepository.getAvailableCurrencies());
    }

    @Override
    public Optional<List<CurrencyConversionRateResponse>> getAllAvailableConversionRates() {

        List<CurrencyConversionRateResponse> currencyConversionRateResponses =
                currencyConversionRateRepository.findAll().parallelStream()
                        .map(device ->
                                modelMapper.map(device, CurrencyConversionRateResponse.class))
                        .collect(Collectors.toList());

        return Optional.of(currencyConversionRateResponses);

    }

    @Override
    public Optional<List<CurrencyConversionRateResponse>> getAvailableRatesByDate(LocalDate date) {
        List<CurrencyConversionRateResponse> currencyConversionRateResponses =
                currencyConversionRateRepository.findByRateDate(date).parallelStream()
                        .map(device ->
                                modelMapper.map(device, CurrencyConversionRateResponse.class))
                        .collect(Collectors.toList());

        return Optional.of(currencyConversionRateResponses);
    }

    @Override
    public Optional<CurrencyConversionRateResponse> getAvailableRatesByCurrencyAndDate(String currencyCode, LocalDate date) {

        CurrencyConversionRate currencyConversionRate =
                currencyConversionRateRepository.findByCurrencyCodeAndRateDate(currencyCode,date);

        return
                Optional.of(modelMapper.map(currencyConversionRate, CurrencyConversionRateResponse.class));
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
