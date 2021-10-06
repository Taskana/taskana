package pro.taskana.task.internal;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.ClobTypeHandler;

import pro.taskana.common.internal.persistence.MapTypeHandler;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.task.internal.models.AttachmentImpl;
import pro.taskana.task.internal.models.AttachmentSummaryImpl;

/** This class is the mybatis mapping of Attachment. */
@SuppressWarnings("checkstyle:LineLength")
public interface AttachmentMapper {

  @Insert(
      "INSERT INTO ATTACHMENT (ID, TASK_ID, CREATED, MODIFIED, CLASSIFICATION_KEY, CLASSIFICATION_ID, REF_COMPANY, REF_SYSTEM, REF_INSTANCE, REF_TYPE, REF_VALUE, CHANNEL, RECEIVED, CUSTOM_ATTRIBUTES) "
          + "VALUES (#{att.id}, #{att.taskId}, #{att.created}, #{att.modified}, #{att.classificationSummary.key}, #{att.classificationSummary.id}, #{att.objectReference.company}, #{att.objectReference.system}, #{att.objectReference.systemInstance}, "
          + " #{att.objectReference.type}, #{att.objectReference.value}, #{att.channel}, #{att.received}, #{att.customAttributes,jdbcType=CLOB,javaType=java.util.Map,typeHandler=pro.taskana.common.internal.persistence.MapTypeHandler} )")
  void insert(@Param("att") AttachmentImpl att);

  @Select(
      "<script> SELECT ID, TASK_ID, CREATED, MODIFIED, CLASSIFICATION_KEY, CLASSIFICATION_ID, REF_COMPANY, REF_SYSTEM, REF_INSTANCE, REF_TYPE, REF_VALUE, CHANNEL, RECEIVED, CUSTOM_ATTRIBUTES "
          + "FROM ATTACHMENT "
          + "WHERE TASK_ID = #{taskId} "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  @Result(property = "id", column = "ID")
  @Result(property = "taskId", column = "TASK_ID")
  @Result(property = "created", column = "CREATED")
  @Result(property = "modified", column = "MODIFIED")
  @Result(property = "classificationSummaryImpl.key", column = "CLASSIFICATION_KEY")
  @Result(property = "classificationSummaryImpl.id", column = "CLASSIFICATION_ID")
  @Result(property = "objectReference.company", column = "REF_COMPANY")
  @Result(property = "objectReference.system", column = "REF_SYSTEM")
  @Result(property = "objectReference.systemInstance", column = "REF_INSTANCE")
  @Result(property = "objectReference.type", column = "REF_TYPE")
  @Result(property = "objectReference.value", column = "REF_VALUE")
  @Result(property = "channel", column = "CHANNEL")
  @Result(property = "received", column = "RECEIVED")
  @Result(
      property = "customAttributes",
      column = "CUSTOM_ATTRIBUTES",
      javaType = Map.class,
      typeHandler = MapTypeHandler.class)
  List<AttachmentImpl> findAttachmentsByTaskId(@Param("taskId") String taskId);

  @Select(
      "<script>SELECT ID, TASK_ID, CREATED, MODIFIED, CLASSIFICATION_KEY, CLASSIFICATION_ID, REF_COMPANY, REF_SYSTEM, REF_INSTANCE, REF_TYPE, REF_VALUE, CHANNEL, RECEIVED "
          + "FROM ATTACHMENT "
          + "<where>"
          + "<choose>"
          + "<when  test='taskIds == null'>"
          + " 1 = 2 "
          + "</when>"
          + "<otherwise>"
          + "TASK_ID IN (<foreach collection='taskIds' item='item' separator=',' >#{item}</foreach>) "
          + "</otherwise>"
          + "</choose>"
          + "</where>"
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  @Result(property = "id", column = "ID")
  @Result(property = "taskId", column = "TASK_ID")
  @Result(property = "created", column = "CREATED")
  @Result(property = "modified", column = "MODIFIED")
  @Result(property = "classificationSummaryImpl.key", column = "CLASSIFICATION_KEY")
  @Result(property = "classificationSummaryImpl.id", column = "CLASSIFICATION_ID")
  @Result(property = "objectReference.company", column = "REF_COMPANY")
  @Result(property = "objectReference.system", column = "REF_SYSTEM")
  @Result(property = "objectReference.systemInstance", column = "REF_INSTANCE")
  @Result(property = "objectReference.type", column = "REF_TYPE")
  @Result(property = "objectReference.value", column = "REF_VALUE")
  @Result(property = "channel", column = "CHANNEL")
  @Result(property = "received", column = "RECEIVED")
  List<AttachmentSummaryImpl> findAttachmentSummariesByTaskIds(
      @Param("taskIds") Collection<String> taskIds);

  @Delete("DELETE FROM ATTACHMENT WHERE ID=#{attachmentId}")
  void delete(@Param("attachmentId") String attachmentId);

  @Delete(
      "<script>DELETE FROM ATTACHMENT WHERE TASK_ID IN(<foreach item='item' collection='taskIds' separator=',' >#{item}</foreach>)</script>")
  void deleteMultipleByTaskIds(@Param("taskIds") List<String> taskIds);

  @Update(
      "UPDATE ATTACHMENT SET TASK_ID = #{taskId}, CREATED = #{created}, MODIFIED = #{modified},"
          + " CLASSIFICATION_KEY = #{classificationSummary.key}, CLASSIFICATION_ID = #{classificationSummary.id}, REF_COMPANY = #{objectReference.company}, REF_SYSTEM = #{objectReference.system},"
          + " REF_INSTANCE = #{objectReference.systemInstance}, REF_TYPE = #{objectReference.type}, REF_VALUE = #{objectReference.value},"
          + " CHANNEL = #{channel}, RECEIVED = #{received}, CUSTOM_ATTRIBUTES = #{customAttributes,jdbcType=CLOB,javaType=java.util.Map,typeHandler=pro.taskana.common.internal.persistence.MapTypeHandler}"
          + " WHERE ID = #{id}")
  void update(AttachmentImpl attachment);

  @Select(
      "<script> select CUSTOM_ATTRIBUTES from ATTACHMENT where id = #{attachmentId}"
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  @Result(
      property = "customAttributes",
      column = "CUSTOM_ATTRIBUTES",
      javaType = String.class,
      typeHandler = ClobTypeHandler.class)
  String getCustomAttributesAsString(@Param("attachmentId") String attachmentId);

  @Select(
      "<script> SELECT DISTINCT t.ID, t.PLANNED FROM TASK t "
          + "LEFT JOIN ATTACHMENT AS a on a.TASK_ID = t.ID"
          + " WHERE a.CLASSIFICATION_ID = #{classificationId} "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  @Result(property = "left", column = "ID")
  @Result(property = "right", column = "PLANNED")
  List<Pair<String, Instant>> findTaskIdsAndPlannedAffectedByClassificationChange(
      @Param("classificationId") String classificationId);
}
