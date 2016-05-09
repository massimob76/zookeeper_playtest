package watcher;

import listener.ShutdownListener;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DefaultWatcher implements Watcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWatcher.class);

    private final ShutdownListener shutdownListener;

    public DefaultWatcher(ShutdownListener shutdownListener) {
        this.shutdownListener = shutdownListener;
    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getState()) {
            case Disconnected:
            case SyncConnected:
                LOGGER.info("Received event state {}", event.getState());
                break;
            case Expired:
                LOGGER.error("client session expired, triggering shutdown");
                shutdownListener.shutDown();
                break;
            default:
                LOGGER.warn("not sure what to do for state {}", event.getState());
                break;

        }
    }

}
