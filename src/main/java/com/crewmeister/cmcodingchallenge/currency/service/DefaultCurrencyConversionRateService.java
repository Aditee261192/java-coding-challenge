package com.crewmeister.cmcodingchallenge.currency.service;

import com.crewmeister.cmcodingchallenge.currency.dao.CurrencyConversionRateRepository;
import com.crewmeister.cmcodingchallenge.currency.exception.ConversionRateNotFoundException;
import com.crewmeister.cmcodingchallenge.currency.exception.InvalidCurrencyException;
import com.crewmeister.cmcodingchallenge.currency.exception.InvalidDateException;
import com.crewmeister.cmcodingchallenge.currency.model.CurrencyConversionRate;
import com.crewmeister.cmcodingchallenge.external.client.BundesbankSdmxWebClient;
import com.crewmeister.cmcodingchallenge.external.parser.BundesbankSdmxStaxParser;
import com.crewmeister.cmcodingchallenge.generated.model.CurrencyConversionRateResponse;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
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
    public List<String> getAllAvailableCurrencies() {
        return
                currencyConversionRateRepository.getAvailableCurrencies();
    }

    @Override
    public List<CurrencyConversionRateResponse> getAllAvailableConversionRates() {

        return
                currencyConversionRateRepository.findAll().stream()
                        .map(rate ->
                                modelMapper.map(rate, CurrencyConversionRateResponse.class))
                        .collect(Collectors.toList());

    }

    @Override
    public List<CurrencyConversionRateResponse> getAvailableRatesByDate(String date) {

        LocalDate inputDate=validateDateFormat(date);

        return
                currencyConversionRateRepository.findByRateDate(inputDate).stream()
                        .map(device ->
                                modelMapper.map(device, CurrencyConversionRateResponse.class))
                        .collect(Collectors.toList());

    }

    @Override
    public CurrencyConversionRateResponse getAvailableRatesByCurrencyAndDate(String currencyCode, String date) {

        LocalDate inputDate=validateDateFormat(date);

        validateCurrencyCode(currencyCode);

        return Optional.ofNullable(
                        currencyConversionRateRepository.findByCurrencyCodeAndRateDate(currencyCode, inputDate)
                )
                .map(rate -> modelMapper.map(rate, CurrencyConversionRateResponse.class))
                .orElseThrow(() -> new ConversionRateNotFoundException(
                        "Conversion rate not Found for Currency "+currencyCode +"and Date "+date
                ));
    }

    private LocalDate validateDateFormat(String date){

        if (date == null || date.isEmpty()){
            throw new InvalidDateException("Invalid Date Format.Expected format: yyyy-MM-dd");
        }
        return LocalDate.parse(date);

    }

    private void validateCurrencyCode(String currencyCode){
        if (currencyCode == null || currencyCode.isEmpty()){
            throw new InvalidCurrencyException("Currency Code Cannot be Empty.");
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void fetchAndParseCurrenciesStreamed() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(10);

        Flux<CurrencyConversionRate> rateFlux = webClient.fetchCurrencyRates(startDate, endDate);

        rateFlux
                .doOnNext(rate -> currencyConversionRateRepository.save(rate))
                .doOnError(e -> LOGGER.error("Error fetching or parsing currency rates", e))
                .doOnComplete(() -> LOGGER.info("Finished fetching and parsing currency rates"))
                .subscribe();
    }

}
