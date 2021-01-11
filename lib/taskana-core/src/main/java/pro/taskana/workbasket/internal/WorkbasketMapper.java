package pro.taskana.workbasket.internal;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import pro.taskana.workbasket.internal.models.WorkbasketImpl;
import pro.taskana.workbasket.internal.models.WorkbasketSummaryImpl;

/** This class is the mybatis mapping of workbaskets. */
@SuppressWarnings("checkstyle:LineLength")
public interface WorkbasketMapper {

  @Select(
      "<script>SELECT ID, KEY, CREATED, MODIFIED, NAME, DOMAIN, TYPE, DESCRIPTION, OWNER, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, ORG_LEVEL_1, ORG_LEVEL_2, ORG_LEVEL_3, ORG_LEVEL_4, MARKED_FOR_DELETION FROM WORKBASKET WHERE ID = #{id} "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  @Result(property = "id", column = "ID")
  @Result(property = "key", column = "KEY")
  @Result(property = "created", column = "CREATED")
  @Result(property = "modified", column = "MODIFIED")
  @Result(property = "name", column = "NAME")
  @Result(property = "domain", column = "DOMAIN")
  @Result(property = "type", column = "TYPE")
  @Result(property = "description", column = "DESCRIPTION")
  @Result(property = "owner", column = "OWNER")
  @Result(property = "custom1", column = "CUSTOM_1")
  @Result(property = "custom2", column = "CUSTOM_2")
  @Result(property = "custom3", column = "CUSTOM_3")
  @Result(property = "custom4", column = "CUSTOM_4")
  @Result(property = "orgLevel1", column = "ORG_LEVEL_1")
  @Result(property = "orgLevel2", column = "ORG_LEVEL_2")
  @Result(property = "orgLevel3", column = "ORG_LEVEL_3")
  @Result(property = "orgLevel4", column = "ORG_LEVEL_4")
  @Result(property = "markedForDeletion", column = "MARKED_FOR_DELETION")
  WorkbasketImpl findById(@Param("id") String id);

  @Select(
      "<script>SELECT ID, KEY, CREATED, MODIFIED, NAME, DOMAIN, TYPE, DESCRIPTION, OWNER, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, ORG_LEVEL_1, ORG_LEVEL_2, ORG_LEVEL_3, ORG_LEVEL_4, MARKED_FOR_DELETION FROM WORKBASKET WHERE UPPER(KEY) = UPPER(#{key}) and UPPER(DOMAIN) = UPPER(#{domain}) "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  @Result(property = "id", column = "ID")
  @Result(property = "key", column = "KEY")
  @Result(property = "created", column = "CREATED")
  @Result(property = "modified", column = "MODIFIED")
  @Result(property = "name", column = "NAME")
  @Result(property = "domain", column = "DOMAIN")
  @Result(property = "type", column = "TYPE")
  @Result(property = "description", column = "DESCRIPTION")
  @Result(property = "owner", column = "OWNER")
  @Result(property = "custom1", column = "CUSTOM_1")
  @Result(property = "custom2", column = "CUSTOM_2")
  @Result(property = "custom3", column = "CUSTOM_3")
  @Result(property = "custom4", column = "CUSTOM_4")
  @Result(property = "orgLevel1", column = "ORG_LEVEL_1")
  @Result(property = "orgLevel2", column = "ORG_LEVEL_2")
  @Result(property = "orgLevel3", column = "ORG_LEVEL_3")
  @Result(property = "orgLevel4", column = "ORG_LEVEL_4")
  @Result(property = "markedForDeletion", column = "MARKED_FOR_DELETION")
  WorkbasketImpl findByKeyAndDomain(@Param("key") String key, @Param("domain") String domain);

  @Select(
      "<script>SELECT ID, KEY, NAME, DESCRIPTION, OWNER, DOMAIN, TYPE, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, ORG_LEVEL_1, ORG_LEVEL_2, ORG_LEVEL_3, ORG_LEVEL_4 FROM WORKBASKET WHERE ID IN (SELECT TARGET_ID FROM DISTRIBUTION_TARGETS WHERE SOURCE_ID = #{id}) "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  @Result(property = "id", column = "ID")
  @Result(property = "key", column = "KEY")
  @Result(property = "name", column = "NAME")
  @Result(property = "description", column = "DESCRIPTION")
  @Result(property = "owner", column = "OWNER")
  @Result(property = "domain", column = "DOMAIN")
  @Result(property = "type", column = "TYPE")
  @Result(property = "custom1", column = "CUSTOM_1")
  @Result(property = "custom2", column = "CUSTOM_2")
  @Result(property = "custom3", column = "CUSTOM_3")
  @Result(property = "custom4", column = "CUSTOM_4")
  @Result(property = "orgLevel1", column = "ORG_LEVEL_1")
  @Result(property = "orgLevel2", column = "ORG_LEVEL_2")
  @Result(property = "orgLevel3", column = "ORG_LEVEL_3")
  @Result(property = "orgLevel4", column = "ORG_LEVEL_4")
  List<WorkbasketSummaryImpl> findDistributionTargets(@Param("id") String id);

  @Select(
      "<script>SELECT ID, KEY, NAME, DESCRIPTION, OWNER, DOMAIN, TYPE, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, ORG_LEVEL_1, ORG_LEVEL_2, ORG_LEVEL_3, ORG_LEVEL_4 FROM WORKBASKET "
          + " WHERE ID IN (SELECT SOURCE_ID FROM DISTRIBUTION_TARGETS WHERE TARGET_ID = #{id}) "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  @Result(property = "id", column = "ID")
  @Result(property = "key", column = "KEY")
  @Result(property = "name", column = "NAME")
  @Result(property = "description", column = "DESCRIPTION")
  @Result(property = "owner", column = "OWNER")
  @Result(property = "domain", column = "DOMAIN")
  @Result(property = "type", column = "TYPE")
  @Result(property = "custom1", column = "CUSTOM_1")
  @Result(property = "custom2", column = "CUSTOM_2")
  @Result(property = "custom3", column = "CUSTOM_3")
  @Result(property = "custom4", column = "CUSTOM_4")
  @Result(property = "orgLevel1", column = "ORG_LEVEL_1")
  @Result(property = "orgLevel2", column = "ORG_LEVEL_2")
  @Result(property = "orgLevel3", column = "ORG_LEVEL_3")
  @Result(property = "orgLevel4", column = "ORG_LEVEL_4")
  List<WorkbasketSummaryImpl> findDistributionSources(@Param("id") String id);

  @Select(
      "<script>SELECT ID, KEY, NAME, DESCRIPTION, OWNER, DOMAIN, TYPE, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, ORG_LEVEL_1, ORG_LEVEL_2, ORG_LEVEL_3, ORG_LEVEL_4 FROM WORKBASKET WHERE ID = #{id} "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  @Result(property = "id", column = "ID")
  @Result(property = "key", column = "KEY")
  @Result(property = "name", column = "NAME")
  @Result(property = "description", column = "DESCRIPTION")
  @Result(property = "owner", column = "OWNER")
  @Result(property = "domain", column = "DOMAIN")
  @Result(property = "type", column = "TYPE")
  @Result(property = "custom1", column = "CUSTOM_1")
  @Result(property = "custom2", column = "CUSTOM_2")
  @Result(property = "custom3", column = "CUSTOM_3")
  @Result(property = "custom4", column = "CUSTOM_4")
  @Result(property = "orgLevel1", column = "ORG_LEVEL_1")
  @Result(property = "orgLevel2", column = "ORG_LEVEL_2")
  @Result(property = "orgLevel3", column = "ORG_LEVEL_3")
  @Result(property = "orgLevel4", column = "ORG_LEVEL_4")
  List<WorkbasketSummaryImpl> findSummaryById(@Param("key") String id);

  @Select(
      "<script>SELECT * FROM WORKBASKET ORDER BY id "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  @Result(property = "id", column = "ID")
  @Result(property = "key", column = "KEY")
  @Result(property = "name", column = "NAME")
  @Result(property = "description", column = "DESCRIPTION")
  @Result(property = "owner", column = "OWNER")
  @Result(property = "domain", column = "DOMAIN")
  @Result(property = "type", column = "TYPE")
  @Result(property = "custom1", column = "CUSTOM_1")
  @Result(property = "custom2", column = "CUSTOM_2")
  @Result(property = "custom3", column = "CUSTOM_3")
  @Result(property = "custom4", column = "CUSTOM_4")
  @Result(property = "orgLevel1", column = "ORG_LEVEL_1")
  @Result(property = "orgLevel2", column = "ORG_LEVEL_2")
  @Result(property = "orgLevel3", column = "ORG_LEVEL_3")
  @Result(property = "orgLevel4", column = "ORG_LEVEL_4")
  List<WorkbasketSummaryImpl> findAll();

  @Insert(
      "<script>INSERT INTO WORKBASKET (ID, KEY, CREATED, MODIFIED, NAME, DOMAIN, TYPE, DESCRIPTION, OWNER, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, ORG_LEVEL_1, ORG_LEVEL_2, ORG_LEVEL_3, ORG_LEVEL_4, MARKED_FOR_DELETION) VALUES (#{workbasket.id}, #{workbasket.key}, #{workbasket.created}, #{workbasket.modified}, #{workbasket.name}, #{workbasket.domain}, #{workbasket.type}, #{workbasket.description}, #{workbasket.owner}, #{workbasket.custom1}, #{workbasket.custom2}, #{workbasket.custom3}, #{workbasket.custom4}, #{workbasket.orgLevel1}, #{workbasket.orgLevel2}, #{workbasket.orgLevel3}, #{workbasket.orgLevel4}, #{workbasket.markedForDeletion}) "
          + "</script>")
  @Options(keyProperty = "id", keyColumn = "ID")
  void insert(@Param("workbasket") WorkbasketImpl workbasket);

  @Update(
      "UPDATE WORKBASKET SET MODIFIED = #{workbasket.modified}, KEY = #{workbasket.key}, NAME = #{workbasket.name}, DOMAIN = #{workbasket.domain}, TYPE = #{workbasket.type}, DESCRIPTION = #{workbasket.description}, OWNER = #{workbasket.owner}, CUSTOM_1 = #{workbasket.custom1}, CUSTOM_2 = #{workbasket.custom2}, CUSTOM_3 = #{workbasket.custom3}, CUSTOM_4 = #{workbasket.custom4}, ORG_LEVEL_1 = #{workbasket.orgLevel1}, ORG_LEVEL_2 = #{workbasket.orgLevel2}, ORG_LEVEL_3 = #{workbasket.orgLevel3}, ORG_LEVEL_4 = #{workbasket.orgLevel4}, MARKED_FOR_DELETION = #{workbasket.markedForDeletion} WHERE id = #{workbasket.id}")
  void update(@Param("workbasket") WorkbasketImpl workbasket);

  @Update(
      "UPDATE WORKBASKET SET MODIFIED = #{workbasket.modified}, NAME = #{workbasket.name}, TYPE = #{workbasket.type}, DESCRIPTION = #{workbasket.description}, OWNER = #{workbasket.owner}, CUSTOM_1 = #{workbasket.custom1}, CUSTOM_2 = #{workbasket.custom2}, CUSTOM_3 = #{workbasket.custom3}, CUSTOM_4 = #{workbasket.custom4}, ORG_LEVEL_1 = #{workbasket.orgLevel1}, ORG_LEVEL_2 = #{workbasket.orgLevel2}, ORG_LEVEL_3 = #{workbasket.orgLevel3}, ORG_LEVEL_4 = #{workbasket.orgLevel4}, MARKED_FOR_DELETION = #{workbasket.markedForDeletion} WHERE KEY = #{workbasket.key} AND DOMAIN = #{workbasket.domain}")
  void updateByKeyAndDomain(@Param("workbasket") WorkbasketImpl workbasket);

  @Delete("DELETE FROM WORKBASKET where id = #{id}")
  void delete(@Param("id") String id);
}
