/*-
 * #%L
 * pro.taskana:taskana-rest-spring
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
package pro.taskana.user.rest;

import pro.taskana.spi.user.api.RefreshUserPostprocessor;
import pro.taskana.user.api.models.User;

public class FirstRefreshUserPostprocessor implements RefreshUserPostprocessor {
  @Override
  public User processUserAfterRefresh(User userToProcess) {
    if (userToProcess.getId().equals("user-2-2")) {
      userToProcess.setOrgLevel1("First");
    }
    return userToProcess;
  }
}
