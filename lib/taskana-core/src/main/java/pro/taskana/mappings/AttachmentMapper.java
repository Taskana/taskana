package pro.taskana.mappings;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.ClobTypeHandler;

import pro.taskana.impl.AttachmentImpl;
import pro.taskana.impl.AttachmentSummaryImpl;
import pro.taskana.impl.TaskQueryImpl;
import pro.taskana.impl.persistence.MapTypeHandler;

/**
 * This class is the mybatis mapping of Attachment.
 */
public interface AttachmentMapper {

    @Insert("INSERT INTO ATTACHMENT (ID, TASK_ID, CREATED, MODIFIED, CLASSIFICATION_KEY, CLASSIFICATION_ID, REF_COMPANY, REF_SYSTEM, REF_INSTANCE, REF_TYPE, REF_VALUE, CHANNEL, RECEIVED, CUSTOM_ATTRIBUTES) "
        + "VALUES (#{att.id}, #{att.taskId}, #{att.created}, #{att.modified}, #{att.classificationSummary.key}, #{att.classificationSummary.id}, #{att.objectReference.company}, #{att.objectReference.system}, #{att.objectReference.systemInstance}, "
        + " #{att.objectReference.type}, #{att.objectReference.value}, #{att.channel}, #{att.received}, #{att.customAttributes,jdbcType=CLOB,javaType=java.util.Map,typeHandler=pro.taskana.impl.persistence.MapTypeHandler} )")
    void insert(@Param("att") AttachmentImpl att);

    @Select("<script> SELECT ID, TASK_ID, CREATED, MODIFIED, CLASSIFICATION_KEY, CLASSIFICATION_ID, REF_COMPANY, REF_SYSTEM, REF_INSTANCE, REF_TYPE, REF_VALUE, CHANNEL, RECEIVED, CUSTOM_ATTRIBUTES "
        + "FROM ATTACHMENT "
        + "WHERE TASK_ID = #{taskId} "
        + "<if test=\"_databaseId == 'db2'\">with UR </if> "
        + "</script>")
    @Results(value = {
        @Result(property = "id", column = "ID"),
        @Result(property = "taskId", column = "TASK_ID"),
        @Result(property = "created", column = "CREATED"),
        @Result(property = "modified", column = "MODIFIED"),
        @Result(property = "classificationSummaryImpl.key", column = "CLASSIFICATION_KEY"),
        @Result(property = "classificationSummaryImpl.id", column = "CLASSIFICATION_ID"),
        @Result(property = "objectReference.company", column = "REF_COMPANY"),
        @Result(property = "objectReference.system", column = "REF_SYSTEM"),
        @Result(property = "objectReference.systemInstance", column = "REF_INSTANCE"),
        @Result(property = "objectReference.type", column = "REF_TYPE"),
        @Result(property = "objectReference.value", column = "REF_VALUE"),
        @Result(property = "channel", column = "CHANNEL"),
        @Result(property = "received", column = "RECEIVED"),
        @Result(property = "customAttributes", column = "CUSTOM_ATTRIBUTES",
            javaType = Map.class, typeHandler = MapTypeHandler.class)
    })
    List<AttachmentImpl> findAttachmentsByTaskId(@Param("taskId") String taskId);

    @Select("<script> SELECT ID, TASK_ID, CREATED, MODIFIED, CLASSIFICATION_KEY, CLASSIFICATION_ID, REF_COMPANY, REF_SYSTEM, REF_INSTANCE, REF_TYPE, REF_VALUE, CHANNEL, RECEIVED, CUSTOM_ATTRIBUTES "
        + "FROM ATTACHMENT "
        + "WHERE ID = #{attachmentId} "
        + "<if test=\"_databaseId == 'db2'\">with UR </if> "
        + "</script>")
    @Results(value = {
        @Result(property = "id", column = "ID"),
        @Result(property = "taskId", column = "TASK_ID"),
        @Result(property = "created", column = "CREATED"),
        @Result(property = "modified", column = "MODIFIED"),
        @Result(property = "classificationSummaryImpl.key", column = "CLASSIFICATION_KEY"),
        @Result(property = "classificationSummaryImpl.id", column = "CLASSIFICATION_ID"),
        @Result(property = "objectReference.company", column = "REF_COMPANY"),
        @Result(property = "objectReference.system", column = "REF_SYSTEM"),
        @Result(property = "objectReference.systemInstance", column = "REF_INSTANCE"),
        @Result(property = "objectReference.type", column = "REF_TYPE"),
        @Result(property = "objectReference.value", column = "REF_VALUE"),
        @Result(property = "channel", column = "CHANNEL"),
        @Result(property = "received", column = "RECEIVED"),
        @Result(property = "customAttributes", column = "CUSTOM_ATTRIBUTES",
            javaType = Map.class, typeHandler = MapTypeHandler.class)
    })
    AttachmentImpl getAttachment(@Param("attachmentId") String attachmentId);

    @Select("<script>SELECT DISTINCT ID, TASK_ID, CREATED, MODIFIED, CLASSIFICATION_KEY, CLASSIFICATION_ID, REF_COMPANY, REF_SYSTEM, REF_INSTANCE, REF_TYPE, REF_VALUE, CHANNEL, RECEIVED "
        + "FROM ATTACHMENT "
        + "<where>"
        + "TASK_ID IN (<foreach collection='taskIds' item='item' separator=',' >#{item}</foreach>) "
        + "<if test='taskQuery.attachmentClassificationKeyIn != null'>AND CLASSIFICATION_KEY IN(<foreach collection='taskQuery.attachmentClassificationKeyIn' item='item' separator=',' >#{item}</foreach>)</if> "
        + "<if test='taskQuery.attachmentClassificationKeyLike != null'>AND (<foreach item='item' collection='taskQuery.attachmentClassificationKeyLike' separator=' OR '>UPPER(CLASSIFICATION_KEY) LIKE #{item}</foreach>)</if> "
        + "<if test='taskQuery.attachmentclassificationIdIn != null'>AND CLASSIFICATION_ID IN(<foreach collection='taskQuery.attachmentclassificationIdIn' item='item' separator=',' >#{item}</foreach>)</if> "
        + "<if test='taskQuery.attachmentclassificationIdLike != null'>AND (<foreach item='item' collection='taskQuery.attachmentclassificationIdLike' separator=' OR '>UPPER(CLASSIFICATION_ID) LIKE #{item}</foreach>)</if> "
        + "<if test='taskQuery.attachmentChannelIn != null'>AND CHANNEL IN(<foreach collection='taskQuery.attachmentChannelIn' item='item' separator=',' >#{item}</foreach>)</if> "
        + "<if test='taskQuery.attachmentChannelLike != null'>AND (<foreach item='item' collection='taskQuery.attachmentChannelLike' separator=' OR '>UPPER(CHANNEL) LIKE #{item}</foreach>)</if> "
        + "<if test='taskQuery.attachmentReferenceIn != null'>AND REF_VALUE IN(<foreach collection='taskQuery.attachmentReferenceIn' item='item' separator=',' >#{item}</foreach>)</if> "
        + "<if test='taskQuery.attachmentReferenceLike != null'>AND (<foreach item='item' collection='taskQuery.attachmentReferenceLike' separator=' OR '>UPPER(REF_VALUE) LIKE #{item}</foreach>)</if> "
        + "<if test='taskQuery.attachmentReceivedIn !=null'> AND ( <foreach item='item' collection='taskQuery.attachmentReceivedIn' separator=' OR ' > ( <if test='item.begin!=null'> RECEIVED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> RECEIVED &lt;=#{item.end} </if>)</foreach>)</if> "
        + "</where>"
        + "<if test=\"_databaseId == 'db2'\">with UR </if> "
        + "</script>")
    @Results(value = {
        @Result(property = "id", column = "ID"),
        @Result(property = "taskId", column = "TASK_ID"),
        @Result(property = "created", column = "CREATED"),
        @Result(property = "modified", column = "MODIFIED"),
        @Result(property = "classificationSummaryImpl.key", column = "CLASSIFICATION_KEY"),
        @Result(property = "classificationSummaryImpl.id", column = "CLASSIFICATION_ID"),
        @Result(property = "objectReference.company", column = "REF_COMPANY"),
        @Result(property = "objectReference.system", column = "REF_SYSTEM"),
        @Result(property = "objectReference.systemInstance", column = "REF_INSTANCE"),
        @Result(property = "objectReference.type", column = "REF_TYPE"),
        @Result(property = "objectReference.value", column = "REF_VALUE"),
        @Result(property = "channel", column = "CHANNEL"),
        @Result(property = "received", column = "RECEIVED")
    })
    List<AttachmentSummaryImpl> findAttachmentSummariesByTaskIds(@Param("taskIds") String[] taskIds, @Param("taskQuery") TaskQueryImpl taskQuery);

    @Delete("DELETE FROM ATTACHMENT WHERE ID=#{attachmentId}")
    void deleteAttachment(@Param("attachmentId") String attachmentId);

    @Update("UPDATE ATTACHMENT SET TASK_ID = #{taskId}, CREATED = #{created}, MODIFIED = #{modified},"
        + " CLASSIFICATION_KEY = #{classificationSummary.key}, CLASSIFICATION_ID = #{classificationSummary.id}, REF_COMPANY = #{objectReference.company}, REF_SYSTEM = #{objectReference.system},"
        + " REF_INSTANCE = #{objectReference.systemInstance}, REF_TYPE = #{objectReference.type}, REF_VALUE = #{objectReference.value},"
        + " CHANNEL = #{channel}, RECEIVED = #{received}, CUSTOM_ATTRIBUTES = #{customAttributes,jdbcType=CLOB,javaType=java.util.Map,typeHandler=pro.taskana.impl.persistence.MapTypeHandler}"
        + " WHERE ID = #{id}")
    void update(AttachmentImpl attachment);

    @Select("<script> select CUSTOM_ATTRIBUTES from ATTACHMENT where id = #{attachmentId}"
        + "<if test=\"_databaseId == 'db2'\">with UR </if> "
        + "</script>")
    @Results(value = {
        @Result(property = "customAttributes", column = "CUSTOM_ATTRIBUTES",
            javaType = String.class, typeHandler = ClobTypeHandler.class)
    })
    String getCustomAttributesAsString(@Param("attachmentId") String attachmentId);

    @Select("<script> SELECT DISTINCT TASK_ID FROM ATTACHMENT WHERE CLASSIFICATION_ID = #{classificationId} "
        + "<if test=\"_databaseId == 'db2'\">with UR </if> "
        + "</script>")
    @Results(value = {
        @Result(property = "taskId", column = "TASK_ID")})
    List<String> findTaskIdsAffectedByClassificationChange(@Param("classificationId") String classificationId);

}
