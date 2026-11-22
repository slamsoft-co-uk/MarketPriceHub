package com.mizuho.external.mph.webcontrollers;

import org.junit.jupiter.api.Test;
import com.mizuho.external.mph.domain.model.Instrument;
import com.mizuho.external.mph.domain.model.Price;
import com.mizuho.external.mph.services.GetPricesForAllInstrumentsOfGivenVendorService;
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

class GetPricesForAllInstrumentsOfGivenVendorResponseControllerTest {

    @Test
    void returnsAllVendorInstrumentsAndPrices() throws Exception {
        GetPricesForAllInstrumentsOfGivenVendorService service = mock(GetPricesForAllInstrumentsOfGivenVendorService.class);
        when(service.getPrices("V001")).thenReturn(Map.of(
                new Instrument("I002", "Second instrument"),
                new Price("V001", "I002", new BigDecimal("20.50"), LocalDateTime.parse("2026-11-21T17:42:12")),
                new Instrument("I001", "First instrument"),
                new Price("V001", "I001", new BigDecimal("10.25"), LocalDateTime.parse("2026-11-21T17:42:12"))));
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new GetPricesForAllInstrumentsOfGivenVendorController(service)).build();

        mockMvc.perform(get("/getPricesForAllInstrumentsOfGivenVendor")
                        .param("vendorId", "V001")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                          {"InstrumentId":"I001","InstrumentDescription":"First instrument","Price":10.25,"PriceDate":"2026-11-21T17:42:12"},
                          {"InstrumentId":"I002","InstrumentDescription":"Second instrument","Price":20.50,"PriceDate":"2026-11-21T17:42:12"}
                        ]
                        """));
    }

    @Test
    void rejectsBlankOrOverlongVendorId() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
                new GetPricesForAllInstrumentsOfGivenVendorController(mock(GetPricesForAllInstrumentsOfGivenVendorService.class))).build();

        mockMvc.perform(get("/getPricesForAllInstrumentsOfGivenVendor"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/getPricesForAllInstrumentsOfGivenVendor").param("vendorId", " "))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/getPricesForAllInstrumentsOfGivenVendor").param("vendorId", "123456789012345678901"))
                .andExpect(status().isBadRequest());
    }
}
