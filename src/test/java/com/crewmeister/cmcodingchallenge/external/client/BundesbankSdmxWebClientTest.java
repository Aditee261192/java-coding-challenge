package com.crewmeister.cmcodingchallenge.external.client;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BundesbankSdmxWebClientTest {

    @Test
    void should_return_flux_of_databuffer() {
        BundesbankSdmxWebClient client = new BundesbankSdmxWebClient();

        Flux<DataBuffer> result =
                client.fetchExchangeRatesStream(
                        LocalDate.now().minusDays(1),
                        LocalDate.now()
                );

        assertNotNull(result);
    }
}
