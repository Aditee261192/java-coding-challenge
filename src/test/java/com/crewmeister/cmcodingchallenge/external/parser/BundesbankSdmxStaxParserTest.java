package com.crewmeister.cmcodingchallenge.external.parser;

import com.crewmeister.cmcodingchallenge.currency.model.CurrencyConversionRate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BundesbankSdmxStaxParserTest {

    @Test
    void should_parse_valid_xml_and_return_currency_rates() {
        InputStream xml = xmlInput(validSdmxXml());

        Flux<CurrencyConversionRate> rateFlux = BundesbankSdmxStaxParser.extractCurrencyRatesStream(
                xml,
                LocalDate.of(2026, 1, 20),
                LocalDate.of(2026, 1, 22)
        );

        List<CurrencyConversionRate> result = rateFlux.collectList().block();

        assertNotNull(result);
        assertEquals(1, result.size());

        CurrencyConversionRate rate = result.get(0);
        assertEquals("USD", rate.getCurrencyCode());
        assertEquals(LocalDate.of(2026, 1, 22), rate.getRateDate());
        assertEquals("1.2345", rate.getConversionRate().toPlainString());
    }

    @Test
    void should_ignore_rates_outside_date_range() {
        InputStream xml = xmlInput(validSdmxXml());

        Flux<CurrencyConversionRate> rateFlux = BundesbankSdmxStaxParser.extractCurrencyRatesStream(
                xml,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 10)
        );

        List<CurrencyConversionRate> result = rateFlux.collectList().block();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void should_throw_exception_for_invalid_xml() {
        InputStream invalidXml = xmlInput("<invalid><xml>");

        Flux<CurrencyConversionRate> rateFlux =
                BundesbankSdmxStaxParser.extractCurrencyRatesStream(
                        invalidXml,
                        LocalDate.now().minusDays(1),
                        LocalDate.now()
                );

        assertThrows(Exception.class, () -> rateFlux.collectList().block());
    }

    private InputStream xmlInput(String xml) {
        return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
    }

    private String validSdmxXml() {
        return
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<message:StructureSpecificData\n" +
                        " xmlns:message=\"http://www.sdmx.org/resources/sdmxml/schemas/v2_1/message\"\n" +
                        " xmlns:data=\"http://www.sdmx.org/resources/sdmxml/schemas/v2_1/data/structurespecific\">\n" +
                        "  <message:DataSet>\n" +
                        "    <data:Series>\n" +
                        "      <data:Value id=\"BBK_ID\" value=\"BBEX3.A.USD.EUR.CA.AA.A04\"/>\n" +
                        "      <data:Obs>\n" +
                        "        <data:ObsDimension value=\"2026-01-22\"/>\n" +
                        "        <data:ObsValue value=\"1.2345\"/>\n" +
                        "      </data:Obs>\n" +
                        "    </data:Series>\n" +
                        "  </message:DataSet>\n" +
                        "</message:StructureSpecificData>";
    }
}
