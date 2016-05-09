package listener;

public interface ShutdownListener {

    void awaitForShutdown();

    void shutDown();

    void registerShutdownAction(Runnable runnable);
}
