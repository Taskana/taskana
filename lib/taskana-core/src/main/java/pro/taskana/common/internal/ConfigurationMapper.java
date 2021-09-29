package pro.taskana.common.internal;

import java.util.Map;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface ConfigurationMapper {

  @Select("SELECT ENFORCE_SECURITY FROM CONFIGURATION")
  Boolean isSecurityEnabled();

  @Insert("INSERT INTO CONFIGURATION(ENFORCE_SECURITY) VALUES (#{securityEnabled})")
  void setSecurityEnabled(@Param("securityEnabled") boolean securityEnabled);

  @Select("SELECT CUSTOM_ATTRIBUTES FROM TASKANA.CONFIGURATION")
  Map<String, Object> getAllCustomAttributes();

  @Update("UPDATE TASKANA.CONFIGURATION SET CUSTOM_ATTRIBUTES = #{customAttributes}")
  void setAllCustomAttributes(@Param("customAttributes") Map<String, ?> customAttributes);
}
