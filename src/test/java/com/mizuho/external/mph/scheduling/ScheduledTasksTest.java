package com.mizuho.external.mph.scheduling;

import com.mizuho.external.mph.services.DeleteStalePricesService;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ScheduledTasksTest {

    @Test
    void deletesPricesUsingConfiguredStaleAgeAndReturnsDeletedCount() {
        DeleteStalePricesService deleteService = mock(DeleteStalePricesService.class);
        ScheduledTasks scheduledTasks = new ScheduledTasks(deleteService, new StalePriceProperties(30));
        when(deleteService.deleteStalePrices(any(LocalDateTime.class))).thenReturn(4);
        LocalDateTime startedAt = LocalDateTime.now();

        int deleted = scheduledTasks.removeStalePrices();

        LocalDateTime finishedAt = LocalDateTime.now();
        assertEquals(4, deleted);
        verify(deleteService).deleteStalePrices(argThat(cutoff ->
                !cutoff.isBefore(startedAt.minusDays(30))
                        && !cutoff.isAfter(finishedAt.minusDays(30))));
    }

    @Test
    void usesZeroDaysWhenConfiguredAndReturnsZeroWhenNothingWasDeleted() {
        DeleteStalePricesService deleteService = mock(DeleteStalePricesService.class);
        when(deleteService.deleteStalePrices(any(LocalDateTime.class))).thenReturn(0);
        ScheduledTasks scheduledTasks = new ScheduledTasks(deleteService, new StalePriceProperties(0));
        LocalDateTime startedAt = LocalDateTime.now();

        int deleted = scheduledTasks.removeStalePrices();

        LocalDateTime finishedAt = LocalDateTime.now();
        assertEquals(0, deleted);
        verify(deleteService).deleteStalePrices(argThat(cutoff ->
                !cutoff.isBefore(startedAt) && !cutoff.isAfter(finishedAt)));
    }

    @Test
    void scheduledMethodHasConfiguredCronExpression() throws NoSuchMethodException {
        Scheduled scheduled = ScheduledTasks.class.getMethod("removeStalePrices")
                .getAnnotation(Scheduled.class);

        assertNotNull(scheduled);
        assertEquals("${stale-prices.cron}", scheduled.cron());
    }
}
