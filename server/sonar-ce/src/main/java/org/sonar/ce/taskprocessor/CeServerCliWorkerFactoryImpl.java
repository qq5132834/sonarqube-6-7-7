package org.sonar.ce.taskprocessor;

import org.sonar.ce.monitoring.CEQueueStatus;
import org.sonar.ce.queue.InternalCeQueue;
import org.sonar.core.util.UuidFactory;
import org.sonar.db.DbClient;
import org.sonar.server.organization.DefaultOrganizationProvider;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.collect.ImmutableSet.copyOf;

public class CeServerCliWorkerFactoryImpl {
    private final UuidFactory uuidFactory;
    private final Set<String> ceWorkerUUIDs = new HashSet<>();
    private final InternalCeQueue queue;
    private final CeTaskProcessorRepository taskProcessorRepository;
    private final EnabledCeWorkerController enabledCeWorkerController;
    private final CeWorker.ExecutionListener[] executionListeners;

    private final DefaultOrganizationProvider defaultOrganizationProvider;
    private final CEQueueStatus queueStatus;
    private final DbClient dbClient;

    /**
     * Used by Pico when there is no {@link CeWorker.ExecutionListener} in the container.
     */
    public CeServerCliWorkerFactoryImpl(InternalCeQueue queue, CeTaskProcessorRepository taskProcessorRepository,
                                        UuidFactory uuidFactory, EnabledCeWorkerController enabledCeWorkerController,
                                        DbClient dbClient,CEQueueStatus queueStatus,DefaultOrganizationProvider defaultOrganizationProvider) {
        this(queue, taskProcessorRepository, uuidFactory, enabledCeWorkerController, new CeWorker.ExecutionListener[0], dbClient, queueStatus, defaultOrganizationProvider);
    }

    public CeServerCliWorkerFactoryImpl(InternalCeQueue queue,
                                        CeTaskProcessorRepository taskProcessorRepository,
                                        UuidFactory uuidFactory,
                                        EnabledCeWorkerController enabledCeWorkerController,
                                        CeWorker.ExecutionListener[] executionListeners,
                                        DbClient dbClient,
                                        CEQueueStatus queueStatus,
                                        DefaultOrganizationProvider defaultOrganizationProvider) {
        this.queue = queue;
        this.taskProcessorRepository = taskProcessorRepository;
        this.uuidFactory = uuidFactory;
        this.enabledCeWorkerController = enabledCeWorkerController;
        this.executionListeners = executionListeners;
        this.dbClient = dbClient;
        this.queueStatus = queueStatus;
        this.defaultOrganizationProvider = defaultOrganizationProvider;
    }

    public CeWorker create(int ordinal, String componentUUID) {
        String uuid = uuidFactory.create();
        ceWorkerUUIDs.add(uuid);
        return new CeServerCliWorkerImpl(ordinal,
                                        uuid,
                                        componentUUID,
                                        queue,
                                        taskProcessorRepository,
                                        enabledCeWorkerController,
                                        defaultOrganizationProvider,
                                        queueStatus,
                                        dbClient,
                                        executionListeners);
    }

    public Set<String> getWorkerUUIDs() {
        return copyOf(ceWorkerUUIDs);
    }
}
