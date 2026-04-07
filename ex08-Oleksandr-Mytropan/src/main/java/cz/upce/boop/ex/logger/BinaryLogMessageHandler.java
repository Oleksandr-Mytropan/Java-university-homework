package cz.upce.boop.ex.logger;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class BinaryLogMessageHandler implements LogMessageHandler {

    private DataOutputStream dos;

    public BinaryLogMessageHandler(String file) throws IOException {
        dos = new DataOutputStream(new FileOutputStream(file, true));
    }

    @Override
    public void handle(LogMessage msg) {
        try {
            dos.writeUTF(msg.occurence().toString());
            dos.writeUTF(msg.severity().toString());
            dos.writeUTF(msg.message());
            dos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
