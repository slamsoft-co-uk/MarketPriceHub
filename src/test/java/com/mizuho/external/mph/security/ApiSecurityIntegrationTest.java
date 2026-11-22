package com.mizuho.external.mph.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void readEndpointsRemainPublic() throws Exception {
        mockMvc.perform(get("/getPricesForAllInstrumentsOfGivenVendor").param("vendorId", "V002"))
                .andExpect(status().isOk());
    }

    @Test
    void mutationEndpointsArePublic() throws Exception {
        mockMvc.perform(post("/addInstrument")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"instrumentId\":\"AUTH01\",\"instrumentDescription\":\"Authorized test\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void openApiDoesNotAdvertiseAuthentication() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.securitySchemes.basicAuth").doesNotExist());
    }
}
