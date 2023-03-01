/*-
 * #%L
 * pro.taskana:taskana-common-security
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
package pro.taskana.common.api.security;

import java.util.List;

/**
 * Provides the context information about the current (calling) user. The context is gathered from
 * the JAAS subject.
 */
public interface CurrentUserContext {

  /**
   * Returns the userid of the current user.
   *
   * @return String the userid. null if there is no JAAS subject.
   */
  public String getUserid();

  /**
   * Returns all groupIds of the current user.
   *
   * @return list containing all groupIds of the current user. Empty if the current user belongs to
   *     no groups or no JAAS Subject set.
   */
  public List<String> getGroupIds();

  /**
   * Returns all accessIds of the current user. This combines the userId and all groupIds of the
   * current user.
   *
   * @return list containing all accessIds of the current user. Empty if there is no JAAS subject.
   */
  public List<String> getAccessIds();
}
