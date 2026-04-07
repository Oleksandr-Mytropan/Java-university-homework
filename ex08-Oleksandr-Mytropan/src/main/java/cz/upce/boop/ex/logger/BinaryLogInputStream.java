package cz.upce.boop.ex.logger;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BinaryLogInputStream implements AutoCloseable {

    private DataInputStream dis;

    public BinaryLogInputStream(InputStream is) {
        dis = new DataInputStream(is);
    }

    public LogMessage readLogMessage() throws IOException {
        try {
            String databin = dis.readUTF();

            databin = databin.trim();
            databin = databin.replace("[", "").replace("]", "");

            LocalDateTime data = LocalDateTime.parse(databin);

            String seviritybin = dis.readUTF();

            LogMessageSeverity sevirity;

            if (null == seviritybin) {
                return null;
            } else {
                switch (seviritybin) {
                    case "Debug": {
                        sevirity = LogMessageSeverity.Debug;
                        break;
                    }
                    case "Information": {
                        sevirity = LogMessageSeverity.Information;
                        break;
                    }
                    case "Warning": {
                        sevirity = LogMessageSeverity.Warning;
                        break;
                    }
                    case "Error": {
                        sevirity = LogMessageSeverity.Error;
                        break;
                    }
                    default:
                        throw new IllegalArgumentException("Sevirity reading from file failed!");
                }
            }

            String message = dis.readUTF();

            return new LogMessage(data, sevirity, message);

        } catch (EOFException e) {
            return null;
        }

    }

    @Override
    public void close() throws Exception {
        dis.close();
    }

}
