package pro.taskana.model.mappings;

import java.util.List;

import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import pro.taskana.impl.ClassificationImpl;
import pro.taskana.impl.ClassificationQueryImpl;
import pro.taskana.impl.ObjectReferenceQueryImpl;
import pro.taskana.impl.TaskImpl;
import pro.taskana.impl.TaskQueryImpl;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.impl.WorkbasketQueryImpl;
import pro.taskana.model.ObjectReference;

/**
 * This class provides a mapper for all queries.
 */
public interface QueryMapper {

    String OBJECTREFERENCEMAPPER_FINDBYID = "pro.taskana.model.mappings.ObjectReferenceMapper.findById";
    String CLASSIFICATION_FINDBYKEYANDDOMAIN = "pro.taskana.model.mappings.ClassificationMapper.findByKeyAndDomain";
    String CLASSIFICATION_FINDBYID = "pro.taskana.model.mappings.ClassificationMapper.findById";

    @Select("<script>SELECT t.ID, t.CREATED, t.CLAIMED, t.COMPLETED, t.MODIFIED, t.PLANNED, t.DUE, t.NAME, t.DESCRIPTION, t.PRIORITY, t.STATE, t.CLASSIFICATION_KEY, t.WORKBASKET_KEY, t.OWNER, t.POR_COMPANY, t.POR_SYSTEM, t.POR_INSTANCE, t.POR_TYPE, t.POR_VALUE, t.IS_READ, t.IS_TRANSFERRED, t.CUSTOM_1, t.CUSTOM_2, t.CUSTOM_3, t.CUSTOM_4, t.CUSTOM_5, t.CUSTOM_6, t.CUSTOM_7, t.CUSTOM_8, t.CUSTOM_9, t.CUSTOM_10 "
        + "FROM TASK t "
        // Joins if Classification Query is needed
        + "<if test='classificationQuery != null'>LEFT OUTER JOIN CLASSIFICATION c on t.CLASSIFICATION_KEY = c.KEY</if> "
        + "<where>"
        + "<if test='name != null'>AND t.NAME IN(<foreach item='item' collection='name' separator=',' >#{item}</foreach>)</if> "
        + "<if test='description != null'>AND t.DESCRIPTION like #{description}</if> "
        + "<if test='priority != null'>AND t.PRIORITY IN(<foreach item='item' collection='priority' separator=',' >#{item}</foreach>)</if> "
        + "<if test='states != null'>AND t.STATE IN(<foreach item='item' collection='states' separator=',' >#{item}</foreach>)</if> "
        + "<if test='workbasketKey != null'>AND t.WORKBASKET_KEY IN(<foreach item='item' collection='workbasketKey' separator=',' >#{item}</foreach>)</if> "
        + "<if test='domain != null'>AND t.DOMAIN IN(<foreach item='item' collection='owner' separator=',' >#{item}</foreach>)</if> "
        + "<if test='owner != null'>AND t.OWNER IN(<foreach item='item' collection='owner' separator=',' >#{item}</foreach>)</if> "
        + "<if test='isRead != null'>AND t.IS_READ = #{isRead}</if> "
        + "<if test='isTransferred != null'>AND t.IS_TRANSFERRED = #{isTransferred}</if> "
        + "<if test='porCompanyIn != null'>AND t.POR_COMPANY IN(<foreach item='item' collection='porCompanyIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='porCompanyLike != null'>AND t.POR_COMPANY like #{porCompanyLike}</if> "
        + "<if test='porSystemIn != null'>AND t.POR_SYSTEM IN(<foreach item='item' collection='porSystemIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='porSystemLike != null'>AND t.POR_SYSTEM like #{porSystemLike}</if> "
        + "<if test='porSystemInstanceIn != null'>AND t.POR_INSTANCE IN(<foreach item='item' collection='porSystemInstanceIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='porSystemInstanceLike != null'>AND t.POR_INSTANCE like #{porSystemInstanceLike}</if> "
        + "<if test='porTypeIn != null'>AND t.POR_TYPE IN(<foreach item='item' collection='porTypeIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='porTypeLike != null'>AND t.POR_TYPE like #{porTypeLike}</if> "
        + "<if test='porValueIn != null'>AND t.POR_VALUE IN(<foreach item='item' collection='porValueIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='porValueLike != null'>AND t.POR_VALUE like #{porValueLike}</if> "
        + "<if test='customFields != null'>AND (t.CUSTOM_1 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR t.CUSTOM_2 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR t.CUSTOM_3 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR t.CUSTOM_4 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR t.CUSTOM_5 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR t.CUSTOM_6 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR t.CUSTOM_7 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR t.CUSTOM_8 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR t.CUSTOM_9 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR t.CUSTOM_10 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>))</if> "
        // Classification Query
        + "<if test='classificationQuery != null'>"
        + "<if test='classificationQuery.key != null'>AND c.KEY IN(<foreach item='item' collection='classificationQuery.key' separator=',' >#{item}</foreach>)</if> "
        + "<if test='classificationQuery.parentClassificationKey != null'>AND c.PARENT_CLASSIFICATION_KEY IN(<foreach item='item' collection='classificationQuery.parentClassificationKey' separator=',' >#{item}</foreach>)</if> "
        + "<if test='classificationQuery.category != null'>AND c.CATEGORY IN(<foreach item='item' collection='classificationQuery.category' separator=',' >#{item}</foreach>)</if> "
        + "<if test='classificationQuery.type != null'>AND c.TYPE IN(<foreach item='item' collection='classificationQuery.type' separator=',' >#{item}</foreach>)</if> "
        + "<if test='classificationQuery.domain != null'>AND c.DOMAIN IN(<foreach item='item' collection='classificationQuery.domain' separator=',' >#{item}</foreach>)</if> "
        + "<if test='classificationQuery.validInDomain != null'>AND c.VALID_IN_DOMAIN = #{classificationQuery.validInDomain}</if> "
        + "<if test='classificationQuery.created != null'>AND c.CREATED IN(<foreach item='item' collection='classificationQuery.created' separator=',' >#{item}</foreach>)</if> "
        + "<if test='classificationQuery.name != null'>AND c.NAME IN(<foreach item='item' collection='classificationQuery.name' separator=',' >#{item}</foreach>)</if> "
        + "<if test='classificationQuery.description != null'>AND c.DESCRIPTION like #{classificationQuery.description}</if> "
        + "<if test='classificationQuery.priority != null'>AND c.PRIORITY IN(<foreach item='item' collection='classificationQuery.priority' separator=',' >#{item}</foreach>)</if> "
        + "<if test='classificationQuery.serviceLevel != null'>AND c.SERVICE_LEVEL IN(<foreach item='item' collection='classificationQuery.serviceLevel' separator=',' >#{item}</foreach>)</if> "
        + "<if test='classificationQuery.applicationEntryPoint != null'>AND c.APPLICATION_ENTRY_POINT IN(<foreach item='item' collection='classificationQuery.applicationEntryPoint' separator=',' >#{item}</foreach>)</if> "
        + "<if test='classificationQuery.customFields != null'>AND (c.CUSTOM_1 IN(<foreach item='item' collection='classificationQuery.customFields' separator=',' >#{item}</foreach>) OR c.CUSTOM_2 IN(<foreach item='item' collection='classificationQuery.customFields' separator=',' >#{item}</foreach>) OR c.CUSTOM_3 IN(<foreach item='item' collection='classificationQuery.customFields' separator=',' >#{item}</foreach>) OR c.CUSTOM_4 IN(<foreach item='item' collection='classificationQuery.customFields' separator=',' >#{item}</foreach>) OR c.CUSTOM_5 IN(<foreach item='item' collection='classificationQuery.customFields' separator=',' >#{item}</foreach>) OR c.CUSTOM_6 IN(<foreach item='item' collection='classificationQuery.customFields' separator=',' >#{item}</foreach>) OR c.CUSTOM_7 IN(<foreach item='item' collection='classificationQuery.customFields' separator=',' >#{item}</foreach>) OR c.CUSTOM_8 IN(<foreach item='item' collection='classificationQuery.customFields' separator=',' >#{item}</foreach>))</if> "
        + "<if test='classificationQuery.validFrom != null'>AND c.VALID_FROM IN(<foreach item='item' collection='classificationQuery.validFrom' separator=',' >#{item}</foreach>)</if> "
        + "<if test='classificationQuery.validUntil != null'>AND c.VALID_UNTIL IN(<foreach item='item' collection='classificationQuery.validUntil' separator=',' >#{item}</foreach>)</if> "
        + "</if>"
        + "</where>"
        + "</script>")
    @Results(value = {@Result(property = "id", column = "ID"),
        @Result(property = "created", column = "CREATED"),
        @Result(property = "claimed", column = "CLAIMED"),
        @Result(property = "completed", column = "COMPLETED"),
        @Result(property = "planned", column = "PLANNED"),
        @Result(property = "due", column = "DUE"),
        @Result(property = "name", column = "NAME"),
        @Result(property = "description", column = "DESCRIPTION"),
        @Result(property = "priority", column = "PRIORITY"),
        @Result(property = "state", column = "STATE"),
        @Result(property = "classification", column = "CLASSIFICATION_ID", javaType = ClassificationImpl.class,
            one = @One(select = CLASSIFICATION_FINDBYID)),
        @Result(property = "workbasketKey", column = "WORKBASKET_KEY"),
        @Result(property = "domain", column = "DOMAIN"),
        @Result(property = "owner", column = "OWNER"),
        @Result(property = "porCompany", column = "POR_COMPANY"),
        @Result(property = "porSystem", column = "POR_SYSTEM"),
        @Result(property = "porSystemInstance", column = "POR_INSTANCE"),
        @Result(property = "porType", column = "POR_TYPE"),
        @Result(property = "porValue", column = "POR_VALUE"),
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
        @Result(property = "custom10", column = "CUSTOM_10")})
    List<TaskImpl> queryTasks(TaskQueryImpl taskQuery);

    @Select("<script>SELECT ID, KEY, PARENT_CLASSIFICATION_KEY, CATEGORY, TYPE, DOMAIN, VALID_IN_DOMAIN, CREATED, NAME, DESCRIPTION, PRIORITY, SERVICE_LEVEL, APPLICATION_ENTRY_POINT, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8, VALID_FROM, VALID_UNTIL "
        + "FROM CLASSIFICATION "
        + "<where>"
        + "<if test='key != null'>AND KEY IN(<foreach item='item' collection='key' separator=',' >#{item}</foreach>)</if> "
        + "<if test='parentClassificationKey != null'>AND PARENT_CLASSIFICATION_KEY IN(<foreach item='item' collection='parentClassificationKey' separator=',' >#{item}</foreach>)</if> "
        + "<if test='category != null'>AND CATEGORY IN(<foreach item='item' collection='category' separator=',' >#{item}</foreach>)</if> "
        + "<if test='type != null'>AND TYPE IN(<foreach item='item' collection='type' separator=',' >#{item}</foreach>)</if> "
        + "<if test='domain != null'>AND DOMAIN IN(<foreach item='item' collection='domain' separator=',' >#{item}</foreach>)</if> "
        + "<if test='validInDomain != null'>AND VALID_IN_DOMAIN = #{validInDomain}</if> "
        + "<if test='created != null'>AND CREATED IN(<foreach item='item' collection='created' separator=',' >#{item}</foreach>)</if> "
        + "<if test='name != null'>AND NAME IN(<foreach item='item' collection='name' separator=',' >#{item}</foreach>)</if> "
        + "<if test='description != null'>AND DESCRIPTION like #{description}</if> "
        + "<if test='priority != null'>AND PRIORITY IN(<foreach item='item' collection='priority' separator=',' >#{item}</foreach>)</if> "
        + "<if test='serviceLevel != null'>AND SERVICE_LEVEL IN(<foreach item='item' collection='serviceLevel' separator=',' >#{item}</foreach>)</if> "
        + "<if test='applicationEntryPoint != null'>AND APPLICATION_ENTRY_POINT IN(<foreach item='item' collection='applicationEntryPoint' separator=',' >#{item}</foreach>)</if> "
        + "<if test='customFields != null'>AND (CUSTOM_1 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR CUSTOM_2 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR CUSTOM_3 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR CUSTOM_4 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR CUSTOM_5 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR CUSTOM_6 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR CUSTOM_7 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>) OR CUSTOM_8 IN(<foreach item='item' collection='customFields' separator=',' >#{item}</foreach>))</if> "
        + "<if test='validFrom != null'>AND VALID_FROM IN(<foreach item='item' collection='validFrom' separator=',' >#{item}</foreach>)</if> "
        + "<if test='validUntil != null'>AND VALID_UNTIL IN(<foreach item='item' collection='validUntil' separator=',' >#{item}</foreach>)</if> "
        + "</where>"
        + "</script>")
    @Results({@Result(property = "id", column = "ID"),
        @Result(property = "parentClassificationId", column = "PARENT_CLASSIFICATION_ID"),
        @Result(property = "category", column = "CATEGORY"),
        @Result(property = "type", column = "TYPE"),
        @Result(property = "domain", column = "DOMAIN"),
        @Result(property = "isValidInDomain", column = "VALID_IN_DOMAIN"),
        @Result(property = "created", column = "CREATED"),
        @Result(property = "name", column = "NAME"),
        @Result(property = "description", column = "DESCRIPTION"),
        @Result(property = "priority", column = "PRIORITY"),
        @Result(property = "serviceLevel", column = "SERVICE_LEVEL"),
        @Result(property = "custom1", column = "CUSTOM_1"),
        @Result(property = "custom2", column = "CUSTOM_2"),
        @Result(property = "custom3", column = "CUSTOM_3"),
        @Result(property = "custom4", column = "CUSTOM_4"),
        @Result(property = "custom5", column = "CUSTOM_5"),
        @Result(property = "custom6", column = "CUSTOM_6"),
        @Result(property = "custom7", column = "CUSTOM_7"),
        @Result(property = "custom8", column = "CUSTOM_8"),
        @Result(property = "validFrom", column = "VALID_FROM"),
        @Result(property = "validUntil", column = "VALID_UNTIL")})
    List<ClassificationImpl> queryClassification(ClassificationQueryImpl classificationQuery);

    @Select("<script>SELECT ID, COMPANY, SYSTEM, SYSTEM_INSTANCE, TYPE, VALUE "
        + "FROM OBJECT_REFERENCE "
        + "<where>"
        + "<if test='company != null'>AND COMPANY IN(<foreach item='item' collection='company' separator=',' >#{item}</foreach>)</if> "
        + "<if test='system != null'>AND SYSTEM IN(<foreach item='item' collection='system' separator=',' >#{item}</foreach>)</if> "
        + "<if test='systemInstance != null'>AND SYSTEM_INSTANCE IN(<foreach item='item' collection='systemInstance' separator=',' >#{item}</foreach>)</if> "
        + "<if test='type != null'>AND TYPE IN(<foreach item='item' collection='type' separator=',' >#{item}</foreach>)</if> "
        + "<if test='value != null'>AND VALUE IN(<foreach item='item' collection='value' separator=',' >#{item}</foreach>)</if> "
        + "</where>"
        + "</script>")
    @Results({
        @Result(property = "id", column = "ID"),
        @Result(property = "company", column = "COMPANY"),
        @Result(property = "system", column = "SYSTEM"),
        @Result(property = "systemInstance", column = "SYSTEM_INSTANCE"),
        @Result(property = "type", column = "TYPE"),
        @Result(property = "value", column = "VALUE")})
    List<ObjectReference> queryObjectReference(ObjectReferenceQueryImpl objectReference);

    @Select("<script>SELECT w.ID, w.KEY, w.CREATED, w.MODIFIED, w.NAME, w.DOMAIN, W.TYPE, w.DESCRIPTION, w.OWNER, w.CUSTOM_1, w.CUSTOM_2, w.CUSTOM_3, w.CUSTOM_4, w.ORG_LEVEL_1, w.ORG_LEVEL_2, w.ORG_LEVEL_3, w.ORG_LEVEL_4 from WORKBASKET w "
        + "<if test='accessId != null'>LEFT OUTER JOIN WORKBASKET_ACCESS_LIST a on w.KEY = a.WORKBASKET_KEY</if> "
        + "<where>"
        + "<if test='owner != null'>AND w.OWNER IN(<foreach item='item' collection='owner' separator=',' >#{item}</foreach>)</if> "
        + "<if test='key != null'>AND w.KEY IN(<foreach item='item' collection='key' separator=',' >#{item}</foreach>)</if> "
        + "<if test='name != null'>AND w.NAME IN(<foreach item='item' collection='name' separator=',' >#{item}</foreach>)</if> "
        + "<if test='domain != null'>AND w.DOMAIN IN(<foreach item='item' collection='domain' separator=',' >#{item}</foreach>)</if> "
        + "<if test='type!= null'>AND w.TYPE IN(<foreach item='item' collection='type' separator=',' >#{item}</foreach>)</if> "
        + "<if test='createdAfter != null'>AND w.CREATED &gt; #{createdAfter}</if> "
        + "<if test='createdBefore != null'>AND w.CREATED &lt; #{createdBefore}</if> "
        + "<if test='modifiedAfter != null'>AND w.MODIFIED &gt; #{modifiedAfter}</if> "
        + "<if test='modifiedBefore != null'>AND w.MODIFIED &lt; #{modifiedBefore}</if> "
        + "<if test='description != null'>AND w.DESCRIPTION like #{description}</if> "
        + "<if test='accessId != null'>AND a.ACCESS_ID IN(<foreach item='item' collection='accessId' separator=',' >#{item}</foreach>)</if> "
        + "<if test='authorization != null'>AND "
        + "<if test=\"authorization.name().equals('OPEN')\">PERM_OPEN</if> "
        + "<if test=\"authorization.name().equals('READ')\">PERM_READ</if>"
        + "<if test=\"authorization.name().equals('APPEND')\">PERM_APPEND</if>"
        + "<if test=\"authorization.name().equals('TRANSFER')\">PERM_TRANSFER</if>"
        + "<if test=\"authorization.name().equals('DISTRIBUTE')\">PERM_DISTRIBUTE</if>"
        + "<if test=\"authorization.name().equals('CUSTOM_1')\">PERM_CUSTOM_1</if>"
        + "<if test=\"authorization.name().equals('CUSTOM_2')\">PERM_CUSTOM_2</if>"
        + "<if test=\"authorization.name().equals('CUSTOM_3')\">PERM_CUSTOM_3</if>"
        + "<if test=\"authorization.name().equals('CUSTOM_4')\">PERM_CUSTOM_4</if>"
        + "<if test=\"authorization.name().equals('CUSTOM_5')\">PERM_CUSTOM_5</if>"
        + "<if test=\"authorization.name().equals('CUSTOM_6')\">PERM_CUSTOM_6</if>"
        + "<if test=\"authorization.name().equals('CUSTOM_7')\">PERM_CUSTOM_7</if>"
        + "<if test=\"authorization.name().equals('CUSTOM_8')\">PERM_CUSTOM_8</if> = 1 "
        + "</if>"
        + "</where>"
        + "</script>")
    @Results({
        @Result(property = "id", column = "ID"),
        @Result(property = "key", column = "KEY"),
        @Result(property = "name", column = "NAME"),
        @Result(property = "created", column = "CREATED"),
        @Result(property = "modified", column = "MODIFIED"),
        @Result(property = "domain", column = "DOMAIN"),
        @Result(property = "type", column = "TYPE"),
        @Result(property = "description", column = "DESCRIPTION"),
        @Result(property = "owner", column = "OWNER"),
        @Result(property = "distributionTargets", column = "id", javaType = List.class,
            many = @Many(select = "findDistributionTargets")),
        @Result(property = "custom1", column = "CUSTOM_1"),
        @Result(property = "custom2", column = "CUSTOM_2"),
        @Result(property = "custom3", column = "CUSTOM_3"),
        @Result(property = "custom4", column = "CUSTOM_4"),
        @Result(property = "orgLevel1", column = "ORG_LEVEL_1"),
        @Result(property = "orgLevel2", column = "ORG_LEVEL_2"),
        @Result(property = "orgLevel3", column = "ORG_LEVEL_3"),
        @Result(property = "orgLevel4", column = "ORG_LEVEL_4")})
    List<WorkbasketImpl> queryWorkbasket(WorkbasketQueryImpl workbasketQuery);

    @Select("<script>SELECT TARGET_ID from DISTRIBUTION_TARGETS "
        + "<where>"
        + "SOURCE_ID = #{sourceId}"
        + "</where>"
        + "</script>")
    @Results(value = {
        @Result(property = "distributionTarget", column = "TARGET_ID")})
    List<String> findDistributionTargets(String sourceId);

}
