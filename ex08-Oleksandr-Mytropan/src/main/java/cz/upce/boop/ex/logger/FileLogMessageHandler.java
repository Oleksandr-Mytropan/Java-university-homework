package cz.upce.boop.ex.logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileLogMessageHandler implements LogMessageHandler {

    private BufferedWriter writer;

    public FileLogMessageHandler(String file) throws IOException {
        writer = new BufferedWriter(new FileWriter(file, true));
    }

    @Override
    public void handle(LogMessage msg) { 
        try {
            writer.write(
                    "[" + msg.occurence() + "] "
                    + msg.severity() + " "
                    + msg.message()
            );
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
