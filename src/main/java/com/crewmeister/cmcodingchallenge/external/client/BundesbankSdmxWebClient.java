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

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(50 * 1024 * 1024))
                .build();

        this.webClient = WebClient.builder()
                .baseUrl("https://api.statistiken.bundesbank.de/rest/data/BBEX3")
                .exchangeStrategies(strategies)
                .build();
    }

    public Flux<DataBuffer> fetchExchangeRatesStream(LocalDate startDate, LocalDate endDate) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("startPeriod", startDate)
                        .queryParam("endPeriod", endDate)
                        .build())
                .accept(MediaType.APPLICATION_XML)
                .retrieve()
                .bodyToFlux(DataBuffer.class);
    }
}
