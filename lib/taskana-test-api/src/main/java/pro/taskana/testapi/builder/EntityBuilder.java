/*-
 * #%L
 * pro.taskana:taskana-test-api
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
package pro.taskana.testapi.builder;

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.security.auth.Subject;

import pro.taskana.common.api.security.UserPrincipal;

public interface EntityBuilder<EntityT, ServiceT> {

  EntityT buildAndStore(ServiceT service) throws Exception;

  default EntityT buildAndStore(ServiceT service, String userId) throws Exception {
    return execAsUser(userId, () -> buildAndStore(service));
  }

  private <T> T execAsUser(String userId, PrivilegedExceptionAction<T> runnable)
      throws PrivilegedActionException {
    Subject subject = new Subject();
    subject.getPrincipals().add(new UserPrincipal(userId));

    return Subject.doAs(subject, runnable);
  }

  interface SummaryEntityBuilder<SummaryEntityT, EntityT extends SummaryEntityT, ServiceT>
      extends EntityBuilder<EntityT, ServiceT> {
    SummaryEntityT entityToSummary(EntityT entity);

    default SummaryEntityT buildAndStoreAsSummary(ServiceT service) throws Exception {
      return entityToSummary(buildAndStore(service));
    }

    default SummaryEntityT buildAndStoreAsSummary(ServiceT service, String userId)
        throws Exception {
      return entityToSummary(buildAndStore(service, userId));
    }
  }
}
