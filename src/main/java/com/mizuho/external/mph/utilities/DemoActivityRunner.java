package com.mizuho.external.mph.utilities;

import com.mizuho.external.mph.domain.model.Price;
import com.mizuho.external.mph.domain.repositories.PriceRepository;
import com.mizuho.external.mph.services.GetPricesForAllInstrumentsOfGivenVendorService;
import com.mizuho.external.mph.services.GetPricesForAllVendorsOfGivenInstrumentService;
import com.mizuho.external.mph.services.PublishPricesService;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Component
@Profile("local")
@ConditionalOnProperty(name = "demo.activity.enabled", havingValue = "true")
@Order(2)
public class DemoActivityRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DemoActivityRunner.class);
    private static final int WORKER_COUNT = 4;
    private static final long MIN_PAUSE_MILLIS = 500;
    private static final long MAX_PAUSE_MILLIS = 1500;

    private final PriceRepository priceRepository;
    private final GetPricesForAllInstrumentsOfGivenVendorService pricesByVendorService;
    private final GetPricesForAllVendorsOfGivenInstrumentService pricesByInstrumentService;
    private final PublishPricesService publishPricesService;
    private final ExecutorService executor = Executors.newFixedThreadPool(WORKER_COUNT);

    public DemoActivityRunner(
            PriceRepository priceRepository,
            GetPricesForAllInstrumentsOfGivenVendorService pricesByVendorService,
            GetPricesForAllVendorsOfGivenInstrumentService pricesByInstrumentService,
            PublishPricesService publishPricesService) {
        this.priceRepository = priceRepository;
        this.pricesByVendorService = pricesByVendorService;
        this.pricesByInstrumentService = pricesByInstrumentService;
        this.publishPricesService = publishPricesService;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<Price> existingPrices = priceRepository.findAll();
        if (existingPrices.isEmpty()) {
            throw new IllegalStateException("Cannot start demo activity without seeded prices.");
        }

        List<String> vendorIds = existingPrices.stream()
                .map(Price::vendorId)
                .distinct()
                .toList();
        List<String> instrumentIds = existingPrices.stream()
                .map(Price::instrumentId)
                .distinct()
                .toList();

        logger.info("Starting local demo activity with {} workers", WORKER_COUNT);
        for (int worker = 1; worker <= WORKER_COUNT; worker++) {
            int workerId = worker;
            executor.submit(() -> runWorker(workerId, vendorIds, instrumentIds, existingPrices));
        }
    }

    private void runWorker(
            int workerId, List<String> vendorIds, List<String> instrumentIds, List<Price> existingPrices) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int operation = ThreadLocalRandom.current().nextInt(3);
                if (operation == 0) {
                    String vendorId = choose(vendorIds);
                    pricesByVendorService.getPrices(vendorId);
                    logger.info("Demo worker {} retrieved prices for vendor {}", workerId, vendorId);
                } else if (operation == 1) {
                    String instrumentId = choose(instrumentIds);
                    pricesByInstrumentService.getPrices(instrumentId);
                    logger.info("Demo worker {} retrieved prices for instrument {}", workerId, instrumentId);
                } else {
                    Price baseline = choose(existingPrices);
                    BigDecimal price = randomizePrice(baseline.price());
                    publishPricesService.publishPrice(
                            baseline.vendorId(), baseline.instrumentId(), price, LocalDateTime.now());
                    logger.info("Demo worker {} published price {} for vendor {} and instrument {}",
                            workerId, price, baseline.vendorId(), baseline.instrumentId());
                }
            } catch (RuntimeException exception) {
                logger.error("Demo worker {} failed to perform an operation", workerId, exception);
            }
            try {
                Thread.sleep(ThreadLocalRandom.current().nextLong(MIN_PAUSE_MILLIS, MAX_PAUSE_MILLIS));
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static <T> T choose(List<T> values) {
        return values.get(ThreadLocalRandom.current().nextInt(values.size()));
    }

    private static BigDecimal randomizePrice(BigDecimal baseline) {
        double multiplier = ThreadLocalRandom.current().nextDouble(0.99, 1.01);
        return baseline.multiply(BigDecimal.valueOf(multiplier))
                .setScale(Math.max(baseline.scale(), 2), RoundingMode.HALF_UP);
    }

    @PreDestroy
    public void stop() {
        executor.shutdownNow();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                logger.warn("Demo activity workers did not stop within five seconds");
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            logger.warn("Interrupted while stopping demo activity workers", exception);
        }
    }
}
