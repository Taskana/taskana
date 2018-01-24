package pro.taskana.model.mappings;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;

import pro.taskana.impl.AttachmentImpl;
import pro.taskana.impl.AttachmentSummaryImpl;
import pro.taskana.impl.persistence.MapTypeHandler;

/**
 * This class is the mybatis mapping of Attachment.
 */
public interface AttachmentMapper {

    @Insert("INSERT INTO ATTACHMENT (ID, TASK_ID, CREATED, MODIFIED, CLASSIFICATION_KEY, REF_COMPANY, REF_SYSTEM, REF_INSTANCE, REF_TYPE, REF_VALUE, CHANNEL, RECEIVED, CUSTOM_ATTRIBUTES) "
        + "VALUES (#{att.id}, #{att.taskId}, #{att.created}, #{att.modified}, #{att.classificationSummary.key}, #{att.objectReference.company}, #{att.objectReference.system}, #{att.objectReference.systemInstance}, "
        + " #{att.objectReference.type}, #{att.objectReference.value}, #{att.channel}, #{att.received}, #{att.customAttributes,jdbcType=BLOB,javaType=java.util.Map,typeHandler=pro.taskana.impl.persistence.MapTypeHandler} )")
    void insert(@Param("att") AttachmentImpl att);

    @Select("SELECT ID, TASK_ID, CREATED, MODIFIED, CLASSIFICATION_KEY, REF_COMPANY, REF_SYSTEM, REF_INSTANCE, REF_TYPE, REF_VALUE, CHANNEL, RECEIVED, CUSTOM_ATTRIBUTES "
        + "FROM ATTACHMENT "
        + "WHERE TASK_ID = #{taskId}")
    @Results(value = {
        @Result(property = "id", column = "ID"),
        @Result(property = "taskId", column = "TASK_ID"),
        @Result(property = "created", column = "CREATED"),
        @Result(property = "modified", column = "MODIFIED"),
        @Result(property = "classificationSummaryImpl.key", column = "CLASSIFICATION_KEY"),
        @Result(property = "objectReference.company", column = "REF_COMPANY"),
        @Result(property = "objectReference.system", column = "REF_SYSTEM"),
        @Result(property = "objectReference.systemInstance", column = "REF_INSTANCE"),
        @Result(property = "objectReference.type", column = "REF_TYPE"),
        @Result(property = "objectReference.value", column = "REF_VALUE"),
        @Result(property = "channel", column = "CHANNEL"),
        @Result(property = "received", column = "RECEIVED"),
        @Result(property = "customAttributes", column = "CUSTOM_ATTRIBUTES", jdbcType = JdbcType.BLOB,
            javaType = Map.class, typeHandler = MapTypeHandler.class)
    })
    List<AttachmentImpl> findAttachmentsByTaskId(@Param("taskId") String taskId);

    @Select("SELECT ID, TASK_ID, CREATED, MODIFIED, CLASSIFICATION_KEY, REF_COMPANY, REF_SYSTEM, REF_INSTANCE, REF_TYPE, REF_VALUE, CHANNEL, RECEIVED, CUSTOM_ATTRIBUTES "
        + "FROM ATTACHMENT "
        + "WHERE ID = #{attachmentId}")
    @Results(value = {
        @Result(property = "id", column = "ID"),
        @Result(property = "taskId", column = "TASK_ID"),
        @Result(property = "created", column = "CREATED"),
        @Result(property = "modified", column = "MODIFIED"),
        @Result(property = "classificationSummaryImpl.key", column = "CLASSIFICATION_KEY"),
        @Result(property = "objectReference.company", column = "REF_COMPANY"),
        @Result(property = "objectReference.system", column = "REF_SYSTEM"),
        @Result(property = "objectReference.systemInstance", column = "REF_INSTANCE"),
        @Result(property = "objectReference.type", column = "REF_TYPE"),
        @Result(property = "objectReference.value", column = "REF_VALUE"),
        @Result(property = "channel", column = "CHANNEL"),
        @Result(property = "received", column = "RECEIVED"),
        @Result(property = "customAttributes", column = "CUSTOM_ATTRIBUTES", jdbcType = JdbcType.BLOB,
            javaType = Map.class, typeHandler = MapTypeHandler.class)
    })
    AttachmentImpl getAttachment(@Param("attachmentId") String attachmentId);

    @Select("<script>SELECT ID, TASK_ID, CREATED, MODIFIED, CLASSIFICATION_KEY, RECEIVED "
        + "FROM ATTACHMENT "
        + "<where>"
        + "TASK_ID IN (<foreach collection='array' item='item' separator=',' >#{item}</foreach>)"
        + "</where>"
        + "</script>")
    @Results(value = {
        @Result(property = "id", column = "ID"),
        @Result(property = "taskId", column = "TASK_ID"),
        @Result(property = "created", column = "CREATED"),
        @Result(property = "modified", column = "MODIFIED"),
        @Result(property = "classificationSummaryImpl.key", column = "CLASSIFICATION_KEY"),
        @Result(property = "received", column = "RECEIVED"),
    })
    List<AttachmentSummaryImpl> findAttachmentSummariesByTaskIds(String[] taskIds);

    @Delete("DELETE FROM ATTACHMENT WHERE ID=#{attachmentId}")
    void deleteAttachment(@Param("attachmentId") String attachmentId);

    @Update("UPDATE ATTACHMENT SET TASK_ID = #{taskId}, CREATED = #{created}, MODIFIED = #{modified},"
        + " CLASSIFICATION_KEY = #{classificationSummary.key}, REF_COMPANY = #{objectReference.company}, REF_SYSTEM = #{objectReference.system},"
        + " REF_INSTANCE = #{objectReference.systemInstance}, REF_TYPE = #{objectReference.type}, REF_VALUE = #{objectReference.value},"
        + " CHANNEL = #{channel}, RECEIVED = #{received}, CUSTOM_ATTRIBUTES = #{customAttributes,jdbcType=BLOB,javaType=java.util.Map,typeHandler=pro.taskana.impl.persistence.MapTypeHandler}"
        + " WHERE ID = #{id}")
    void update(AttachmentImpl attachment);
}
