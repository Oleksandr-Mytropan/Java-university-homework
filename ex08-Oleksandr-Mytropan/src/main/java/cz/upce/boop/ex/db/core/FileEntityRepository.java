package cz.upce.boop.ex.db.core;

import cz.upce.boop.ex.logger.ConsoleLogMessageHandler;
import cz.upce.boop.ex.logger.DateTimeProvider;
import cz.upce.boop.ex.logger.FileLogMessageHandler;
import cz.upce.boop.ex.logger.LogMessageHandler;
import cz.upce.boop.ex.logger.LogMessageSeverity;
import cz.upce.boop.ex.logger.MemoryLoggerWithHandlers;
import cz.upce.boop.ex.logger.Logger;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.ConsoleHandler;
import java.util.stream.Collectors;

public class FileEntityRepository<T extends DatabaseEntity<K>, K extends PrimaryKey<?>>
        implements EntityRepository<T, K> {

    protected final File databaseDir;
    protected final EntitySerializer<T> serializer;
    protected Logger logger = new MemoryLoggerWithHandlers(() -> LocalDateTime.now());
    protected LogMessageHandler consoleLogger = new ConsoleLogMessageHandler();
    
    public FileEntityRepository(String dirPath, EntitySerializer<T> serializer) {
        this.databaseDir = new File(dirPath);
        if (!databaseDir.exists()) {
            if (!databaseDir.mkdirs()) { 
                throw new DatabaseException("Failed to create directory: " + dirPath);
            }
        }
        this.serializer = serializer;
        this.logger = logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public T findById(K id) {
        File file = new File(databaseDir, id.toFileName());
        if (!file.exists()) {
            logger.logMessage(LogMessageSeverity.Warning, "ID: " + id.toString() + "NOT FOUND!");
            consoleLogger.handle(logger.getLastLogMessage());
            return null;
        }

        logger.logMessage(LogMessageSeverity.Information, "Finding ID: " + id.toString());
        consoleLogger.handle(logger.getLastLogMessage());
        return readFromFile(file);
    }

    @Override
    public List<T> findAll() {
        List<T> result = new ArrayList<>();
        File[] files = databaseDir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    result.add(readFromFile(file));

                    logger.logMessage(LogMessageSeverity.Information, "Finding all and return List");
                    consoleLogger.handle(logger.getLastLogMessage());
                }
            }
        }

        logger.logMessage(LogMessageSeverity.Error, "Finding ends with error, empty list is returned");
        consoleLogger.handle(logger.getLastLogMessage());
        return result;
    }

    @Override
    public void save(T entity) {
        if (entity.getId() == null) {
            logger.logMessage(LogMessageSeverity.Error, "Entity ID is invalid!");
            consoleLogger.handle(logger.getLastLogMessage());

            throw new IllegalArgumentException("Entity ID cannot be null");
        }

        File file = new File(databaseDir, entity.getId().toFileName());
        if (file.exists()) {
            logger.logMessage(LogMessageSeverity.Warning, "Already existing ID!");
            consoleLogger.handle(logger.getLastLogMessage());
            throw new DatabaseException("Entity with ID " + entity.getId() + " already exists");
        }

        writeToFile(file, entity);
        logger.logMessage(LogMessageSeverity.Information, "Saving entity: " + entity.getId());
        consoleLogger.handle(logger.getLastLogMessage());
    }

    @Override
    public void update(T entity) {
        if (entity.getId() == null) {
            logger.logMessage(LogMessageSeverity.Error, "Null ID!");
            consoleLogger.handle(logger.getLastLogMessage());
            throw new IllegalArgumentException("Entity ID cannot be null");
        }

        File file = new File(databaseDir, entity.getId().toFileName());
        if (!file.exists()) {
            logger.logMessage(LogMessageSeverity.Error, "Entity not found!");
            consoleLogger.handle(logger.getLastLogMessage());
            throw new DatabaseException("Entity with ID " + entity.getId() + " does not exist");
        }

        writeToFile(file, entity);
        logger.logMessage(LogMessageSeverity.Information, "Updating entity: "
                + entity.getId().toString());

        consoleLogger.handle(logger.getLastLogMessage());
    }

    @Override
    public void delete(K id) {
        File file = new File(databaseDir, id.toFileName());

        if (!file.exists()) {
            logger.logMessage(LogMessageSeverity.Error, "Entity not found!");
            consoleLogger.handle(logger.getLastLogMessage());
            throw new DatabaseException("Entity with ID " + id + " does not exist");
        }

        if (!file.delete()) {
            logger.logMessage(LogMessageSeverity.Error, "Deleting failed!");
            consoleLogger.handle(logger.getLastLogMessage());
            throw new DatabaseException("Failed to delete entity with ID " + id);
        }

        logger.logMessage(LogMessageSeverity.Information, "Deleting entity with id: " + id);
        consoleLogger.handle(logger.getLastLogMessage());
    }

    @Override
    public List<T> findByCondition(Predicate<T> condition) {
        List<T> all = findAll();
        List<T> result = new ArrayList<>();

        for (T entity : all) {
            if (condition.test(entity)) {
                result.add(entity);
            } else {
                logger.logMessage(LogMessageSeverity.Warning, "Entity with condition: " + condition + " not found!");
                consoleLogger.handle(logger.getLastLogMessage());
            }
        }

        String str = result.stream().map(Object::toString).collect(Collectors.joining(" ; "));

        logger.logMessage(LogMessageSeverity.Information, "Finding entity by condition: " + str);
        consoleLogger.handle(logger.getLastLogMessage());
        return result;
    }

    protected void writeToFile(File file, T entity) {
        String serialized = serializer.serialize(entity);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(serialized);

        } catch (IOException e) {
            logger.logMessage(LogMessageSeverity.Error, "Writing entity to file failed!");
            consoleLogger.handle(logger.getLastLogMessage());
            throw new DatabaseException("Error writing entity with ID " + entity.getId(), e);
        }

    }

    private T readFromFile(File file) {
        try {
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }

            T entity = serializer.deserialize(content.toString());

            logger.logMessage(LogMessageSeverity.Information, "Reading file: " + file.getName());
            consoleLogger.handle(logger.getLastLogMessage());

            return entity;

        } catch (IOException e) {
            logger.logMessage(LogMessageSeverity.Error, "Reading entity from file failed!");
            consoleLogger.handle(logger.getLastLogMessage());
            throw new DatabaseException("Error reading file: " + file.getName(), e);
        }
    }
}
