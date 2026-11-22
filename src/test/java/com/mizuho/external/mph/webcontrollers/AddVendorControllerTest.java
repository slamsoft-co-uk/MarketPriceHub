package com.mizuho.external.mph.webcontrollers;

import com.mizuho.external.mph.domain.model.Vendor;
import com.mizuho.external.mph.services.AddVendorService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AddVendorControllerTest {

    @Test
    void addsVendorFromRequest() throws Exception {
        AddVendorService service = mock(AddVendorService.class);
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new AddVendorController(service))
                .build();

        mockMvc.perform(post("/addVendor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"vendorId":"V876","vendorDescription":"Vendor Description for V876"}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("Insertion of vendor V876 complete"));

        verify(service).addVendor(argThat(vendor ->
                vendor.equals(new Vendor("V876", "Vendor Description for V876"))));
    }
}