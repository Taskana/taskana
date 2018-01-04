package pro.taskana.model.mappings;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import pro.taskana.impl.AttachmentImpl;

/**
 * This class is the mybatis mapping of Attachment.
 */
public interface AttachmentMapper {

    @Insert("INSERT INTO ATTACHMENT (ID,  TASK_ID, CREATED, MODIFIED, CLASSIFICATION_KEY, REF_COMPANY, REF_SYSTEM, REF_INSTANCE, REF_TYPE, REF_VALUE, CHANNEL, RECEIVED, CUSTOM_ATTRIBUTES) "
        + "VALUES (#{att.id}, #{att.taskId}, #{att.created}, #{att.modified}, #{att.classification.key}, #{att.objectReference.company}, #{att.objectReference.system}, #{att.objectReference.systemInstance}, "
        + " #{att.objectReference.type}, #{att.objectReference.value}, #{att.channel}, #{att.received}, #{att.customAttributes,jdbcType=BLOB,javaType=java.util.Map,typeHandler=pro.taskana.impl.persistence.MapTypeHandler} )")
    void insert(@Param("att") AttachmentImpl att);

}
