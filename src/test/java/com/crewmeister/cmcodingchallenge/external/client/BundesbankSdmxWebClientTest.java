package com.crewmeister.cmcodingchallenge.external.client;

import com.crewmeister.cmcodingchallenge.currency.model.CurrencyConversionRate;
import com.crewmeister.cmcodingchallenge.external.parser.BundesbankSdmxStaxParser;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public class BundesbankSdmxWebClientTest {

    @Test
    void should_throw_exception_for_invalid_xml() {
        String invalidXml = "<Series><ObsValue></Series>"; // missing value
        ByteArrayInputStream inputStream = new ByteArrayInputStream(invalidXml.getBytes(StandardCharsets.UTF_8));

        Flux<CurrencyConversionRate> flux = BundesbankSdmxStaxParser.extractCurrencyRatesStream(
                inputStream,
                LocalDate.MIN,
                LocalDate.MAX
        );

        StepVerifier.create(flux)
                .expectError(IllegalArgumentException.class) // now parser throws this
                .verify();
    }

}
