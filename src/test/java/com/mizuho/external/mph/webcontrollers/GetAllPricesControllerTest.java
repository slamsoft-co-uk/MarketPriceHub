package com.mizuho.external.mph.webcontrollers;

import com.mizuho.external.mph.domain.dto.response.GetAllPricesResponse;
import com.mizuho.external.mph.services.GetAllPricesService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetAllPricesControllerTest {

    @Test
    void returnsAllPricesSortedByVendorAndInstrument() throws Exception {
        GetAllPricesService service = mock(GetAllPricesService.class);
        LocalDateTime priceDate = LocalDateTime.parse("2026-11-21T17:42:12");
        when(service.getAllPrices()).thenReturn(List.of(
                new GetAllPricesResponse("V002", "Second vendor", "I001", "First instrument",
                        new BigDecimal("20.50"), priceDate),
                new GetAllPricesResponse("V001", "First vendor", "I002", "Second instrument",
                        new BigDecimal("15.25"), priceDate),
                new GetAllPricesResponse("V001", "First vendor", "I001", "First instrument",
                        new BigDecimal("10.25"), priceDate)));
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new GetAllPricesController(service)).build();

        mockMvc.perform(get("/getAllPrices").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                          {"VendorId":"V001","VendorDescription":"First vendor","InstrumentId":"I001","InstrumentDescription":"First instrument","Price":10.25,"PriceDate":"2026-11-21T17:42:12"},
                          {"VendorId":"V001","VendorDescription":"First vendor","InstrumentId":"I002","InstrumentDescription":"Second instrument","Price":15.25,"PriceDate":"2026-11-21T17:42:12"},
                          {"VendorId":"V002","VendorDescription":"Second vendor","InstrumentId":"I001","InstrumentDescription":"First instrument","Price":20.50,"PriceDate":"2026-11-21T17:42:12"}
                        ]
                        """));
    }
}
