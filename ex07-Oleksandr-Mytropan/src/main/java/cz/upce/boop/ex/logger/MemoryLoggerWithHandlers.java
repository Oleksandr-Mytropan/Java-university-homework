package cz.upce.boop.ex.logger;

import cz.upce.boop.ex.collection.BGenericList;
import cz.upce.boop.ex.collection.BList;
import java.time.LocalDateTime;

public class MemoryLoggerWithHandlers implements Logger {

    private record HandlerSeverityPair(LogMessageSeverity severity, LogMessageHandler handler) {

    }

    private final BList<LogMessage> loggedMessages;
    private final BList<HandlerSeverityPair> handlers;
    private final DateTimeProvider dateTimeProvider;

    public MemoryLoggerWithHandlers(DateTimeProvider dateTimeProvider) {
        if (dateTimeProvider == null) {
            throw new IllegalArgumentException("dateTimeProvider is null");
        }

        loggedMessages = new BGenericList<>();
        handlers = new BGenericList<>();

        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public void logMessage(LogMessageSeverity severity, String message) {
        if (message == null) {
            throw new IllegalArgumentException("message is null");
        }

        // construct log message
        LocalDateTime datetime = dateTimeProvider.getDateTime();
        LogMessage log = new LogMessage(datetime, severity, message);

        // add to memory storage
        loggedMessages.add(log);

        // call all relevant handlers
        for (HandlerSeverityPair pair : handlers) {
            if (pair.severity.isLessOrEqualSeverityThan(severity)) {
                pair.handler.handle(log);
            }
        }
    }

    public void addLogMessageHandler(LogMessageSeverity minimalSeverity, LogMessageHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("handler is null");
        }

        handlers.add(new HandlerSeverityPair(minimalSeverity, handler));
    }

    public BList<LogMessage> getLoggedMessages() {
        return loggedMessages;
    }

    public LogMessage getLastLogMessage() {
        return loggedMessages.get(loggedMessages.size() - 1);
    }
}
