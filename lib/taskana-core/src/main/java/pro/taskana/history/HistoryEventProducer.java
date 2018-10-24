package pro.taskana.history;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.history.api.TaskanaHistory;
import pro.taskana.history.api.TaskanaHistoryEvent;

/**
 * Creates events and emits them to the registered history service providers.
 */
public final class HistoryEventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryEventProducer.class);

    TaskanaEngineConfiguration taskanaEngineConfiguration;
    private static HistoryEventProducer emitterInstance;
    private ServiceLoader<TaskanaHistory> serviceLoader;
    private boolean enabled = false;

    public static synchronized HistoryEventProducer getInstance(TaskanaEngineConfiguration taskanaEngineConfiguration) {
        if (emitterInstance == null) {
            emitterInstance = new HistoryEventProducer(taskanaEngineConfiguration);
        }
        return emitterInstance;
    }

    public static boolean isHistoryEnabled() {
        return getInstance(null).isEnabled();
    }

    private HistoryEventProducer(TaskanaEngineConfiguration taskanaEngineConfiguration) {
        this.taskanaEngineConfiguration = taskanaEngineConfiguration;
        serviceLoader = ServiceLoader.load(TaskanaHistory.class);
        Iterator<TaskanaHistory> serviceIterator = serviceLoader.iterator();
        while (serviceIterator.hasNext()) {
            TaskanaHistory history = serviceIterator.next();
            history.initialize(taskanaEngineConfiguration);
            LOGGER.info("Registered history provider: {}", history.getClass().getName());
            enabled = true;
        }
        if (!enabled) {
            LOGGER.info("No history provider found. Running without history.");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void createEvent(TaskanaHistoryEvent event) {
        LOGGER.debug("Sending event to history service providers: {}", event);
        serviceLoader.forEach(historyProvider -> {
            historyProvider.create(event);
        });
    }
}
