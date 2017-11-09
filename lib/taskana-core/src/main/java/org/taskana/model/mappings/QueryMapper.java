package org.taskana.model.mappings;

import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.taskana.impl.persistence.ClassificationQueryImpl;
import org.taskana.impl.persistence.ObjectReferenceQueryImpl;
import org.taskana.impl.persistence.TaskQueryImpl;
import org.taskana.model.Classification;
import org.taskana.model.ObjectReference;
import org.taskana.model.Task;

import java.util.List;

/**
 * This class provides a mapper for all queries.
 */
public interface QueryMapper {

    String OBJECTREFERENCEMAPPER_FINDBYID = "org.taskana.model.mappings.ObjectReferenceMapper.findById";
    String CLASSIFICATION_FINDBYID = "org.taskana.model.mappings.ClassificationMapper.findByIdAndDomain";

    @Select("<script>SELECT t.ID, t.TENANT_ID, t.CREATED, t.CLAIMED, t.COMPLETED, t.MODIFIED, t.PLANNED, t.DUE, t.NAME, t.DESCRIPTION, t.PRIORITY, t.STATE, t.CLASSIFICATION_ID, t.WORKBASKETID, t.OWNER, t.PRIMARY_OBJ_REF_ID, t.IS_READ, t.IS_TRANSFERRED, t.CUSTOM_1, t.CUSTOM_2, t.CUSTOM_3, t.CUSTOM_4, t.CUSTOM_5, t.CUSTOM_6, t.CUSTOM_7, t.CUSTOM_8, t.CUSTOM_9, t.CUSTOM_10 "
            + "FROM TASK t "
            // Joins if Classification or Object Reference Query is needed
            + "<if test='classificationQuery != null'>LEFT OUTER JOIN CLASSIFICATION c on t.CLASSIFICATION_ID = c.ID</if> "
            + "<if test='objectReferenceQuery != null'>LEFT OUTER JOIN OBJECT_REFERENCE o on t.PRIMARY_OBJ_REF_ID = o.ID</if> "
            + "<where>"
            + "<if test='tenantId != null'>t.TENANT_ID = #{tenantId}</if> "
            + "<if test='name != null'>AND t.NAME IN(<foreach item='item' collection='name' separator=',' >#{item}</foreach>)</if> "
            + "<if test='description != null'>AND t.DESCRIPTION like #{description}</if> "
            + "<if test='priority != null'>AND t.PRIORITY IN(<foreach item='item' collection='priority' separator=',' >#{item}</foreach>)</if> "
            + "<if test='states != null'>AND t.STATE IN(<foreach item='item' collection='states' separator=',' >#{item}</foreach>)</if> "
            + "<if test='workbasketId != null'>AND t.WORKBASKETID IN(<foreach item='item' collection='workbasketId' separator=',' >#{item}</foreach>)</if> "
            + "<if test='owner != null'>AND t.OWNER IN(<foreach item='item' collection='owner' separator=',' >#{item}</foreach>)</if> "
            + "<if test='isRead != null'>AND t.IS_READ = #{isRead}</if> "
            + "<if test='isTransferred != null'>AND t.IS_TRANSFERRED = #{isTransferred}</if> "
            + "<if test='customFields != null'>AND (t.CUSTOM_1 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR t.CUSTOM_2 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR t.CUSTOM_3 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR t.CUSTOM_4 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR t.CUSTOM_5 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR t.CUSTOM_6 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR t.CUSTOM_7 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR t.CUSTOM_8 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR t.CUSTOM_9 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR t.CUSTOM_10 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>))</if> "
            // Classification Query
            + "<if test='classificationQuery != null'>"
            + "<if test='classificationQuery.tenantId != null'>AND c.TENANT_ID = #{classificationQuery.tenantId}</if> "
            + "<if test='classificationQuery.parentClassificationId != null'>AND c.PARENT_CLASSIFICATION_ID IN(<foreach item='item' collection='classificationQuery.parentClassificationId' separator=',' >#{item}</foreach>)</if> "
            + "<if test='classificationQuery.category != null'>AND c.CATEGORY IN(<foreach item='item' collection='classificationQuery.category' separator=',' >#{item}</foreach>)</if> "
            + "<if test='classificationQuery.type != null'>AND c.TYPE IN(<foreach item='item' collection='classificationQuery.type' separator=',' >#{item}</foreach>)</if> "
            + "<if test='classificationQuery.domain != null'>AND c.DOMAIN IN(<foreach item='item' collection='classificationQuery.domain' separator=',' >#{item}</foreach>)</if> "
            + "<if test='classificationQuery.validInDomain != null'>AND c.VALID_IN_DOMAIN like #{classificationQuery.validInDomain}</if> "
            + "<if test='classificationQuery.created != null'>AND c.CREATED IN(<foreach item='item' collection='classificationQuery.created' separator=',' >#{item}</foreach>)</if> "
            + "<if test='classificationQuery.name != null'>AND c.NAME IN(<foreach item='item' collection='classificationQuery.name' separator=',' >#{item}</foreach>)</if> "
            + "<if test='classificationQuery.description != null'>AND c.DESCRIPTION like #{classificationQuery.description}</if> "
            + "<if test='classificationQuery.priority != null'>AND c.PRIORITY IN(<foreach item='item' collection='classificationQuery.priority' separator=',' >#{item}</foreach>)</if> "
            + "<if test='classificationQuery.serviceLevel != null'>AND c.SERVICE_LEVEL IN(<foreach item='item' collection='classificationQuery.serviceLevel' separator=',' >#{item}</foreach>)</if> "
            + "<if test='classificationQuery.customFields != null'>AND (c.CUSTOM_1 IN(<foreach item='item' collection='classificationQuery.customFields' separator=',' >#{item}</foreach>) OR c.CUSTOM_2 IN(<foreach item='item' collection='classificationQuery.customFields' separator=',' >#{item}</foreach>) OR c.CUSTOM_3 IN(<foreach item='item' collection='classificationQuery.customFields' separator=',' >#{item}</foreach>) OR c.CUSTOM_4 IN(<foreach item='item' collection='classificationQuery.customFields' separator=',' >#{item}</foreach>) OR c.CUSTOM_5 IN(<foreach item='item' collection='classificationQuery.customFields' separator=',' >#{item}</foreach>) OR c.CUSTOM_6 IN(<foreach item='item' collection='classificationQuery.customFields' separator=',' >#{item}</foreach>) OR c.CUSTOM_7 IN(<foreach item='item' collection='classificationQuery.customFields' separator=',' >#{item}</foreach>) OR c.CUSTOM_8 IN(<foreach item='item' collection='classificationQuery.customFields' separator=',' >#{item}</foreach>))</if> "
            + "<if test='classificationQuery.validFrom != null'>AND c.VALID_FROM IN(<foreach item='item' collection='classificationQuery.validFrom' separator=',' >#{item}</foreach>)</if> "
            + "<if test='classificationQuery.validUntil != null'>AND c.VALID_UNTIL IN(<foreach item='item' collection='classificationQuery.validUntil' separator=',' >#{item}</foreach>)</if> "
            + "</if>"
            // Object Reference Query
            + "<if test='objectReferenceQuery != null'>"
            + "<if test='objectReferenceQuery.tenantId != null'>AND o.TENANT_ID = #{objectReferenceQuery.tenantId}</if> "
            + "<if test='objectReferenceQuery.company != null'>AND o.COMPANY IN(<foreach item='item' collection='objectReferenceQuery.company' separator=',' >#{item}</foreach>)</if> "
            + "<if test='objectReferenceQuery.system != null'>AND o.SYSTEM IN(<foreach item='item' collection='objectReferenceQuery.system' separator=',' >#{item}</foreach>)</if> "
            + "<if test='objectReferenceQuery.systemInstance != null'>AND o.SYSTEM_INSTANCE IN(<foreach item='item' collection='objectReferenceQuery.systemInstance' separator=',' >#{item}</foreach>)</if> "
            + "<if test='objectReferenceQuery.type != null'>AND o.TYPE IN(<foreach item='item' collection='objectReferenceQuery.type' separator=',' >#{item}</foreach>)</if> "
            + "<if test='objectReferenceQuery.value != null'>AND o.VALUE IN(<foreach item='item' collection='objectReferenceQuery.value' separator=',' >#{item}</foreach>)</if> "
            + "</if>"
            + "</where>"
            + "</script>")
    @Results(value = { @Result(property = "id", column = "ID"),
            @Result(property = "tenantId", column = "TENANT_ID"),
            @Result(property = "created", column = "CREATED"),
            @Result(property = "claimed", column = "CLAIMED"),
            @Result(property = "completed", column = "COMPLETED"),
            @Result(property = "planned", column = "PLANNED"),
            @Result(property = "due", column = "DUE"),
            @Result(property = "name", column = "NAME"),
            @Result(property = "description", column = "DESCRIPTION"),
            @Result(property = "priority", column = "PRIORITY"),
            @Result(property = "state", column = "STATE"),
            @Result(property = "classification", column = "CLASSIFICATION_ID", javaType = Classification.class, one = @One(select = CLASSIFICATION_FINDBYID)),
            @Result(property = "workbasketId", column = "WORKBASKETID"),
            @Result(property = "owner", column = "OWNER"),
            @Result(property = "primaryObjRef", column = "PRIMARY_OBJ_REF_ID", javaType = ObjectReference.class, one = @One(select = OBJECTREFERENCEMAPPER_FINDBYID)),
            @Result(property = "isRead", column = "IS_READ"),
            @Result(property = "isTransferred", column = "IS_TRANSFERRED"),
            @Result(property = "custom1", column = "CUSTOM_1"),
            @Result(property = "custom2", column = "CUSTOM_2"),
            @Result(property = "custom3", column = "CUSTOM_3"),
            @Result(property = "custom4", column = "CUSTOM_4"),
            @Result(property = "custom5", column = "CUSTOM_5"),
            @Result(property = "custom6", column = "CUSTOM_6"),
            @Result(property = "custom7", column = "CUSTOM_7"),
            @Result(property = "custom8", column = "CUSTOM_8"),
            @Result(property = "custom9", column = "CUSTOM_9"),
            @Result(property = "custom10", column = "CUSTOM_10") })
    List<Task> queryTasks(TaskQueryImpl taskQuery);

    @Select("<script>SELECT ID, TENANT_ID, PARENT_CLASSIFICATION_ID, CATEGORY, TYPE, CREATED, NAME, DESCRIPTION, PRIORITY, SERVICE_LEVEL "
            + "FROM CLASSIFICATION "
            + "<where>"
            + "<if test='tenantId != null'>TENANT_ID = #{tenantId}</if> "
            + "<if test='parentClassificationId != null'>AND PARENT_CLASSIFICATION_ID IN(<foreach item='item' collection='parentClassificationId' separator=',' >#{item}</foreach>)</if> "
            + "<if test='category != null'>AND CATEGORY IN(<foreach item='item' collection='category' separator=',' >#{item}</foreach>)</if> "
            + "<if test='type != null'>AND TYPE IN(<foreach item='item' collection='type' separator=',' >#{item}</foreach>)</if> "
            + "<if test='domain != null'>AND DOMAIN IN(<foreach item='item' collection='domain' separator=',' >#{item}</foreach>)</if> "
            + "<if test='validInDomain != null'>AND VALID_IN_DOMAIN like #{validInDomain}</if> "
            + "<if test='created != null'>AND CREATED IN(<foreach item='item' collection='created' separator=',' >#{item}</foreach>)</if> "
            + "<if test='name != null'>AND NAME IN(<foreach item='item' collection='name' separator=',' >#{item}</foreach>)</if> "
            + "<if test='description != null'>AND DESCRIPTION like #{description}</if> "
            + "<if test='priority != null'>AND PRIORITY IN(<foreach item='item' collection='priority' separator=',' >#{item}</foreach>)</if> "
            + "<if test='serviceLevel != null'>AND SERVICE_LEVEL IN(<foreach item='item' collection='serviceLevel' separator=',' >#{item}</foreach>)</if> "
            + "<if test='customFields != null'>AND (CUSTOM_1 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR CUSTOM_2 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR CUSTOM_3 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR CUSTOM_4 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR CUSTOM_5 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR CUSTOM_6 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR CUSTOM_7 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR CUSTOM_8 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>))</if> "
            + "<if test='validFrom != null'>AND VALID_FROM IN(<foreach item='item' collection='validFrom' separator=',' >#{item}</foreach>)</if> "
            + "<if test='validUntil != null'>AND VALID_UNTIL IN(<foreach item='item' collection='validUntil' separator=',' >#{item}</foreach>)</if> "
            + "</where>"
            + "</script>")
    @Results({ @Result(property = "id", column = "ID"),
            @Result(property = "tenantId", column = "TENANT_ID"),
            @Result(property = "parentClassificationId", column = "PARENT_CLASSIFICATION_ID"),
            @Result(property = "category", column = "CATEGORY"),
            @Result(property = "type", column = "TYPE"),
            @Result(property = "created", column = "CREATED"),
            @Result(property = "name", column = "NAME"),
            @Result(property = "description", column = "DESCRIPTION"),
            @Result(property = "priority", column = "PRIORITY"),
            @Result(property = "serviceLevel", column = "SERVICE_LEVEL") })
    List<Classification> queryClassification(ClassificationQueryImpl classificationQuery);


    @Select("<script>SELECT ID, TENANT_ID, COMPANY, SYSTEM, SYSTEM_INSTANCE, TYPE, VALUE "
            + "FROM OBJECT_REFERENCE "
            + "<where>"
            + "<if test='tenantId != null'>TENANT_ID = #{tenantId}</if> "
            + "<if test='company != null'>AND COMPANY IN(<foreach item='item' collection='company' separator=',' >#{item}</foreach>)</if> "
            + "<if test='system != null'>AND SYSTEM IN(<foreach item='item' collection='system' separator=',' >#{item}</foreach>)</if> "
            + "<if test='systemInstance != null'>AND SYSTEM_INSTANCE IN(<foreach item='item' collection='systemInstance' separator=',' >#{item}</foreach>)</if> "
            + "<if test='type != null'>AND TYPE IN(<foreach item='item' collection='type' separator=',' >#{item}</foreach>)</if> "
            + "<if test='value != null'>AND VALUE IN(<foreach item='item' collection='value' separator=',' >#{item}</foreach>)</if> "
            + "</where>"
            + "</script>")
    @Results({
        @Result(property = "id", column = "ID"),
        @Result(property = "tenantId", column = "TENANT_ID"),
        @Result(property = "company", column = "COMPANY"),
        @Result(property = "system", column = "SYSTEM"),
        @Result(property = "systemInstance", column = "SYSTEM_INSTANCE"),
        @Result(property = "type", column = "TYPE"),
        @Result(property = "value", column = "VALUE") })
    List<ObjectReference> queryObjectReference(ObjectReferenceQueryImpl objectReference);
}
