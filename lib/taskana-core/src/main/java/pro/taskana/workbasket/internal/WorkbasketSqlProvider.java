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
package pro.taskana.workbasket.internal;

import static pro.taskana.common.internal.util.SqlProviderUtil.CLOSING_SCRIPT_TAG;
import static pro.taskana.common.internal.util.SqlProviderUtil.DB2_WITH_UR;
import static pro.taskana.common.internal.util.SqlProviderUtil.OPENING_SCRIPT_TAG;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import pro.taskana.common.internal.util.Pair;

public class WorkbasketSqlProvider {

  private static final List<Pair<String, String>> COLUMNS =
      Arrays.asList(
          Pair.of("ID", "#{workbasket.id}"),
          Pair.of("KEY", "#{workbasket.key}"),
          Pair.of("CREATED", "#{workbasket.created}"),
          Pair.of("MODIFIED", "#{workbasket.modified}"),
          Pair.of("NAME", "#{workbasket.name}"),
          Pair.of("DOMAIN", "#{workbasket.domain}"),
          Pair.of("TYPE", "#{workbasket.type}"),
          Pair.of("DESCRIPTION", "#{workbasket.description}"),
          Pair.of("OWNER", "#{workbasket.owner}"),
          Pair.of("CUSTOM_1", "#{workbasket.custom1}"),
          Pair.of("CUSTOM_2", "#{workbasket.custom2}"),
          Pair.of("CUSTOM_3", "#{workbasket.custom3}"),
          Pair.of("CUSTOM_4", "#{workbasket.custom4}"),
          Pair.of("ORG_LEVEL_1", "#{workbasket.orgLevel1}"),
          Pair.of("ORG_LEVEL_2", "#{workbasket.orgLevel2}"),
          Pair.of("ORG_LEVEL_3", "#{workbasket.orgLevel3}"),
          Pair.of("ORG_LEVEL_4", "#{workbasket.orgLevel4}"),
          Pair.of("MARKED_FOR_DELETION", "#{workbasket.markedForDeletion}"));

  private WorkbasketSqlProvider() {}

  public static String findById() {
    return OPENING_SCRIPT_TAG
        + "SELECT "
        + commonSelectFields(false)
        + " FROM WORKBASKET WHERE ID = #{id}"
        + DB2_WITH_UR
        + CLOSING_SCRIPT_TAG;
  }

  public static String findSummaryById() {
    return OPENING_SCRIPT_TAG
        + "SELECT "
        + commonSelectFields(true)
        + " FROM WORKBASKET WHERE ID = #{id} "
        + DB2_WITH_UR
        + CLOSING_SCRIPT_TAG;
  }

  public static String findByKeyAndDomain() {
    return OPENING_SCRIPT_TAG
        + "SELECT "
        + commonSelectFields(false)
        + " FROM WORKBASKET WHERE UPPER(KEY) = UPPER(#{key}) and UPPER(DOMAIN) = UPPER(#{domain}) "
        + DB2_WITH_UR
        + CLOSING_SCRIPT_TAG;
  }

  public static String findDistributionTargets() {
    return OPENING_SCRIPT_TAG
        + "SELECT "
        + commonSelectFields(true)
        + " FROM WORKBASKET "
        + "WHERE ID IN (SELECT TARGET_ID FROM DISTRIBUTION_TARGETS WHERE SOURCE_ID = #{id}) "
        + DB2_WITH_UR
        + CLOSING_SCRIPT_TAG;
  }

  public static String findDistributionSources() {
    return OPENING_SCRIPT_TAG
        + "SELECT "
        + commonSelectFields(true)
        + " FROM WORKBASKET "
        + "WHERE ID IN (SELECT SOURCE_ID FROM DISTRIBUTION_TARGETS WHERE TARGET_ID = #{id}) "
        + "<if test=\"_databaseId == 'db2'\">with UR </if> "
        + CLOSING_SCRIPT_TAG;
  }

  public static String findAll() {
    return OPENING_SCRIPT_TAG
        + "SELECT * FROM WORKBASKET ORDER BY ID "
        + "<if test=\"_databaseId == 'db2'\">with UR </if> "
        + CLOSING_SCRIPT_TAG;
  }

  public static String insert() {
    return OPENING_SCRIPT_TAG
        + "INSERT INTO WORKBASKET ("
        + commonSelectFields(false)
        + ") "
        + "VALUES ("
        + valueReferences()
        + ") "
        + CLOSING_SCRIPT_TAG;
  }

  public static String update() {
    return "UPDATE WORKBASKET "
        + "SET "
        + updateSetStatement(false)
        + " WHERE id = #{workbasket.id}";
  }

  public static String updateByKeyAndDomain() {
    return "UPDATE WORKBASKET "
        + "SET "
        + updateSetStatement(true)
        + " WHERE KEY = #{workbasket.key} AND DOMAIN = #{workbasket.domain}";
  }

  public static String delete() {
    return "DELETE FROM WORKBASKET where id = #{id}";
  }

  private static String updateSetStatement(boolean byKeyAndDomain) {
    return COLUMNS.stream()
        .filter(
            col -> {
              if (byKeyAndDomain) {
                String name = col.getLeft();
                return !(name.contains("ID")
                    || name.contains("KEY")
                    || name.contains("DOMAIN")
                    || name.contains("CREATED"));
              } else {
                return true;
              }
            })
        .map(col -> col.getLeft() + " = " + col.getRight())
        .collect(Collectors.joining(", "));
  }

  private static String commonSelectFields(boolean excludeMarkedForDeletion) {
    int limit = COLUMNS.size();
    if (excludeMarkedForDeletion) {
      limit -= 1;
    }
    return COLUMNS.stream().limit(limit).map(Pair::getLeft).collect(Collectors.joining(", "));
  }

  private static String valueReferences() {
    return COLUMNS.stream().map(Pair::getRight).collect(Collectors.joining(", "));
  }
}
