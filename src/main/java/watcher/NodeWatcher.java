package watcher;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import writer.DataWriter;

public class NodeWatcher implements Watcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeWatcher.class);
    private static final String PATH = "/test";

    private final ZooKeeper zk;
    private final DataWriter writer;

    public NodeWatcher(ZooKeeper zk, DataWriter writer) {
        this.zk = zk;
        this.writer = writer;
    }

    public void start() {
        setWatcher();
    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case NodeCreated:
                LOGGER.debug("node create");
                getAndWriteData();
                break;
            case NodeDeleted:
                LOGGER.debug("node deleted");
                break;
            case NodeDataChanged:
                LOGGER.debug("node data changed");
                getAndWriteData();
                break;
            default:
                LOGGER.info("Event type changed to {}", event.getType());
        }
        setWatcher();

    }

    private void setWatcher() {
        try {
            LOGGER.debug("setting watcher for node path {}", PATH);
            zk.exists(PATH, this);
        } catch (KeeperException e) {
            LOGGER.error("unexpected exception {}", e);
        } catch (InterruptedException e) {
            // ignored
        }
    }

    private void getAndWriteData() {
        try {
            String data = new String(zk.getData(PATH, false, null));
            LOGGER.debug("retrieved node data {}", data);
            writer.write(data);
        } catch (KeeperException e) {
            LOGGER.error("Exception was throw possibly node has been deleted? {}", e);
        } catch (InterruptedException e) {
            // ignored
        }
    }
}
