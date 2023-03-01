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
package pro.taskana.user.internal;

import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import pro.taskana.user.api.models.User;
import pro.taskana.user.internal.models.UserImpl;

public interface UserMapper {
  @SelectProvider(type = UserMapperSqlProvider.class, method = "findById")
  @Result(property = "id", column = "USER_ID")
  @Result(property = "groups", column = "USER_ID", many = @Many(select = "findGroupsById"))
  @Result(property = "firstName", column = "FIRST_NAME")
  @Result(property = "lastName", column = "LASTNAME")
  @Result(property = "fullName", column = "FULL_NAME")
  @Result(property = "longName", column = "LONG_NAME")
  @Result(property = "email", column = "E_MAIL")
  @Result(property = "phone", column = "PHONE")
  @Result(property = "mobilePhone", column = "MOBILE_PHONE")
  @Result(property = "orgLevel4", column = "ORG_LEVEL_4")
  @Result(property = "orgLevel3", column = "ORG_LEVEL_3")
  @Result(property = "orgLevel2", column = "ORG_LEVEL_2")
  @Result(property = "orgLevel1", column = "ORG_LEVEL_1")
  @Result(property = "data", column = "DATA")
  UserImpl findById(String id);

  @Result(property = "id", column = "USER_ID")
  @Result(property = "groups", column = "USER_ID", many = @Many(select = "findGroupsById"))
  @Result(property = "firstName", column = "FIRST_NAME")
  @Result(property = "lastName", column = "LASTNAME")
  @Result(property = "fullName", column = "FULL_NAME")
  @Result(property = "longName", column = "LONG_NAME")
  @Result(property = "email", column = "E_MAIL")
  @Result(property = "phone", column = "PHONE")
  @Result(property = "mobilePhone", column = "MOBILE_PHONE")
  @Result(property = "orgLevel4", column = "ORG_LEVEL_4")
  @Result(property = "orgLevel3", column = "ORG_LEVEL_3")
  @Result(property = "orgLevel2", column = "ORG_LEVEL_2")
  @Result(property = "orgLevel1", column = "ORG_LEVEL_1")
  @Result(property = "data", column = "DATA")
  @SelectProvider(type = UserMapperSqlProvider.class, method = "findByIds")
  List<UserImpl> findByIds(@Param("ids") Set<String> ids);

  @SelectProvider(type = UserMapperSqlProvider.class, method = "findGroupsById")
  Set<String> findGroupsById(String id);

  @InsertProvider(type = UserMapperSqlProvider.class, method = "insert")
  void insert(User user);

  @InsertProvider(
      type = UserMapperSqlProvider.class,
      method = "insertGroupsOracle",
      databaseId = "oracle")
  @InsertProvider(type = UserMapperSqlProvider.class, method = "insertGroups")
  void insertGroups(User user);

  @UpdateProvider(type = UserMapperSqlProvider.class, method = "update")
  void update(User user);

  @DeleteProvider(type = UserMapperSqlProvider.class, method = "delete")
  void delete(String id);

  @DeleteProvider(type = UserMapperSqlProvider.class, method = "deleteGroups")
  void deleteGroups(String id);
}
