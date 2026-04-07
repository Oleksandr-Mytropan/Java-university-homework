package cz.upce.boop.ex.logger;

@FunctionalInterface
public interface Logger {

    public void logMessage(LogMessageSeverity severity, String message);
    
    default void addLogMessageHandler(LogMessageSeverity minimalSeverity, LogMessageHandler handler){}
    
    default LogMessage getLastLogMessage(){
        return null;
    }
}
