package pro.taskana.workbasket.internal;

import java.util.List;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import pro.taskana.workbasket.internal.models.WorkbasketAccessItemImpl;

/** This class is the mybatis mapping of workbasket access items. */
@SuppressWarnings("checkstyle:LineLength")
public interface WorkbasketAccessMapper {

  @SelectProvider(type = WorkbasketAccessSqlProvider.class, method = "findById")
  @Result(property = "id", column = "ID")
  @Result(property = "workbasketId", column = "WORKBASKET_ID")
  @Result(property = "accessId", column = "ACCESS_ID")
  @Result(property = "accessName", column = "ACCESS_NAME")
  @Result(property = "permRead", column = "PERM_READ")
  @Result(property = "permReadTasks", column = "PERM_READTASKS")
  @Result(property = "permEditTasks", column = "PERM_EDITTASKS")
  @Result(property = "permOpen", column = "PERM_OPEN")
  @Result(property = "permAppend", column = "PERM_APPEND")
  @Result(property = "permTransfer", column = "PERM_TRANSFER")
  @Result(property = "permDistribute", column = "PERM_DISTRIBUTE")
  @Result(property = "permCustom1", column = "PERM_CUSTOM_1")
  @Result(property = "permCustom2", column = "PERM_CUSTOM_2")
  @Result(property = "permCustom3", column = "PERM_CUSTOM_3")
  @Result(property = "permCustom4", column = "PERM_CUSTOM_4")
  @Result(property = "permCustom5", column = "PERM_CUSTOM_5")
  @Result(property = "permCustom6", column = "PERM_CUSTOM_6")
  @Result(property = "permCustom7", column = "PERM_CUSTOM_7")
  @Result(property = "permCustom8", column = "PERM_CUSTOM_8")
  @Result(property = "permCustom9", column = "PERM_CUSTOM_9")
  @Result(property = "permCustom10", column = "PERM_CUSTOM_10")
  @Result(property = "permCustom11", column = "PERM_CUSTOM_11")
  @Result(property = "permCustom12", column = "PERM_CUSTOM_12")
  WorkbasketAccessItemImpl findById(@Param("id") String id);

  @SelectProvider(type = WorkbasketAccessSqlProvider.class, method = "findByWorkbasketId")
  @Result(property = "id", column = "ID")
  @Result(property = "workbasketId", column = "WORKBASKET_ID")
  @Result(property = "workbasketKey", column = "KEY")
  @Result(property = "accessId", column = "ACCESS_ID")
  @Result(property = "accessName", column = "ACCESS_NAME")
  @Result(property = "permRead", column = "PERM_READ")
  @Result(property = "permReadTasks", column = "PERM_READTASKS")
  @Result(property = "permEditTasks", column = "PERM_EDITTASKS")
  @Result(property = "permOpen", column = "PERM_OPEN")
  @Result(property = "permAppend", column = "PERM_APPEND")
  @Result(property = "permTransfer", column = "PERM_TRANSFER")
  @Result(property = "permDistribute", column = "PERM_DISTRIBUTE")
  @Result(property = "permCustom1", column = "PERM_CUSTOM_1")
  @Result(property = "permCustom2", column = "PERM_CUSTOM_2")
  @Result(property = "permCustom3", column = "PERM_CUSTOM_3")
  @Result(property = "permCustom4", column = "PERM_CUSTOM_4")
  @Result(property = "permCustom5", column = "PERM_CUSTOM_5")
  @Result(property = "permCustom6", column = "PERM_CUSTOM_6")
  @Result(property = "permCustom7", column = "PERM_CUSTOM_7")
  @Result(property = "permCustom8", column = "PERM_CUSTOM_8")
  @Result(property = "permCustom9", column = "PERM_CUSTOM_9")
  @Result(property = "permCustom10", column = "PERM_CUSTOM_10")
  @Result(property = "permCustom11", column = "PERM_CUSTOM_11")
  @Result(property = "permCustom12", column = "PERM_CUSTOM_12")
  List<WorkbasketAccessItemImpl> findByWorkbasketId(@Param("id") String id);

  @SelectProvider(type = WorkbasketAccessSqlProvider.class, method = "findByAccessId")
  @Result(property = "id", column = "ID")
  @Result(property = "workbasketId", column = "WORKBASKET_ID")
  @Result(property = "workbasketKey", column = "KEY")
  @Result(property = "accessId", column = "ACCESS_ID")
  @Result(property = "accessName", column = "ACCESS_NAME")
  @Result(property = "permRead", column = "PERM_READ")
  @Result(property = "permReadTasks", column = "PERM_READTASKS")
  @Result(property = "permEditTasks", column = "PERM_EDITTASKS")
  @Result(property = "permOpen", column = "PERM_OPEN")
  @Result(property = "permAppend", column = "PERM_APPEND")
  @Result(property = "permTransfer", column = "PERM_TRANSFER")
  @Result(property = "permDistribute", column = "PERM_DISTRIBUTE")
  @Result(property = "permCustom1", column = "PERM_CUSTOM_1")
  @Result(property = "permCustom2", column = "PERM_CUSTOM_2")
  @Result(property = "permCustom3", column = "PERM_CUSTOM_3")
  @Result(property = "permCustom4", column = "PERM_CUSTOM_4")
  @Result(property = "permCustom5", column = "PERM_CUSTOM_5")
  @Result(property = "permCustom6", column = "PERM_CUSTOM_6")
  @Result(property = "permCustom7", column = "PERM_CUSTOM_7")
  @Result(property = "permCustom8", column = "PERM_CUSTOM_8")
  @Result(property = "permCustom9", column = "PERM_CUSTOM_9")
  @Result(property = "permCustom10", column = "PERM_CUSTOM_10")
  @Result(property = "permCustom11", column = "PERM_CUSTOM_11")
  @Result(property = "permCustom12", column = "PERM_CUSTOM_12")
  List<WorkbasketAccessItemImpl> findByAccessId(@Param("id") String id);

  @InsertProvider(type = WorkbasketAccessSqlProvider.class, method = "insert")
  @Options(keyProperty = "id", keyColumn = "ID")
  void insert(@Param("workbasketAccessItem") WorkbasketAccessItemImpl workbasketAccessItem);

  @UpdateProvider(type = WorkbasketAccessSqlProvider.class, method = "update")
  void update(@Param("workbasketAccessItem") WorkbasketAccessItemImpl workbasketAccessItem);

  @DeleteProvider(type = WorkbasketAccessSqlProvider.class, method = "delete")
  void delete(@Param("id") String id);

  @DeleteProvider(
      type = WorkbasketAccessSqlProvider.class,
      method = "deleteAllAccessItemsForWorkbasketId")
  void deleteAllAccessItemsForWorkbasketId(@Param("workbasketId") String workbasketId);

  @DeleteProvider(type = WorkbasketAccessSqlProvider.class, method = "deleteAccessItemsForAccessId")
  void deleteAccessItemsForAccessId(@Param("accessId") String accessId);

  @SelectProvider(type = WorkbasketAccessSqlProvider.class, method = "findByWorkbasketAndAccessId")
  @Result(property = "id", column = "ID")
  @Result(property = "workbasketId", column = "WORKBASKET_ID")
  @Result(property = "accessId", column = "ACCESS_ID")
  @Result(property = "accessName", column = "ACCESS_NAME")
  @Result(property = "permRead", column = "PERM_READ")
  @Result(property = "permReadTasks", column = "PERM_READTASKS")
  @Result(property = "permEditTasks", column = "PERM_EDITTASKS")
  @Result(property = "permOpen", column = "PERM_OPEN")
  @Result(property = "permAppend", column = "PERM_APPEND")
  @Result(property = "permTransfer", column = "PERM_TRANSFER")
  @Result(property = "permDistribute", column = "PERM_DISTRIBUTE")
  @Result(property = "permCustom1", column = "PERM_CUSTOM_1")
  @Result(property = "permCustom2", column = "PERM_CUSTOM_2")
  @Result(property = "permCustom3", column = "PERM_CUSTOM_3")
  @Result(property = "permCustom4", column = "PERM_CUSTOM_4")
  @Result(property = "permCustom5", column = "PERM_CUSTOM_5")
  @Result(property = "permCustom6", column = "PERM_CUSTOM_6")
  @Result(property = "permCustom7", column = "PERM_CUSTOM_7")
  @Result(property = "permCustom8", column = "PERM_CUSTOM_8")
  @Result(property = "permCustom9", column = "PERM_CUSTOM_9")
  @Result(property = "permCustom10", column = "PERM_CUSTOM_10")
  @Result(property = "permCustom11", column = "PERM_CUSTOM_11")
  @Result(property = "permCustom12", column = "PERM_CUSTOM_12")
  WorkbasketAccessItemImpl findByWorkbasketAndAccessId(
      @Param("workbasketId") String workbasketId, @Param("accessIds") List<String> accessIds);

  @SelectProvider(
      type = WorkbasketAccessSqlProvider.class,
      method = "findByWorkbasketKeyDomainAndAccessId")
  @Result(property = "workbasketId", column = "WORKBASKET_ID")
  @Result(property = "accessId", column = "ACCESS_ID")
  @Result(property = "accessName", column = "ACCESS_NAME")
  @Result(property = "permRead", column = "PERM_READ")
  @Result(property = "permReadTasks", column = "PERM_READTASKS")
  @Result(property = "permEditTasks", column = "PERM_EDITTASKS")
  @Result(property = "permOpen", column = "PERM_OPEN")
  @Result(property = "permAppend", column = "PERM_APPEND")
  @Result(property = "permTransfer", column = "PERM_TRANSFER")
  @Result(property = "permDistribute", column = "PERM_DISTRIBUTE")
  @Result(property = "permCustom1", column = "PERM_CUSTOM_1")
  @Result(property = "permCustom2", column = "PERM_CUSTOM_2")
  @Result(property = "permCustom3", column = "PERM_CUSTOM_3")
  @Result(property = "permCustom4", column = "PERM_CUSTOM_4")
  @Result(property = "permCustom5", column = "PERM_CUSTOM_5")
  @Result(property = "permCustom6", column = "PERM_CUSTOM_6")
  @Result(property = "permCustom7", column = "PERM_CUSTOM_7")
  @Result(property = "permCustom8", column = "PERM_CUSTOM_8")
  @Result(property = "permCustom9", column = "PERM_CUSTOM_9")
  @Result(property = "permCustom10", column = "PERM_CUSTOM_10")
  @Result(property = "permCustom11", column = "PERM_CUSTOM_11")
  @Result(property = "permCustom12", column = "PERM_CUSTOM_12")
  WorkbasketAccessItemImpl findByWorkbasketKeyDomainAndAccessId(
      @Param("workbasketKey") String workbasketKey,
      @Param("domain") String domain,
      @Param("accessIds") List<String> accessIds);
}
