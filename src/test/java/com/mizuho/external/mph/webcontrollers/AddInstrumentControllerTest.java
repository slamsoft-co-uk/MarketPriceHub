package com.mizuho.external.mph.webcontrollers;

import com.mizuho.external.mph.domain.model.Instrument;
import com.mizuho.external.mph.services.AddInstrumentService;
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

class AddInstrumentControllerTest {

    @Test
    void addsInstrumentFromRequest() throws Exception {
        AddInstrumentService service = mock(AddInstrumentService.class);
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new AddInstrumentController(service))
                .build();

        mockMvc.perform(post("/addInstrument")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"instrumentId":"IN987","instrumentDescription":"Description for IN987"}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("Insertion of instrument IN987 complete"));

        verify(service).addInstrument(argThat(instrument ->
                instrument.equals(new Instrument("IN987", "Description for IN987"))));
    }
}
