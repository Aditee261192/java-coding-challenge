package com.crewmeister.cmcodingchallenge.external.client;

import com.crewmeister.cmcodingchallenge.currency.model.CurrencyConversionRate;
import com.crewmeister.cmcodingchallenge.external.parser.BundesbankSdmxStaxParser;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.time.LocalDate;

@Component
public class BundesbankSdmxWebClient {

    private final WebClient webClient;

    public BundesbankSdmxWebClient(WebClient bundesbankWebClient) {
        this.webClient = bundesbankWebClient;
    }

    public Flux<CurrencyConversionRate> fetchCurrencyRates(LocalDate startDate, LocalDate endDate) {
        return Flux.create(sink -> {
            try {
                // Piped streams to stream data directly to StAX parser
                PipedOutputStream pos = new PipedOutputStream();
                PipedInputStream pis = new PipedInputStream(pos);

                // Parsing thread
                new Thread(() -> {
                    try {
                        BundesbankSdmxStaxParser.extractCurrencyRatesStream(pis, startDate, endDate)
                                .doOnNext(sink::next)
                                .doOnComplete(sink::complete)
                                .doOnError(sink::error)
                                .subscribe();
                    } catch (Exception e) {
                        sink.error(e);
                    }
                }).start();

                // Stream response into PipedOutputStream
                webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/data/BBEX3")
                                .queryParam("startPeriod", startDate)
                                .queryParam("endPeriod", endDate)
                                .build())
                        .retrieve()
                        .bodyToFlux(DataBuffer.class)
                        .doOnNext(dataBuffer -> {
                            try {
                                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(bytes);
                                DataBufferUtils.release(dataBuffer);
                                pos.write(bytes);  // write chunk to parser
                            } catch (IOException e) {
                                sink.error(e);
                            }
                        })
                        .doOnComplete(() -> {
                            try {
                                pos.close();  // signal end of stream
                            } catch (IOException ignored) {}
                        })
                        .doOnError(sink::error)
                        .subscribe();

            } catch (IOException e) {
                sink.error(e);
            }
        });
    }
}
