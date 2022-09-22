package pro.taskana.user.internal;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import pro.taskana.user.api.models.User;
import pro.taskana.user.internal.models.UserImpl;

public interface UserMapper {
  @SelectProvider(type = UserMapperSqlProvider.class, method = "findById")
  @Result(property = "id", column = "USER_ID")
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
  UserImpl findById(@Param("id") String id);

  @InsertProvider(type = UserMapperSqlProvider.class, method = "insert")
  void insert(User user);

  @UpdateProvider(type = UserMapperSqlProvider.class, method = "update")
  void update(User user);

  @DeleteProvider(type = UserMapperSqlProvider.class, method = "delete")
  void delete(String id);
}
