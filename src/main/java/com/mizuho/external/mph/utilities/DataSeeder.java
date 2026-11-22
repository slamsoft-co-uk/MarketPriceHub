package com.mizuho.external.mph.utilities;

import com.mizuho.external.mph.domain.model.Instrument;
import com.mizuho.external.mph.domain.model.Price;
import com.mizuho.external.mph.domain.model.Vendor;
import com.mizuho.external.mph.domain.repositories.InstrumentRepository;
import com.mizuho.external.mph.domain.repositories.PriceRepository;
import com.mizuho.external.mph.domain.repositories.VendorRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

@Component
@Profile("local")
public class DataSeeder implements ApplicationRunner {

    private final InstrumentRepository instrumentRepository;
    private final VendorRepository vendorRepository;
    private final PriceRepository priceRepository;

    DataSeeder(InstrumentRepository instrumentRepository, VendorRepository vendorRepository, PriceRepository priceRepository) {
        this.instrumentRepository = instrumentRepository;
        this.vendorRepository = vendorRepository;
        this.priceRepository = priceRepository;
    }

    public void loadFromCsv(String resourcePath) {
        InputStream is = Objects.requireNonNull(
                DataSeeder.class.getResourceAsStream(resourcePath),
                "Demo file not found: " + resourcePath
        );

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            reader.lines()
                    .skip(1) // Skip header row
                    .filter(line -> !line.isBlank())
                    .forEach(this::parseAndLoadLine);
        } catch (Exception e) {
            throw new RuntimeException("Failed to seed demo data", e);
        }
    }

    private void parseAndLoadLine(String line) {
        String[] parts = line.split(",");
        String type = parts[0].trim();
        switch (type) {
            case "VENDOR" -> vendorRepository.save(new Vendor(parts[1].trim(), parts[2].trim()));
            case "INSTRUMENT" -> instrumentRepository.save(new Instrument(parts[1].trim(), parts[2].trim()));
            case "PRICE" -> priceRepository.save(new Price(parts[1].trim(), parts[2].trim(), new BigDecimal(parts[3].trim()), parts[4].trim().equals("<CURRENTDATETIME>") ? LocalDateTime.now() : LocalDateTime.parse(parts[4].trim())));
            default -> throw new IllegalArgumentException("Unknown record type: " + type);
        }
    }

    @Override
    public void run(ApplicationArguments args)  {
        loadFromCsv("/demo-data.csv");
    }
}
