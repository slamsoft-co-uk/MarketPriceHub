package com.mizuho.external.mph.webcontrollers;

import org.junit.jupiter.api.Test;
import com.mizuho.external.mph.domain.model.Price;
import com.mizuho.external.mph.domain.model.Vendor;
import com.mizuho.external.mph.services.GetPricesForAllVendorsOfGivenInstrumentService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetPricesForAllVendorsOfGivenInstrumentResponseControllerTest {

    @Test
    void returnsAllVendorsAndPricesForInstrument() throws Exception {
        GetPricesForAllVendorsOfGivenInstrumentService service = mock(GetPricesForAllVendorsOfGivenInstrumentService.class);
        when(service.getPrices("I001")).thenReturn(Map.of(
                new Vendor("V002", "Second vendor"),
                new Price("V002", "I001", new BigDecimal("20.50"), LocalDateTime.parse("2026-11-21T17:42:12")),
                new Vendor("V001", "First vendor"),
                new Price("V001", "I001", new BigDecimal("10.25"), LocalDateTime.parse("2026-11-21T17:42:12"))));
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new GetPricesForAllVendorsOfGivenInstrumentController(service))
                .build();

        mockMvc.perform(get("/getPricesForAllVendorsOfGivenInstrument")
                        .param("instrumentId", "I001")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                          {"VendorId":"V001","VendorDescription":"First vendor","Price":10.25,"PriceDate":"2026-11-21T17:42:12"},
                          {"VendorId":"V002","VendorDescription":"Second vendor","Price":20.50,"PriceDate":"2026-11-21T17:42:12"}
                        ]
                        """));
    }

    @Test
    void rejectsBlankOrOverlongInstrumentId() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
                new GetPricesForAllVendorsOfGivenInstrumentController(mock(GetPricesForAllVendorsOfGivenInstrumentService.class))).build();

        mockMvc.perform(get("/getPricesForAllVendorsOfGivenInstrument"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/getPricesForAllVendorsOfGivenInstrument").param("instrumentId", " "))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/getPricesForAllVendorsOfGivenInstrument").param("instrumentId", "123456789012345678901"))
                .andExpect(status().isBadRequest());
    }
}
