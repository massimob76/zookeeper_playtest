import listener.ShutdownListener;
import listener.ShutdownListenerImpl;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import watcher.DefaultWatcher;
import watcher.NodeWatcher;
import writer.DataWriter;
import writer.DataWriterImpl;

import java.io.IOException;

public class Starter {

    private static final int SESSION_TIMEOUT = 3000;
    private static final String CONNECT_STRING = "localhost:2181";
    private static final Logger LOGGER = LoggerFactory.getLogger(Starter.class);

    private final ZooKeeper zk;
    private final ShutdownListenerImpl shutdownListener;

    public Starter() throws IOException {
        shutdownListener = new ShutdownListenerImpl();
        zk = new ZooKeeper(CONNECT_STRING, SESSION_TIMEOUT, new DefaultWatcher(shutdownListener));
        LOGGER.info("Connected to zooKeeper");
    }

    public void start() throws IOException {
        DataWriter writer = new DataWriterImpl(shutdownListener);
        new NodeWatcher(zk, writer).start();

        shutdownListener.awaitForShutdown();

    }

    public static void main(String[] args) throws IOException {
        new Starter().start();
    }

}
