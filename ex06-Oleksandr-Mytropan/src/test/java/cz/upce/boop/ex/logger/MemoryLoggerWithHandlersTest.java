package cz.upce.boop.ex.logger;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MemoryLoggerWithHandlersTest {

    @Test
    public void test1StubMock() {
        DateTimeProvider dateTimeProvider = mock(DateTimeProvider.class);
        LocalDateTime fixedtTime = LocalDateTime.of(2020, 1, 1, 10, 0);
        when(dateTimeProvider.getDateTime()).thenReturn(fixedtTime);

        LogMessageHandler handler = mock(LogMessageHandler.class);

        MemoryLoggerWithHandlers logger = new MemoryLoggerWithHandlers(dateTimeProvider);

        logger.addLogMessageHandler(LogMessageSeverity.Debug, handler);

        logger.logMessage(LogMessageSeverity.Error, "message");

        LogMessage last = logger.getLastLogMessage();

        assertEquals(fixedtTime, last.occurence());
        assertEquals(LogMessageSeverity.Error, last.severity());
        assertEquals("message", last.message());

        verify(handler).handle(last);

    }

    @Test
    public void test2ManyDates() {
        DateTimeProvider dateTimeProvider = mock(DateTimeProvider.class);
        LocalDateTime t1 = LocalDateTime.of(2020, 1, 1, 10, 0);
        LocalDateTime t2 = LocalDateTime.of(2022, 5, 1, 3, 0);

        when(dateTimeProvider.getDateTime()).thenReturn(t1, t2);

        MemoryLoggerWithHandlers logger = new MemoryLoggerWithHandlers(dateTimeProvider);

        logger.logMessage(LogMessageSeverity.Error, "message1");
        logger.logMessage(LogMessageSeverity.Error, "message2");

        assertEquals(t1, logger.getLoggedMessages().get(0).occurence());
        assertEquals(t2, logger.getLoggedMessages().get(1).occurence());
    }

    @Test
    public void test3CalledAndNotCalledHandlers() {
        DateTimeProvider dateTimeProvider = mock(DateTimeProvider.class);
        when(dateTimeProvider.getDateTime()).thenReturn(LocalDateTime.now());

        LogMessageHandler handler1 = mock(LogMessageHandler.class);
        LogMessageHandler handler2 = mock(LogMessageHandler.class);

        MemoryLoggerWithHandlers logger = new MemoryLoggerWithHandlers(dateTimeProvider);

        logger.addLogMessageHandler(LogMessageSeverity.Warning, handler1);
        logger.addLogMessageHandler(LogMessageSeverity.Error, handler2);

        logger.logMessage(LogMessageSeverity.Warning, "warning_msg");

        verify(handler1).handle(any(LogMessage.class));
        verify(handler2, never()).handle(any());
    }

    @Test
    public void test4CalledBothHandlers() {
        DateTimeProvider dateTimeProvider = mock(DateTimeProvider.class);
        when(dateTimeProvider.getDateTime()).thenReturn(LocalDateTime.now());

        LogMessageHandler handler1 = mock(LogMessageHandler.class);
        LogMessageHandler handler2 = mock(LogMessageHandler.class);

        MemoryLoggerWithHandlers logger = new MemoryLoggerWithHandlers(dateTimeProvider);

        logger.addLogMessageHandler(LogMessageSeverity.Warning, handler1);
        logger.addLogMessageHandler(LogMessageSeverity.Error, handler2);

        logger.logMessage(LogMessageSeverity.Error, "error_msg");

        verify(handler1).handle(any(LogMessage.class));
        verify(handler2).handle(any(LogMessage.class));
    }
}
