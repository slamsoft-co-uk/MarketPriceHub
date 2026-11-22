package com.mizuho.external.mph.webcontrollers;

import com.mizuho.external.mph.services.PublishPricesService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PublishPriceControllerTest {

    @Test
    void publishesPriceFromRequest() throws Exception {
        PublishPricesService service = mock(PublishPricesService.class);
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new PublishPricesController(service))
                .build();

        mockMvc.perform(post("/publishPrices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"instrumentId":"I003","vendorId":"V002","price":224.75,
                                 "priceDate":"2025-01-01T00:00:00"}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Insertion of a new price for instrument I003, vendor V002 complete"));

        verify(service).publishPrice("V002", "I003", new BigDecimal("224.75"),
                LocalDateTime.of(2025, 1, 1, 0, 0));
    }
}