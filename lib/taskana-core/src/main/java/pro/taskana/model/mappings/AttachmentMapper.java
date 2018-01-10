package pro.taskana.model.mappings;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;

import pro.taskana.Classification;
import pro.taskana.impl.AttachmentImpl;
import pro.taskana.impl.persistence.MapTypeHandler;

/**
 * This class is the mybatis mapping of Attachment.
 */
public interface AttachmentMapper {

    String CLASSIFICATION_FINDBYID = "pro.taskana.model.mappings.ClassificationMapper.findById";

    @Insert("INSERT INTO ATTACHMENT (ID, TASK_ID, CREATED, MODIFIED, CLASSIFICATION_KEY, REF_COMPANY, REF_SYSTEM, REF_INSTANCE, REF_TYPE, REF_VALUE, CHANNEL, RECEIVED, CUSTOM_ATTRIBUTES) "
        + "VALUES (#{att.id}, #{att.taskId}, #{att.created}, #{att.modified}, #{att.classification.key}, #{att.objectReference.company}, #{att.objectReference.system}, #{att.objectReference.systemInstance}, "
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
        @Result(property = "classification", column = "CLASSIFICATION_KEY", javaType = Classification.class,
            one = @One(select = CLASSIFICATION_FINDBYID)),
        @Result(property = "porCompany", column = "REF_COMPANY"),
        @Result(property = "porSystem", column = "REF_SYSTEM"),
        @Result(property = "porSystemInstance", column = "REF_INSTANCE"),
        @Result(property = "porType", column = "REF_TYPE"),
        @Result(property = "porValue", column = "REF_VALUE"),
        @Result(property = "channel", column = "CHANNEL"),
        @Result(property = "received", column = "RECEIVED"),
        @Result(property = "customAttributes", column = "CUSTOM_ATTRIBUTES", jdbcType = JdbcType.BLOB,
            javaType = Map.class, typeHandler = MapTypeHandler.class),
    })
    List<AttachmentImpl> findAttachmentsByTaskId(@Param("taskId") String taskId);
}
