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
package pro.taskana.common.api;

import pro.taskana.common.internal.jobs.TaskanaJob;

/** Service to manage the {@linkplain TaskanaJob TaskanaJobs}. */
public interface JobService {

  /**
   * Initializes the given {@linkplain ScheduledJob} and inserts it into the database.
   *
   * @param job the {@linkplain ScheduledJob job} to be created
   * @return the created {@linkplain ScheduledJob job}
   */
  ScheduledJob createJob(ScheduledJob job);
}
