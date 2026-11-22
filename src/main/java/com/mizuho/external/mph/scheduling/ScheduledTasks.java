package com.mizuho.external.mph.scheduling;

import com.mizuho.external.mph.services.DeleteStalePricesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    private final StalePriceProperties stalePriceProperties;
    private final DeleteStalePricesService deleteStalePricesService;

    public ScheduledTasks(DeleteStalePricesService deleteStalePricesService, StalePriceProperties stalePriceProperties) {
        this.deleteStalePricesService = deleteStalePricesService;
        this.stalePriceProperties = stalePriceProperties;
    }

    @Scheduled(cron = "${stale-prices.cron}")
    public int removeStalePrices() {
        long noDaysBeforePriceIsStale = stalePriceProperties.stale();
        logger.info("Removing stale prices older than {} days", noDaysBeforePriceIsStale);
        int recordsDeleted = deleteStalePricesService.deleteStalePrices(
                LocalDateTime.now().minusDays(noDaysBeforePriceIsStale));
        logger.info("Removal process complete - stale prices removed: {}", recordsDeleted);
        return recordsDeleted;
    }

}