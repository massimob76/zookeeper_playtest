package writer;

import listener.ShutdownListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;

public class DataWriterImpl implements DataWriter {

    private static final Path PATH = FileSystems.getDefault().getPath("temp");
    private static final Logger LOGGER = LoggerFactory.getLogger(DataWriterImpl.class);

    private final BufferedWriter bufferedWriter;

    public DataWriterImpl(ShutdownListener shutdownListener) throws IOException {
        this.bufferedWriter = Files.newBufferedWriter(PATH, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        shutdownListener.registerShutdownAction(() -> {
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                LOGGER.error("exception while closing writer {}", e);
            }
        });
    }

    @Override
    public void write(String data) {
        try {
            LOGGER.debug("writing data: {}", data);
            bufferedWriter.write(data);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            LOGGER.error("Exception {} while writing data {}", e, data);
        }
    }

}
