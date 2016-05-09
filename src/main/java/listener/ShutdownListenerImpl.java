package listener;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ShutdownListenerImpl implements ShutdownListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownListenerImpl.class);

    private boolean isShutdown = false;
    private List<Runnable> shutdownActions = new CopyOnWriteArrayList<>();

    public ShutdownListenerImpl() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutDown()));
    }


    @Override
    public void awaitForShutdown() {
        synchronized (this) {
            while (!isShutdown) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    // ignored
                }
            }
        }
    }

    @Override
    public void shutDown() {
        LOGGER.info("starting shutdown operations");
        shutdownActions.forEach(runnable -> runnable.run());
        synchronized (this) {
            isShutdown = true;
            notify();
        }
    }

    @Override
    public void registerShutdownAction(Runnable runnable) {
        shutdownActions.add(runnable);
    }
}
