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
package pro.taskana.common.internal;

import static pro.taskana.common.internal.util.SqlProviderUtil.CLOSING_SCRIPT_TAG;
import static pro.taskana.common.internal.util.SqlProviderUtil.OPENING_SCRIPT_TAG;

import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface ConfigurationMapper {

  @Select(
      databaseId = "oracle",
      value =
          OPENING_SCRIPT_TAG
              + "SELECT c1.ENFORCE_SECURITY FROM CONFIGURATION c1 "
              + "WHERE c1.rowid = (SELECT c2.rowid FROM CONFIGURATION c2 FETCH FIRST 1 ROWS ONLY) "
              + "<if test='lockForUpdate == true'>"
              + "FOR UPDATE"
              + "</if>"
              + CLOSING_SCRIPT_TAG)
  @Select(
      OPENING_SCRIPT_TAG
          + "SELECT ENFORCE_SECURITY FROM CONFIGURATION "
          + "<if test='lockForUpdate == true'>"
          + "FETCH FIRST ROW ONLY FOR UPDATE "
          + "<if test=\"_databaseId == 'db2'\">WITH RS USE AND KEEP UPDATE LOCKS </if> "
          + "</if>"
          + CLOSING_SCRIPT_TAG)
  Boolean isSecurityEnabled(boolean lockForUpdate);

  @Update("UPDATE CONFIGURATION SET ENFORCE_SECURITY = #{securityEnabled} WHERE NAME = 'MASTER'")
  void setSecurityEnabled(@Param("securityEnabled") boolean securityEnabled);

  @Select(
      databaseId = "oracle",
      value =
          OPENING_SCRIPT_TAG
              + "SELECT c1.CUSTOM_ATTRIBUTES FROM CONFIGURATION c1 "
              + "WHERE c1.rowid = (SELECT c2.rowid FROM CONFIGURATION c2 FETCH FIRST 1 ROWS ONLY) "
              + "<if test='lockForUpdate == true'>"
              + "FOR UPDATE"
              + "</if>"
              + CLOSING_SCRIPT_TAG)
  @Select(
      OPENING_SCRIPT_TAG
          + "SELECT CUSTOM_ATTRIBUTES FROM CONFIGURATION "
          + "<if test='lockForUpdate == true'>"
          + "FETCH FIRST ROW ONLY FOR UPDATE"
          + "<if test=\"_databaseId == 'db2'\">WITH RS USE AND KEEP UPDATE LOCKS </if> "
          + "</if>"
          + CLOSING_SCRIPT_TAG)
  Map<String, Object> getAllCustomAttributes(boolean lockForUpdate);

  @Update("UPDATE CONFIGURATION SET CUSTOM_ATTRIBUTES = #{customAttributes} WHERE NAME = 'MASTER'")
  void setAllCustomAttributes(@Param("customAttributes") Map<String, ?> customAttributes);
}
