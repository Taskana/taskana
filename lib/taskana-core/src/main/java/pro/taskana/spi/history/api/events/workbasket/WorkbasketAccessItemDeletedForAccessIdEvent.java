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
package pro.taskana.spi.history.api.events.workbasket;

import java.time.Instant;

import pro.taskana.workbasket.api.models.Workbasket;

public class WorkbasketAccessItemDeletedForAccessIdEvent extends WorkbasketHistoryEvent {

  public WorkbasketAccessItemDeletedForAccessIdEvent(
      String id, Workbasket workbasket, String userId) {
    super(id, workbasket, userId, null);
    eventType = WorkbasketHistoryEventType.ACCESS_ITEM_DELETED_FOR_ACCESS_ID.getName();
    created = Instant.now();
  }
}
