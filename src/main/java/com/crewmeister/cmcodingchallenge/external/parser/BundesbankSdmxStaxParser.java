package com.crewmeister.cmcodingchallenge.external.parser;

import com.crewmeister.cmcodingchallenge.currency.model.CurrencyConversionRate;
import reactor.core.publisher.Flux;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BundesbankSdmxStaxParser {

    /**
     * Streaming SDMX parser with robust InputStream handling.
     */
    public static Flux<CurrencyConversionRate> extractCurrencyRatesStream(
            InputStream inputStream,
            LocalDate startDate,
            LocalDate endDate) {

        return Flux.create(emitter -> {
            // Wrap input stream to prevent mid-attribute EOF errors
            try (BufferedInputStream bufferedStream = new BufferedInputStream(inputStream)) {

                XMLInputFactory factory = XMLInputFactory.newInstance();
                factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
                factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);

                XMLStreamReader reader = factory.createXMLStreamReader(bufferedStream);

                Map<String, Set<LocalDate>> seenRates = new HashMap<>();
                String currentCurrency = null;
                LocalDate currentDate = null;
                BigDecimal currentRate = null;
                boolean inSeries = false;

                while (reader.hasNext()) {
                    int event = reader.next();

                    if (event == XMLStreamConstants.START_ELEMENT) {
                        String localName = reader.getLocalName();

                        if ("Series".equals(localName)) {
                            inSeries = true;
                            currentCurrency = null;
                        }

                        if (inSeries && "Value".equals(localName)) {
                            String id = reader.getAttributeValue(null, "id");
                            String value = reader.getAttributeValue(null, "value");

                            if ("BBK_ID".equals(id) && value != null) {
                                String[] parts = value.split("\\.");
                                currentCurrency = parts.length >= 3 ? parts[2] : null;
                            }
                        }

                        if (inSeries && "ObsDimension".equals(localName) && currentCurrency != null) {
                            String dateStr = reader.getAttributeValue(null, "value");

                            if (dateStr != null && dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                                LocalDate parsedDate = LocalDate.parse(dateStr);
                                currentDate = (!parsedDate.isBefore(startDate) && !parsedDate.isAfter(endDate))
                                        ? parsedDate
                                        : null;
                            } else {
                                currentDate = null;
                            }
                        }

                        if (inSeries && "ObsValue".equals(localName)
                                && currentCurrency != null
                                && currentDate != null) {

                            String rateStr = reader.getAttributeValue(null, "value");

                            if (rateStr != null && !rateStr.isEmpty()) {
                                try {
                                    currentRate = new BigDecimal(rateStr);
                                } catch (NumberFormatException ex) {
                                    emitter.error(new IllegalArgumentException("Invalid rate: " + rateStr, ex));
                                    return;
                                }

                                Set<LocalDate> seenDates =
                                        seenRates.computeIfAbsent(currentCurrency, k -> new HashSet<>());

                                if (!seenDates.contains(currentDate)) {
                                    CurrencyConversionRate rate = CurrencyConversionRate.builder()
                                            .currencyCode(currentCurrency)
                                            .rateDate(currentDate)
                                            .conversionRate(currentRate)
                                            .build();

                                    emitter.next(rate);
                                    seenDates.add(currentDate);
                                }
                            }
                        }
                    }

                    if (event == XMLStreamConstants.END_ELEMENT) {
                        String localName = reader.getLocalName();

                        if ("Obs".equals(localName)) {
                            currentDate = null;
                            currentRate = null;
                        }

                        if ("Series".equals(localName)) {
                            inSeries = false;
                            currentCurrency = null;
                        }
                    }
                }

                reader.close();
                emitter.complete();

            } catch (XMLStreamException e) {
                emitter.error(new IllegalArgumentException("Invalid SDMX XML format", e));
            } catch (Exception e) {
                emitter.error(new IllegalArgumentException("Unexpected error parsing SDMX XML", e));
            }
        });
    }
}
