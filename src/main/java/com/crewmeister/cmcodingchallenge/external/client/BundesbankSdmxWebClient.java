package com.crewmeister.cmcodingchallenge.external.client;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Component
public class BundesbankSdmxWebClient {

    private final WebClient webClient;

    public BundesbankSdmxWebClient() {
        // Increase max in-memory buffer to handle large responses safely
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(50 * 1024 * 1024)) // 50 MB
                .build();

        this.webClient = WebClient.builder()
                .baseUrl("https://api.statistiken.bundesbank.de/rest/data/BBEX3")
                .exchangeStrategies(strategies)
                .build();
    }

    /**
     * Fetch exchange rates as a Flux of DataBuffer (streaming XML).
     * Works for large files without memory overflow.
     */
    public Flux<DataBuffer> fetchExchangeRatesStream(LocalDate startDate, LocalDate endDate) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("startPeriod", startDate)
                        .queryParam("endPeriod", endDate)
                        .build())
                .accept(MediaType.APPLICATION_XML) // explicitly accept XML
                .retrieve()
                .bodyToFlux(DataBuffer.class); // stream raw XML
    }
}
