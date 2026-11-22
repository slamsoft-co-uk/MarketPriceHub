package com.mizuho.external.mph.webcontrollers;

import com.mizuho.external.mph.domain.model.Instrument;
import com.mizuho.external.mph.domain.dto.request.InstrumentUpdateRequest;
import com.mizuho.external.mph.services.AddInstrumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AddInstrumentController {

    private final AddInstrumentService addInstrumentService;

    public AddInstrumentController(AddInstrumentService addInstrumentService) {
        this.addInstrumentService = addInstrumentService;
    }

    @PostMapping("/addInstrument")
    public ResponseEntity<String> addInstrument(
            @Valid @RequestBody InstrumentUpdateRequest instrumentUpdateRequest) {
        addInstrumentService.addInstrument(new Instrument(
                instrumentUpdateRequest.instrumentId(), instrumentUpdateRequest.instrumentDescription()));
        return new ResponseEntity<>(
                String.format("Insertion of instrument %s complete", instrumentUpdateRequest.instrumentId()),
                HttpStatus.OK);
    }
}
