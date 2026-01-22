package com.crewmeister.cmcodingchallenge.external.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient bundesbankWebClient() {

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer ->
                        configurer.defaultCodecs()
                                .maxInMemorySize(50 * 1024 * 1024) // 50 MB
                )
                .build();

        return WebClient.builder()
                .baseUrl("https://api.statistiken.bundesbank.de/rest")
                .exchangeStrategies(strategies)
                .build();
    }
}