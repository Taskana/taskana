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
package pro.taskana.task.internal;

import static pro.taskana.common.internal.util.SqlProviderUtil.CLOSING_SCRIPT_TAG;
import static pro.taskana.common.internal.util.SqlProviderUtil.CLOSING_WHERE_TAG;
import static pro.taskana.common.internal.util.SqlProviderUtil.DB2_WITH_UR;
import static pro.taskana.common.internal.util.SqlProviderUtil.OPENING_SCRIPT_TAG;
import static pro.taskana.common.internal.util.SqlProviderUtil.OPENING_WHERE_TAG;
import static pro.taskana.common.internal.util.SqlProviderUtil.whereIn;

public class ObjectReferenceQuerySqlProvider {

  private ObjectReferenceQuerySqlProvider() {}

  public static String queryObjectReferences() {
    return OPENING_SCRIPT_TAG
        + "SELECT ID, COMPANY, SYSTEM, SYSTEM_INSTANCE, TYPE, VALUE "
        + "FROM OBJECT_REFERENCE "
        + OPENING_WHERE_TAG
        + commonObjectReferenceWhereStatement()
        + CLOSING_WHERE_TAG
        + "ORDER BY COMPANY ASC, SYSTEM ASC, SYSTEM_INSTANCE ASC, TYPE ASC, VALUE ASC"
        + DB2_WITH_UR
        + CLOSING_SCRIPT_TAG;
  }

  public static String countQueryObjectReferences() {
    return OPENING_SCRIPT_TAG
        + "SELECT COUNT(ID) "
        + "FROM OBJECT_REFERENCE "
        + OPENING_WHERE_TAG
        + commonObjectReferenceWhereStatement()
        + CLOSING_WHERE_TAG
        + DB2_WITH_UR
        + CLOSING_SCRIPT_TAG;
  }

  public static String queryObjectReferenceColumnValues() {
    return OPENING_SCRIPT_TAG
        + "SELECT DISTINCT ${columnName} "
        + "FROM OBJECT_REFERENCE "
        + OPENING_WHERE_TAG
        + commonObjectReferenceWhereStatement()
        + CLOSING_WHERE_TAG
        + DB2_WITH_UR
        + CLOSING_SCRIPT_TAG;
  }

  private static String commonObjectReferenceWhereStatement() {
    StringBuilder sb = new StringBuilder();
    whereIn("company", "COMPANY", sb);
    whereIn("system", "SYSTEM", sb);
    whereIn("systemInstance", "SYSTEM_INSTANCE", sb);
    whereIn("type", "TYPE", sb);
    whereIn("value", "VALUE", sb);
    return sb.toString();
  }
}
