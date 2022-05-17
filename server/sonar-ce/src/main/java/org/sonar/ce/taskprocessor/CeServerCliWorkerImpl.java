/*
 * SonarQube
 * Copyright (C) 2009-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.ce.taskprocessor;

import com.google.common.collect.ImmutableMap;
import org.sonar.api.utils.MessageException;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.ce.monitoring.CEQueueStatus;
import org.sonar.ce.queue.CeQueueImpl;
import org.sonar.ce.queue.CeTask;
import org.sonar.ce.queue.CeTaskResult;
import org.sonar.ce.queue.InternalCeQueue;
import org.sonar.core.util.logs.Profiler;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.ce.CeActivityDto;
import org.sonar.db.ce.CeQueueDao;
import org.sonar.db.ce.CeQueueDto;
import org.sonar.db.component.ComponentDto;
import org.sonar.server.organization.DefaultOrganizationProvider;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.sonar.ce.taskprocessor.CeWorker.Result.*;

public class CeServerCliWorkerImpl implements CeWorker {

  private static final Logger LOG = Loggers.get(CeWorkerImpl.class);

  private final int ordinal;
  private final String workUuid;
  private final String componentUUID;
  private final InternalCeQueue queue;
  private final CeTaskProcessorRepository taskProcessorRepository;
  private final EnabledCeWorkerController enabledCeWorkerController;
  private final List<ExecutionListener> listeners;
  private final DefaultOrganizationProvider defaultOrganizationProvider;
  private final CEQueueStatus queueStatus;
  private final DbClient dbClient;

  private static final int MAX_EXECUTION_COUNT = 1;

  public CeServerCliWorkerImpl(int ordinal, String workUuid,
                               String componentUUID, InternalCeQueue queue, CeTaskProcessorRepository taskProcessorRepository,
                               EnabledCeWorkerController enabledCeWorkerController,
                               DefaultOrganizationProvider defaultOrganizationProvider,
                               CEQueueStatus queueStatus,
                               DbClient dbClient,
                               ExecutionListener... listeners) {
    this.ordinal = checkOrdinal(ordinal);
    this.workUuid = workUuid;
    this.componentUUID = componentUUID;
    this.queue = queue;
    this.taskProcessorRepository = taskProcessorRepository;
    this.enabledCeWorkerController = enabledCeWorkerController;
    this.defaultOrganizationProvider = defaultOrganizationProvider;
    this.queueStatus = queueStatus;
    this.dbClient = dbClient;
    this.listeners = Arrays.asList(listeners);
  }

  private static int checkOrdinal(int ordinal) {
    checkArgument(ordinal >= 0, "Ordinal must be >= 0");
    return ordinal;
  }

  @Override
  public Result call() throws Exception {
    long startTime = System.currentTimeMillis();
    LOG.info("CeServerCliWorkerImpl任务执行开始时间:" + startTime);
    Result result = withCustomizedThreadName(this::findAndProcessTask);
    long endTime = System.currentTimeMillis();
    LOG.info("CeServerCliWorkerImpl任务执行完成时间:" + endTime + "，耗时：" + (endTime-startTime));
    return result;
  }

  private <T> T withCustomizedThreadName(Supplier<T> supplier) {
    Thread currentThread = Thread.currentThread();
    String oldName = currentThread.getName();
    try {
      currentThread.setName(String.format("Worker %s (UUID=%s) on %s", getOrdinal(), getUUID(), oldName));
      return supplier.get();
    } finally {
      currentThread.setName(oldName);
    }
  }

  private Result findAndProcessTask() {
    if (!enabledCeWorkerController.isEnabled(this)) {
      return DISABLED;
    }
    Optional<CeTask> ceTask = tryAndFindTaskToExecute();
    if (!ceTask.isPresent()) {
      return NO_TASK;
    }

    try (EnabledCeWorkerController.ProcessingRecorderHook processing = enabledCeWorkerController.registerProcessingFor(this)) {
      executeTask(ceTask.get());
      //执行任务
    } catch (Exception e) {
      LOG.error(format("An error occurred while executing task with uuid '%s'", ceTask.get().getUuid()), e);
    }
    return TASK_PROCESSED;
  }

  private Optional<CeTask> tryAndFindTaskToExecute() {
    try {
      return this.peek(workUuid);
    } catch (Exception e) {
      LOG.error("Failed to pop the queue of analysis reports", e);
    }
    return Optional.empty();
  }

  public Optional<CeTask> peek(String workerUuid) {
    requireNonNull(workerUuid, "workerUuid can't be null");

    try (DbSession dbSession = dbClient.openSession(false)) {
      CeQueueDao ceQueueDao = dbClient.ceQueueDao();
      int i = ceQueueDao.resetToPendingForWorker(dbSession, workerUuid);
      if (i > 0) {
        LOG.debug("{} in progress tasks reset for worker uuid {}", i, workerUuid);
      }
      Optional<CeQueueDto> dto = ceQueueDao.peek(dbSession, workerUuid, MAX_EXECUTION_COUNT);
      CeTask task = null;
      if (dto.isPresent()) {
        task = loadTask(dbSession, dto.get());
        queueStatus.addInProgress();
      }
      return Optional.ofNullable(task);
    }
  }

  protected CeTask loadTask(DbSession dbSession, CeQueueDto dto) {
    if (dto.getComponentUuid() == null) {
      return new CeQueueImpl.CeQueueDtoToCeTask(defaultOrganizationProvider.get().getUuid()).apply(dto);
    }
    com.google.common.base.Optional<ComponentDto> componentDto = dbClient.componentDao().selectByUuid(dbSession, dto.getComponentUuid());
    if (componentDto.isPresent()) {
      return new CeQueueImpl.CeQueueDtoToCeTask(defaultOrganizationProvider.get().getUuid(), ImmutableMap.of(dto.getComponentUuid(), componentDto.get())).apply(dto);
    }
    return new CeQueueImpl.CeQueueDtoToCeTask(defaultOrganizationProvider.get().getUuid()).apply(dto);
  }

  @Override
  public int getOrdinal() {
    return ordinal;
  }

  @Override
  public String getUUID() {
    return workUuid;
  }

  private void executeTask(CeTask task) {
    callListeners(t -> t.onStart(task));
    Profiler ceProfiler = startActivityProfiler(task);

    CeActivityDto.Status status = CeActivityDto.Status.FAILED;
    CeTaskResult taskResult = null;
    Throwable error = null;
    try {
      // TODO delegate the message to the related task processor, according to task type
      Optional<CeTaskProcessor> taskProcessor = taskProcessorRepository.getForCeTask(task);
      if (taskProcessor.isPresent()) {
        taskResult = taskProcessor.get().process(task);
        status = CeActivityDto.Status.SUCCESS;
      } else {
        LOG.error("No CeTaskProcessor is defined for task of type {}. Plugin configuration may have changed", task.getType());
        status = CeActivityDto.Status.FAILED;
      }
    } catch (MessageException e) {
      // error
      error = e;
    } catch (Throwable e) {
      // error
      LOG.error(format("Failed to execute task %s", task.getUuid()), e);
      error = e;
    } finally {
      finalizeTask(task, ceProfiler, status, taskResult, error);
    }
  }

  private void callListeners(Consumer<ExecutionListener> call) {
    listeners.forEach(listener -> {
      try {
        call.accept(listener);
      } catch (Throwable t) {
        LOG.error(format("Call to listener %s failed.", listener.getClass().getSimpleName()), t);
      }
    });
  }

  private void finalizeTask(CeTask task, Profiler ceProfiler, CeActivityDto.Status status,
                            @Nullable CeTaskResult taskResult, @Nullable Throwable error) {
    try {
      queue.remove(task, status, taskResult, error);
    } catch (Exception e) {
      String errorMessage = format("Failed to finalize task with uuid '%s' and persist its state to db", task.getUuid());
      if (error instanceof MessageException) {
        LOG.error(format("%s. Task failed with MessageException \"%s\"", errorMessage, error.getMessage()), e);
      } else {
        LOG.error(errorMessage, e);
      }
    } finally {
      // finalize
      stopActivityProfiler(ceProfiler, task, status);
      callListeners(t -> t.onEnd(task, status, taskResult, error));
    }
  }

  private static Profiler startActivityProfiler(CeTask task) {
    Profiler profiler = Profiler.create(LOG);
    addContext(profiler, task);
    LOG.info("初始化profiler并，开始执行任务");
    return profiler.startInfo("Execute task");
  }

  private static void stopActivityProfiler(Profiler profiler, CeTask task, CeActivityDto.Status status) {
    addContext(profiler, task);
    if (status == CeActivityDto.Status.FAILED) {
      profiler.stopError("Executed task");
    } else {
      profiler.stopInfo("Executed task");
    }
  }

  private static void addContext(Profiler profiler, CeTask task) {
    profiler
            .logTimeLast(true)
            .addContext("project", task.getComponentKey())
            .addContext("type", task.getType())
            .addContext("id", task.getUuid());
    String submitterLogin = task.getSubmitterLogin();
    if (submitterLogin != null) {
      profiler.addContext("submitter", submitterLogin);
    }
  }
}
