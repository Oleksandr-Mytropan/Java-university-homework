package cz.upce.boop.ex;

import cz.upce.boop.ex.di.DIContainer;
import cz.upce.boop.ex.di.Scope;
import cz.upce.boop.ex.logger.*;

public class Ex07 {

    public static void main(String[] args) {
        //ukol 4
        DIContainer container = new DIContainer();

        container.register(
                DateTimeProvider.class,
                LocalNowDateTimeProvider.class,
                Scope.SINGLETON
        );

        container.register(
                Logger.class,
                MemoryLoggerWithHandlers.class,
                (DIContainer c) -> {
                    DateTimeProvider dateTimeProvider = c.getInstance(DateTimeProvider.class);

                    MemoryLoggerWithHandlers logger = new MemoryLoggerWithHandlers(dateTimeProvider);

                    logger.addLogMessageHandler(
                            LogMessageSeverity.Information,
                            new ConsoleLogMessageHandler()
                    );
                    return logger;
                },
                Scope.SINGLETON
        );

        Logger logger = container.getInstance(Logger.class);

        logger.logMessage(LogMessageSeverity.Information, "Test message");
    }
}
