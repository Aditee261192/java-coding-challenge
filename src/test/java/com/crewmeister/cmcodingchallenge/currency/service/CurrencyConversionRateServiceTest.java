package com.crewmeister.cmcodingchallenge.currency.service;

import com.crewmeister.cmcodingchallenge.currency.dao.CurrencyConversionRateRepository;
import com.crewmeister.cmcodingchallenge.currency.model.CurrencyConversionRate;
import com.crewmeister.cmcodingchallenge.external.client.BundesbankSdmxWebClient;
import com.crewmeister.cmcodingchallenge.generated.model.CurrencyConversionRateResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CurrencyConversionRateServiceTest {

    @Mock
    private CurrencyConversionRateRepository currencyConversionRateRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private BundesbankSdmxWebClient webClient;

    @Test
    void should_return_list_of_currencies() {
        List<String> currencyList = List.of("USD", "INR", "EUR");

        when(currencyConversionRateRepository.getAvailableCurrencies()).thenReturn(currencyList);

        DefaultCurrencyConversionRateService service =
                new DefaultCurrencyConversionRateService(webClient, currencyConversionRateRepository, modelMapper);

        List<String> result = service.getAllAvailableCurrencies();

        assertEquals(currencyList.size(), result.size());
        assertEquals(currencyList.get(0), result.get(0));
        assertEquals(currencyList.get(1), result.get(1));
    }

    @Test
    void should_return_list_of_currencyConversionRates() {
        Object[][] data = {
                {"USD", 1.0},
                {"EUR", 0.91},
                {"INR", 82.5}
        };
        LocalDate date = LocalDate.of(2026, 1, 22);

        List<CurrencyConversionRate> entities = createCurrencyEntities(date, data);
        List<CurrencyConversionRateResponse> dtos = createCurrencyResponses(date, data);

        when(currencyConversionRateRepository.findAll()).thenReturn(entities);
        mockModelMapperForList(entities, dtos);

        DefaultCurrencyConversionRateService service =
                new DefaultCurrencyConversionRateService(webClient, currencyConversionRateRepository, modelMapper);

        List<CurrencyConversionRateResponse> result = service.getAllAvailableConversionRates();

        assertEquals(entities.size(), result.size());
        assertEquals(entities.get(0).getCurrencyCode(), result.get(0).getCurrencyCode());
        assertEquals(entities.get(1).getCurrencyCode(), result.get(1).getCurrencyCode());
    }

    @Test
    void should_return_list_of_currencyConversionRatesByDate() {
        Object[][] data = {
                {"USD", 1.0},
                {"EUR", 0.91}
        };
        LocalDate date = LocalDate.of(2026, 1, 22);

        List<CurrencyConversionRate> entities = createCurrencyEntities(date, data);
        List<CurrencyConversionRateResponse> dtos = createCurrencyResponses(date, data);

        when(currencyConversionRateRepository.findByRateDate(Mockito.any())).thenReturn(entities);
        mockModelMapperForList(entities, dtos);

        DefaultCurrencyConversionRateService service =
                new DefaultCurrencyConversionRateService(webClient, currencyConversionRateRepository, modelMapper);

        List<CurrencyConversionRateResponse> result = service.getAvailableRatesByDate("2026-01-22");

        assertEquals(entities.size(), result.size());
        assertEquals(entities.get(0).getCurrencyCode(), result.get(0).getCurrencyCode());
        assertEquals(entities.get(1).getCurrencyCode(), result.get(1).getCurrencyCode());
    }

    @Test
    void should_return_list_of_currencyConversionRateByCurrencyAndDate() {
        LocalDate date = LocalDate.of(2026, 1, 22);

        CurrencyConversionRate entity = CurrencyConversionRate.builder()
                .currencyCode("USD")
                .conversionRate(BigDecimal.valueOf(1.0))
                .rateDate(date)
                .build();

        CurrencyConversionRateResponse dto = new CurrencyConversionRateResponse()
                .currencyCode("USD")
                .conversionRate(1.0)
                .rateDate(date);

        when(currencyConversionRateRepository.findByCurrencyCodeAndRateDate(Mockito.any(), Mockito.any()))
                .thenReturn(entity);
        when(modelMapper.map(entity, CurrencyConversionRateResponse.class)).thenReturn(dto);

        DefaultCurrencyConversionRateService service =
                new DefaultCurrencyConversionRateService(webClient, currencyConversionRateRepository, modelMapper);

        CurrencyConversionRateResponse result =
                service.getAvailableRatesByCurrencyAndDate("USD", "2026-01-22");

        assertEquals(entity.getCurrencyCode(), result.getCurrencyCode());
        assertEquals(entity.getConversionRate(), BigDecimal.valueOf(result.getConversionRate()));
    }



    private List<CurrencyConversionRate> createCurrencyEntities(LocalDate date, Object[][] data) {
        return IntStream.range(0, data.length)
                .mapToObj(i -> CurrencyConversionRate.builder()
                        .currencyCode((String) data[i][0])
                        .conversionRate(BigDecimal.valueOf((Double) data[i][1]))
                        .rateDate(date)
                        .build())
                .collect(Collectors.toList());
    }

    private List<CurrencyConversionRateResponse> createCurrencyResponses(LocalDate date, Object[][] data) {
        return IntStream.range(0, data.length)
                .mapToObj(i -> new CurrencyConversionRateResponse()
                        .currencyCode((String) data[i][0])
                        .conversionRate((Double) data[i][1])
                        .rateDate(date))
                .collect(Collectors.toList());
    }

    private void mockModelMapperForList(List<CurrencyConversionRate> entities, List<CurrencyConversionRateResponse> dtos) {
        IntStream.range(0, entities.size())
                .forEach(i -> when(modelMapper.map(entities.get(i), CurrencyConversionRateResponse.class))
                        .thenReturn(dtos.get(i)));
    }
}
