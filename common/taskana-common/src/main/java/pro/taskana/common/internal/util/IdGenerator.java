/*-
 * #%L
 * pro.taskana:taskana-common
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
package pro.taskana.common.internal.util;

import java.util.UUID;

/** This class contains util methods for generating ids. */
public final class IdGenerator {

  public static final String ID_PREFIX_CLASSIFICATION = "CLI";

  public static final String ID_PREFIX_WORKBASKET = "WBI";
  public static final String ID_PREFIX_WORKBASKET_AUTHORIZATION = "WAI";

  public static final String ID_PREFIX_TASK = "TKI";
  public static final String ID_PREFIX_EXT_TASK = "ETI";
  public static final String ID_PREFIX_BUSINESS_PROCESS = "BPI";
  public static final String ID_PREFIX_ATTACHMENT = "TAI";
  public static final String ID_PREFIX_OBJECT_REFERENCE = "ORI";

  public static final String ID_PREFIX_TASK_COMMENT = "TCI";

  public static final String ID_PREFIX_CLASSIFICATION_HISTORY_EVENT = "CHI";
  public static final String ID_PREFIX_WORKBASKET_HISTORY_EVENT = "WHI";
  public static final String ID_PREFIX_TASK_HISTORY_EVENT = "THI";

  private static final String SEPARATOR = ":";

  // disable initialization
  private IdGenerator() {}

  /**
   * This method create an id with an specific prefix.
   *
   * @param prefix only 3 characters!
   * @return a String with a length of 40 characters
   */
  public static String generateWithPrefix(String prefix) {
    return prefix + SEPARATOR + UUID.randomUUID();
  }
}
