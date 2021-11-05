package pro.taskana.common.internal;

import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface ConfigurationMapper {

  @Select(
      "<script> SELECT ENFORCE_SECURITY FROM CONFIGURATION "
          + "<if test='lockForUpdate == true'>"
          + "FETCH FIRST ROW ONLY FOR UPDATE "
          + "<if test=\"_databaseId == 'db2'\">WITH RS USE AND KEEP UPDATE LOCKS </if> "
          + "</if>"
          + "</script>")
  Boolean isSecurityEnabled(boolean lockForUpdate);

  @Update("UPDATE CONFIGURATION SET ENFORCE_SECURITY = #{securityEnabled} WHERE NAME = 'MASTER'")
  void setSecurityEnabled(@Param("securityEnabled") boolean securityEnabled);

  @Select(
      "<script> SELECT CUSTOM_ATTRIBUTES FROM CONFIGURATION "
          + "<if test='lockForUpdate == true'>"
          + "FETCH FIRST ROW ONLY FOR UPDATE "
          + "<if test=\"_databaseId == 'db2'\">WITH RS USE AND KEEP UPDATE LOCKS </if> "
          + "</if>"
          + "</script>")
  Map<String, Object> getAllCustomAttributes(boolean lockForUpdate);

  @Update("UPDATE CONFIGURATION SET CUSTOM_ATTRIBUTES = #{customAttributes} WHERE NAME = 'MASTER'")
  void setAllCustomAttributes(@Param("customAttributes") Map<String, ?> customAttributes);
}
