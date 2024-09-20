package io.kadai.workbasket.internal;

import io.kadai.workbasket.internal.models.WorkbasketImpl;
import io.kadai.workbasket.internal.models.WorkbasketSummaryImpl;
import java.util.List;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

/** This class is the mybatis mapping of workbaskets. */
@SuppressWarnings("checkstyle:LineLength")
public interface WorkbasketMapper {

  @SelectProvider(type = WorkbasketSqlProvider.class, method = "findById")
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
  @Result(property = "custom5", column = "CUSTOM_5")
  @Result(property = "custom6", column = "CUSTOM_6")
  @Result(property = "custom7", column = "CUSTOM_7")
  @Result(property = "custom8", column = "CUSTOM_8")
  WorkbasketImpl findById(@Param("id") String id);

  @SelectProvider(type = WorkbasketSqlProvider.class, method = "findByKeyAndDomain")
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
  @Result(property = "custom5", column = "CUSTOM_5")
  @Result(property = "custom6", column = "CUSTOM_6")
  @Result(property = "custom7", column = "CUSTOM_7")
  @Result(property = "custom8", column = "CUSTOM_8")
  WorkbasketImpl findByKeyAndDomain(@Param("key") String key, @Param("domain") String domain);

  @SelectProvider(type = WorkbasketSqlProvider.class, method = "findDistributionTargets")
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
  @Result(property = "custom5", column = "CUSTOM_5")
  @Result(property = "custom6", column = "CUSTOM_6")
  @Result(property = "custom7", column = "CUSTOM_7")
  @Result(property = "custom8", column = "CUSTOM_8")
  List<WorkbasketSummaryImpl> findDistributionTargets(@Param("id") String id);

  @SelectProvider(type = WorkbasketSqlProvider.class, method = "findDistributionSources")
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
  @Result(property = "custom5", column = "CUSTOM_5")
  @Result(property = "custom6", column = "CUSTOM_6")
  @Result(property = "custom7", column = "CUSTOM_7")
  @Result(property = "custom8", column = "CUSTOM_8")
  List<WorkbasketSummaryImpl> findDistributionSources(@Param("id") String id);

  @SelectProvider(type = WorkbasketSqlProvider.class, method = "findSummaryById")
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
  @Result(property = "custom5", column = "CUSTOM_5")
  @Result(property = "custom6", column = "CUSTOM_6")
  @Result(property = "custom7", column = "CUSTOM_7")
  @Result(property = "custom8", column = "CUSTOM_8")
  List<WorkbasketSummaryImpl> findSummaryById(@Param("key") String id);

  @SelectProvider(type = WorkbasketSqlProvider.class, method = "findAll")
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
  @Result(property = "custom5", column = "CUSTOM_5")
  @Result(property = "custom6", column = "CUSTOM_6")
  @Result(property = "custom7", column = "CUSTOM_7")
  @Result(property = "custom8", column = "CUSTOM_8")
  List<WorkbasketSummaryImpl> findAll();

  @InsertProvider(type = WorkbasketSqlProvider.class, method = "insert")
  @Options(keyProperty = "id", keyColumn = "ID")
  void insert(@Param("workbasket") WorkbasketImpl workbasket);

  @UpdateProvider(type = WorkbasketSqlProvider.class, method = "update")
  void update(@Param("workbasket") WorkbasketImpl workbasket);

  @UpdateProvider(type = WorkbasketSqlProvider.class, method = "updateByKeyAndDomain")
  void updateByKeyAndDomain(@Param("workbasket") WorkbasketImpl workbasket);

  @DeleteProvider(type = WorkbasketSqlProvider.class, method = "delete")
  void delete(@Param("id") String id);
}
