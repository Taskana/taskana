/*-
 * #%L
 * pro.taskana:taskana-core
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.common.internal.jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RealClock implements Clock {

  private final long initialStartDelay;
  private final long period;
  private final TimeUnit periodTimeUnit;
  private final List<ClockListener> listeners = Collections.synchronizedList(new ArrayList<>());
  private final ScheduledExecutorService timerService =
      Executors.newSingleThreadScheduledExecutor();

  public RealClock(long initialStartDelay, long period, TimeUnit periodTimeUnit) {
    this.initialStartDelay = initialStartDelay;
    this.period = period;
    this.periodTimeUnit = periodTimeUnit;
  }

  @Override
  public void register(ClockListener listener) {
    listeners.add(listener);
  }

  @Override
  public void start() {
    timerService.scheduleAtFixedRate(
        this::reportTimeElapse, initialStartDelay, period, periodTimeUnit);
  }

  @Override
  public void stop() {
    timerService.shutdown();
  }

  private void reportTimeElapse() {
    listeners.forEach(ClockListener::timeElapsed);
  }
}
