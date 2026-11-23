package com.mizuho.external.mph.webcontrollers;

import com.mizuho.external.mph.domain.model.Instrument;
import com.mizuho.external.mph.domain.dto.request.InstrumentUpdateRequest;
import com.mizuho.external.mph.services.AddInstrumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Locale;

@RestController
public class AddInstrumentController {

    private static final Logger logger = LoggerFactory.getLogger(AddInstrumentController.class);

    private final AddInstrumentService addInstrumentService;

    public AddInstrumentController(AddInstrumentService addInstrumentService) {
        this.addInstrumentService = addInstrumentService;
    }

    @PostMapping("/addInstrument")
    public ResponseEntity<String> addInstrument(
            @Valid @RequestBody InstrumentUpdateRequest instrumentUpdateRequest, Principal principal) {
        long startTime = System.nanoTime();
        String userName = principal != null ? principal.getName() : "unknown";
        logger.info("Received request to add instrument: user={}, instrumentId={}, instrumentDescription={}",
                userName, instrumentUpdateRequest.instrumentId(), instrumentUpdateRequest.instrumentDescription());

        boolean completed = false;
        try {
            addInstrumentService.addInstrument(new Instrument(
                    instrumentUpdateRequest.instrumentId(), instrumentUpdateRequest.instrumentDescription()));
            completed = true;
            return new ResponseEntity<>(
                    String.format("Insertion of instrument %s complete", instrumentUpdateRequest.instrumentId()),
                    HttpStatus.OK);
        } finally {
            String durationSeconds = String.format(Locale.ROOT, "%.4f",
                    (System.nanoTime() - startTime) / 1_000_000_000.0);
            logger.info("Add instrument request finished: user={}, instrumentId={}, instrumentDescription={}, "
                            + "outcome={}, durationSeconds={}",
                    userName, instrumentUpdateRequest.instrumentId(), instrumentUpdateRequest.instrumentDescription(),
                    completed ? "success" : "failure", durationSeconds);
        }
    }
}
