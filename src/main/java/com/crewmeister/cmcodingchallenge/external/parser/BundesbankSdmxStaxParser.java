package com.crewmeister.cmcodingchallenge.external.parser;

import com.crewmeister.cmcodingchallenge.currency.model.CurrencyConversionRate;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

public class BundesbankSdmxStaxParser {


    public static List<CurrencyConversionRate> extractCurrencyRates(
            InputStream inputStream,
            LocalDate startDate,
            LocalDate endDate) throws Exception {


        Map<String, Map<LocalDate, CurrencyConversionRate>> uniqueRates = new HashMap<>();

        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
        XMLStreamReader reader = factory.createXMLStreamReader(inputStream);

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

                        if (!parsedDate.isBefore(startDate) && !parsedDate.isAfter(endDate)) {
                            currentDate = parsedDate;
                        } else {
                            currentDate = null;
                        }
                    } else {
                        currentDate = null;
                    }
                }

                if (inSeries && "ObsValue".equals(localName)
                        && currentCurrency != null
                        && currentDate != null) {

                    String rateStr = reader.getAttributeValue(null, "value");

                    if (rateStr != null && !rateStr.isEmpty()) {
                        currentRate = new BigDecimal(rateStr);

                        uniqueRates
                                .computeIfAbsent(currentCurrency, k -> new HashMap<>())
                                .putIfAbsent(
                                        currentDate,
                                        CurrencyConversionRate.builder()
                                                .currencyCode(currentCurrency)
                                                .rateDate(currentDate)
                                                .conversionRate(currentRate)
                                                .build()
                                );
                    }
                }
            }

            if (event == XMLStreamConstants.END_ELEMENT && "Obs".equals(reader.getLocalName())) {
                currentDate = null;
                currentRate = null;
            }

            if (event == XMLStreamConstants.END_ELEMENT && "Series".equals(reader.getLocalName())) {
                inSeries = false;
                currentCurrency = null;
            }
        }

        reader.close();

        return uniqueRates.values()
                .stream()
                .flatMap(m -> m.values().stream())
                .collect(Collectors.toList());
    }
}
