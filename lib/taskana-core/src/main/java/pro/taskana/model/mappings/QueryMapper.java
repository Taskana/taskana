package pro.taskana.model.mappings;

import java.util.List;

import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import pro.taskana.impl.ClassificationQueryImpl;
import pro.taskana.impl.ClassificationSummaryImpl;
import pro.taskana.impl.ObjectReferenceQueryImpl;
import pro.taskana.impl.TaskQueryImpl;
import pro.taskana.impl.TaskSummaryImpl;
import pro.taskana.impl.WorkbasketAccessItemImpl;
import pro.taskana.impl.WorkbasketAccessItemQueryImpl;
import pro.taskana.impl.WorkbasketQueryImpl;
import pro.taskana.impl.WorkbasketSummaryImpl;
import pro.taskana.model.ObjectReference;

/**
 * This class provides a mapper for all queries.
 */
public interface QueryMapper {

    String OBJECTREFERENCEMAPPER_FINDBYID = "pro.taskana.model.mappings.ObjectReferenceMapper.findById";
    String CLASSIFICATION_FINDBYKEYANDDOMAIN = "pro.taskana.model.mappings.ClassificationMapper.findByKeyAndDomain";
    String CLASSIFICATION_FINDBYID = "pro.taskana.model.mappings.ClassificationMapper.findById";
    String WORKBASKET_FINDSUMMARYBYKEY = "pro.taskana.model.mappings.WorkbasketMapper.findSummaryByKey";

    @Select("<script>SELECT t.ID, t.CREATED, t.CLAIMED, t.COMPLETED, t.MODIFIED, t.PLANNED, t.DUE, t.NAME, t.DESCRIPTION, t.NOTE, t.PRIORITY, t.STATE, t.CLASSIFICATION_KEY, t.DOMAIN, t.WORKBASKET_KEY, t.BUSINESS_PROCESS_ID, t.PARENT_BUSINESS_PROCESS_ID, t.OWNER, t.POR_COMPANY, t.POR_SYSTEM, t.POR_INSTANCE, t.POR_TYPE, t.POR_VALUE, t.IS_READ, t.IS_TRANSFERRED, t.CUSTOM_1, t.CUSTOM_2, t.CUSTOM_3, t.CUSTOM_4, t.CUSTOM_5, t.CUSTOM_6, t.CUSTOM_7, t.CUSTOM_8, t.CUSTOM_9, t.CUSTOM_10 "
        + "FROM TASK t "
        + "<where>"
        + "<if test='taskIds != null'>AND t.ID IN(<foreach item='item' collection='taskIds' separator=',' >#{item}</foreach>)</if> "
        + "<if test='name != null'>AND t.NAME IN(<foreach item='item' collection='name' separator=',' >#{item}</foreach>)</if> "
        + "<if test='description != null'>AND t.DESCRIPTION like #{description}</if> "
        + "<if test='note != null'>AND t.NOTE like #{note}</if> "
        + "<if test='priority != null'>AND t.PRIORITY IN(<foreach item='item' collection='priority' separator=',' >#{item}</foreach>)</if> "
        + "<if test='states != null'>AND t.STATE IN(<foreach item='item' collection='states' separator=',' >#{item}</foreach>)</if> "
        + "<if test='workbasketKey != null'>AND t.WORKBASKET_KEY IN(<foreach item='item' collection='workbasketKey' separator=',' >#{item}</foreach>)</if> "
        + "<if test='classificationKey != null'>AND t.CLASSIFICATION_KEY IN(<foreach item='item' collection='classificationKey' separator=',' >#{item}</foreach>)</if> "
        + "<if test='domain != null'>AND t.DOMAIN IN(<foreach item='item' collection='domain' separator=',' >#{item}</foreach>)</if> "
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
        + "</where>"
        + "<if test='!orderBy.isEmpty()'>ORDER BY <foreach item='item' collection='orderBy' separator=',' >${item}</foreach></if> "
        + "</script>")
    @Results(value = {@Result(property = "taskId", column = "ID"),
        @Result(property = "created", column = "CREATED"),
        @Result(property = "claimed", column = "CLAIMED"),
        @Result(property = "completed", column = "COMPLETED"),
        @Result(property = "modified", column = "MODIFIED"),
        @Result(property = "planned", column = "PLANNED"),
        @Result(property = "due", column = "DUE"),
        @Result(property = "name", column = "NAME"),
        @Result(property = "note", column = "NOTE"),
        @Result(property = "priority", column = "PRIORITY"),
        @Result(property = "state", column = "STATE"),
        @Result(property = "domain", column = "DOMAIN"),
        @Result(property = "workbasketSummaryImpl.key", column = "WORKBASKET_KEY"),
        @Result(property = "classificationSummaryImpl.key", column = "CLASSIFICATION_KEY"),
        @Result(property = "businessProcessId", column = "BUSINESS_PROCESS_ID"),
        @Result(property = "parentBusinessProcessId", column = "PARENT_BUSINESS_PROCESS_ID"),
        @Result(property = "owner", column = "OWNER"),
        @Result(property = "primaryObjRef.company", column = "POR_COMPANY"),
        @Result(property = "primaryObjRef.system", column = "POR_SYSTEM"),
        @Result(property = "primaryObjRef.systemInstance", column = "POR_INSTANCE"),
        @Result(property = "primaryObjRef.type", column = "POR_TYPE"),
        @Result(property = "primaryObjRef.value", column = "POR_VALUE"),
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
    List<TaskSummaryImpl> queryTasks(TaskQueryImpl taskQuery);

    @Select("<script>SELECT ID, KEY, PARENT_CLASSIFICATION_KEY, CATEGORY, TYPE, DOMAIN, VALID_IN_DOMAIN, CREATED, NAME, DESCRIPTION, PRIORITY, SERVICE_LEVEL, APPLICATION_ENTRY_POINT, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8 "
        + "FROM CLASSIFICATION "
        + "<where>"
        + "<if test='key != null'>AND KEY IN(<foreach item='item' collection='key' separator=',' >#{item}</foreach>)</if> "
        + "<if test='parentClassificationKey != null'>AND PARENT_CLASSIFICATION_KEY IN(<foreach item='item' collection='parentClassificationKey' separator=',' >#{item}</foreach>)</if> "
        + "<if test='category != null'>AND CATEGORY IN(<foreach item='item' collection='category' separator=',' >#{item}</foreach>)</if> "
        + "<if test='type != null'>AND TYPE IN(<foreach item='item' collection='type' separator=',' >#{item}</foreach>)</if> "
        + "<if test='domain != null'>AND DOMAIN IN(<foreach item='item' collection='domain' separator=',' >#{item}</foreach>)</if> "
        + "<if test='validInDomain != null'>AND VALID_IN_DOMAIN = #{validInDomain}</if> "
        + "<if test='created != null'>AND CREATED IN(<foreach item='item' collection='created' separator=',' >SUBSTRING(#{item}, 1, 10)</foreach>)</if> "
        + "<if test='nameIn != null'>AND NAME IN(<foreach item='item' collection='nameIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='nameLike != null'>AND (<foreach item='item' collection='nameLike' separator=' OR '>NAME LIKE #{item}</foreach>)</if> "
        + "<if test='descriptionLike != null'>AND DESCRIPTION like #{descriptionLike}</if> "
        + "<if test='priority != null'>AND PRIORITY IN(<foreach item='item' collection='priority' separator=',' >#{item}</foreach>)</if> "
        + "<if test='serviceLevelIn != null'>AND SERVICE_LEVEL IN(<foreach item='item' collection='serviceLevelIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='serviceLevelLike != null'>AND (<foreach item='item' collection='serviceLevelLike' separator=' OR ' >SERVICE_LEVEL LIKE #{item}</foreach>)</if> "
        + "<if test='applicationEntryPointIn != null'>AND APPLICATION_ENTRY_POINT IN(<foreach item='item' collection='applicationEntryPoint' separator=',' >#{item}</foreach>)</if> "
        + "<if test='applicationEntryPointLike != null'>AND (<foreach item='item' collection='applicationEntryPointLike' separator=' OR ' >APPLICATION_ENTRY_POINT LIKE #{item}</foreach>)</if> "
        + "<if test='custom1In != null'>AND CUSTOM_1 IN(<foreach item='item' collection='custom1In' separator=',' >#{item}</foreach>) </if> "
        + "<if test='custom1Like != null'>AND (<foreach item='item' collection='custom1Like' separator=' OR ' >CUSTOM_1 LIKE #{item}</foreach>)</if> "
        + "<if test='custom2In != null'>AND CUSTOM_2 IN(<foreach item='item' collection='custom2In' separator=',' >#{item}</foreach>) </if> "
        + "<if test='custom2Like != null'>AND (<foreach item='item' collection='custom2Like' separator=' OR ' > CUSTOM_2 LIKE #{item}</foreach>)</if> "
        + "<if test='custom3In != null'>AND CUSTOM_3 IN(<foreach item='item' collection='custom3In' separator=',' >#{item}</foreach>) </if> "
        + "<if test='custom3Like != null'>AND (<foreach item='item' collection='custom3Like' separator=' OR ' > CUSTOM_3 LIKE #{item}</foreach>)</if> "
        + "<if test='custom4In != null'>AND CUSTOM_4 IN(<foreach item='item' collection='custom4In' separator=',' >#{item}</foreach>) </if> "
        + "<if test='custom4Like != null'>AND (<foreach item='item' collection='custom4Like' separator=' OR ' > CUSTOM_4 LIKE #{item}</foreach>)</if> "
        + "<if test='custom5In != null'>AND CUSTOM_5 IN(<foreach item='item' collection='custom5In' separator=',' >#{item}</foreach>) </if> "
        + "<if test='custom5Like != null'>AND (<foreach item='item' collection='custom5Like' separator=' OR ' > CUSTOM_5 LIKE #{item}</foreach>)</if> "
        + "<if test='custom6In != null'>AND CUSTOM_6 IN(<foreach item='item' collection='custom6In' separator=',' >#{item}</foreach>) </if> "
        + "<if test='custom6Like != null'>AND (<foreach item='item' collection='custom6Like' separator=' OR ' > CUSTOM_6 LIKE #{item}</foreach>)</if> "
        + "<if test='custom7In != null'>AND CUSTOM_7 IN(<foreach item='item' collection='custom7In' separator=',' >#{item}</foreach>) </if> "
        + "<if test='custom7Like != null'>AND (<foreach item='item' collection='custom7Like' separator=' OR ' > CUSTOM_7 LIKE #{item}</foreach>)</if> "
        + "<if test='custom8In != null'>AND CUSTOM_8 IN(<foreach item='item' collection='custom8In' separator=',' >#{item}</foreach>) </if> "
        + "<if test='custom8Like != null'>AND (<foreach item='item' collection='custom8Like' separator=' OR ' > CUSTOM_8 LIKE #{item}</foreach>)</if> "
        + "</where>"
        + "</script>")
    @Results({@Result(property = "id", column = "ID"),
        @Result(property = "key", column = "KEY"),
        @Result(property = "category", column = "CATEGORY"),
        @Result(property = "type", column = "TYPE"),
        @Result(property = "domain", column = "DOMAIN"),
        @Result(property = "name", column = "NAME")})
    List<ClassificationSummaryImpl> queryClassification(ClassificationQueryImpl classificationQuery);

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

    @Select("<script>"
        + "SELECT DISTINCT w.ID, w.KEY, w.NAME, w.DOMAIN, W.TYPE, w.DESCRIPTION, w.OWNER, w.ORG_LEVEL_1, w.ORG_LEVEL_2, w.ORG_LEVEL_3, w.ORG_LEVEL_4 from WORKBASKET w "
        + "<if test='accessId != null'>LEFT OUTER JOIN WORKBASKET_ACCESS_LIST a on w.KEY = a.WORKBASKET_KEY</if> "
        + "<where>"
        + "<if test='owner != null'>AND w.OWNER IN(<foreach item='item' collection='owner' separator=',' >#{item}</foreach>)</if> "
        + "<if test='keyIn != null'>AND UPPER(w.KEY) IN(<foreach item='item' collection='keyIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='keyLike != null'>AND (<foreach item='item' collection='keyLike' separator=' OR ' >UPPER(w.KEY) LIKE #{item}</foreach>)</if> "
        + "<if test='nameIn != null'>AND UPPER(w.NAME) IN(<foreach item='item' collection='nameIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='nameLike != null'>AND (<foreach item='item' collection='nameLike' separator=' OR ' >UPPER(w.NAME) LIKE #{item}</foreach>)</if> "
        + "<if test='keyOrNameLike != null'>AND (<foreach item='item' collection='keyOrNameLike' separator=' OR ' >UPPER(w.NAME) LIKE #{item} OR UPPER(w.KEY) LIKE #{item}</foreach>)</if> "
        + "<if test='domain != null'>AND w.DOMAIN IN(<foreach item='item' collection='domain' separator=',' >#{item}</foreach>)</if> "
        + "<if test='type!= null'>AND w.TYPE IN(<foreach item='item' collection='type' separator=',' >#{item}</foreach>)</if> "
        + "<if test='createdAfter != null'>AND w.CREATED &gt; #{createdAfter}</if> "
        + "<if test='createdBefore != null'>AND w.CREATED &lt; #{createdBefore}</if> "
        + "<if test='modifiedAfter != null'>AND w.MODIFIED &gt; #{modifiedAfter}</if> "
        + "<if test='modifiedBefore != null'>AND w.MODIFIED &lt; #{modifiedBefore}</if> "
        + "<if test='descriptionLike != null'>AND UPPER(w.DESCRIPTION) like #{descriptionLike}</if> "
        + "<if test='accessId != null'>AND a.ACCESS_ID IN(<foreach item='item' collection='accessId' separator=',' >#{item}</foreach>) AND PERM_READ = 1 </if> "
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
        + "<if test=\"authorization.name().equals('CUSTOM_8')\">PERM_CUSTOM_8</if>"
        + "<if test=\"authorization.name().equals('CUSTOM_9')\">PERM_CUSTOM_9</if>"
        + "<if test=\"authorization.name().equals('CUSTOM_10')\">PERM_CUSTOM_10</if>"
        + "<if test=\"authorization.name().equals('CUSTOM_11')\">PERM_CUSTOM_11</if>"
        + "<if test=\"authorization.name().equals('CUSTOM_12')\">PERM_CUSTOM_12</if> = 1 "
        + "</if>"
        + "</where>"
        + "<if test='!orderBy.isEmpty()'>ORDER BY <foreach item='orderItem' collection='orderBy' separator=',' >${orderItem}</foreach></if> "
        + "</script>")
    @Results({
        @Result(property = "id", column = "ID"),
        @Result(property = "key", column = "KEY"),
        @Result(property = "name", column = "NAME"),
        @Result(property = "description", column = "DESCRIPTION"),
        @Result(property = "owner", column = "OWNER"),
        @Result(property = "domain", column = "DOMAIN"),
        @Result(property = "type", column = "TYPE"),
        @Result(property = "orgLevel1", column = "ORG_LEVEL_1"),
        @Result(property = "orgLevel2", column = "ORG_LEVEL_2"),
        @Result(property = "orgLevel3", column = "ORG_LEVEL_3"),
        @Result(property = "orgLevel4", column = "ORG_LEVEL_4")})
    List<WorkbasketSummaryImpl> queryWorkbasket(WorkbasketQueryImpl workbasketQuery);

    @Select("<script>"
        + "SELECT "
        + "ID, WORKBASKET_KEY, ACCESS_ID, PERM_READ, PERM_OPEN, PERM_APPEND, PERM_TRANSFER, PERM_DISTRIBUTE, PERM_CUSTOM_1, PERM_CUSTOM_2, "
        + "PERM_CUSTOM_3, PERM_CUSTOM_4, PERM_CUSTOM_5, PERM_CUSTOM_6, PERM_CUSTOM_7, PERM_CUSTOM_8, PERM_CUSTOM_9, PERM_CUSTOM_10, PERM_CUSTOM_11, PERM_CUSTOM_12 "
        + "from WORKBASKET_ACCESS_LIST "
        + "<where>"
        + "<if test='workbasketKeyIn != null'>AND UPPER(WORKBASKET_KEY) IN(<foreach item='item' collection='workbasketKeyIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='accessIdIn != null'>AND ACCESS_ID IN(<foreach item='item' collection='accessIdIn' separator=',' >#{item}</foreach>) </if> "
        + "</where>"
        + "<if test='!orderBy.isEmpty()'>ORDER BY <foreach item='orderItem' collection='orderBy' separator=',' >${orderItem}</foreach></if> "
        + "</script>")
    @Results({
        @Result(property = "id", column = "ID"),
        @Result(property = "workbasketKey", column = "WORKBASKET_KEY"),
        @Result(property = "accessId", column = "ACCESS_ID"),
        @Result(property = "permRead", column = "PERM_READ"),
        @Result(property = "permOpen", column = "PERM_OPEN"),
        @Result(property = "permAppend", column = "PERM_APPEND"),
        @Result(property = "permTransfer", column = "PERM_TRANSFER"),
        @Result(property = "permDistribute", column = "PERM_DISTRIBUTE"),
        @Result(property = "permCustom1", column = "PERM_CUSTOM_1"),
        @Result(property = "permCustom2", column = "PERM_CUSTOM_2"),
        @Result(property = "permCustom3", column = "PERM_CUSTOM_3"),
        @Result(property = "permCustom4", column = "PERM_CUSTOM_4"),
        @Result(property = "permCustom5", column = "PERM_CUSTOM_5"),
        @Result(property = "permCustom6", column = "PERM_CUSTOM_6"),
        @Result(property = "permCustom7", column = "PERM_CUSTOM_7"),
        @Result(property = "permCustom8", column = "PERM_CUSTOM_8"),
        @Result(property = "permCustom9", column = "PERM_CUSTOM_9"),
        @Result(property = "permCustom10", column = "PERM_CUSTOM_10"),
        @Result(property = "permCustom11", column = "PERM_CUSTOM_11"),
        @Result(property = "permCustom12", column = "PERM_CUSTOM_12")})
    List<WorkbasketAccessItemImpl> queryWorkbasketAccessItem(WorkbasketAccessItemQueryImpl accessItemQuery);

    @Select("<script>SELECT COUNT(ID) FROM TASK t "
        + "<where>"
        + "<if test='name != null'>AND t.NAME IN(<foreach item='item' collection='name' separator=',' >#{item}</foreach>)</if> "
        + "<if test='description != null'>AND t.DESCRIPTION like #{description}</if> "
        + "<if test='note != null'>AND t.NOTE like #{note}</if> "
        + "<if test='priority != null'>AND t.PRIORITY IN(<foreach item='item' collection='priority' separator=',' >#{item}</foreach>)</if> "
        + "<if test='states != null'>AND t.STATE IN(<foreach item='item' collection='states' separator=',' >#{item}</foreach>)</if> "
        + "<if test='workbasketKey != null'>AND t.WORKBASKET_KEY IN(<foreach item='item' collection='workbasketKey' separator=',' >#{item}</foreach>)</if> "
        + "<if test='classificationKey != null'>AND t.CLASSIFICATION_KEY IN(<foreach item='item' collection='classificationKey' separator=',' >#{item}</foreach>)</if> "
        + "<if test='domain != null'>AND t.DOMAIN IN(<foreach item='item' collection='domain' separator=',' >#{item}</foreach>)</if> "
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
        + "</where>"
        + "</script>")
    Long countQueryTasks(TaskQueryImpl taskQuery);

    @Select("<script>SELECT COUNT(ID) FROM CLASSIFICATION "
        + "<where>"
        + "<if test='key != null'>AND KEY IN(<foreach item='item' collection='key' separator=',' >#{item}</foreach>)</if> "
        + "<if test='parentClassificationKey != null'>AND PARENT_CLASSIFICATION_KEY IN(<foreach item='item' collection='parentClassificationKey' separator=',' >#{item}</foreach>)</if> "
        + "<if test='category != null'>AND CATEGORY IN(<foreach item='item' collection='category' separator=',' >#{item}</foreach>)</if> "
        + "<if test='type != null'>AND TYPE IN(<foreach item='item' collection='type' separator=',' >#{item}</foreach>)</if> "
        + "<if test='domain != null'>AND DOMAIN IN(<foreach item='item' collection='domain' separator=',' >#{item}</foreach>)</if> "
        + "<if test='validInDomain != null'>AND VALID_IN_DOMAIN = #{validInDomain}</if> "
        + "<if test='created != null'>AND CREATED IN(<foreach item='item' collection='created' separator=',' >SUBSTRING(#{item}, 1, 10)</foreach>)</if> "
        + "<if test='nameIn != null'>AND NAME IN(<foreach item='item' collection='nameIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='nameLike != null'>AND (<foreach item='item' collection='nameLike' separator=' OR '>NAME LIKE #{item}</foreach>)</if> "
        + "<if test='descriptionLike != null'>AND DESCRIPTION like #{descriptionLike}</if> "
        + "<if test='priority != null'>AND PRIORITY IN(<foreach item='item' collection='priority' separator=',' >#{item}</foreach>)</if> "
        + "<if test='serviceLevelIn != null'>AND SERVICE_LEVEL IN(<foreach item='item' collection='serviceLevelIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='serviceLevelLike != null'>AND (<foreach item='item' collection='serviceLevelLike' separator=' OR ' >SERVICE_LEVEL LIKE #{item}</foreach>)</if> "
        + "<if test='applicationEntryPointIn != null'>AND APPLICATION_ENTRY_POINT IN(<foreach item='item' collection='applicationEntryPoint' separator=',' >#{item}</foreach>)</if> "
        + "<if test='applicationEntryPointLike != null'>AND (<foreach item='item' collection='applicationEntryPointLike' separator=' OR ' >APPLICATION_ENTRY_POINT LIKE #{item}</foreach>)</if> "
        + "<if test='custom1In != null'>AND CUSTOM_1 IN(<foreach item='item' collection='custom1In' separator=',' >#{item}</foreach>) </if> "
        + "<if test='custom1Like != null'>AND (<foreach item='item' collection='custom1Like' separator=' OR ' >CUSTOM_1 LIKE #{item}</foreach>)</if> "
        + "<if test='custom2In != null'>AND CUSTOM_2 IN(<foreach item='item' collection='custom2In' separator=',' >#{item}</foreach>) </if> "
        + "<if test='custom2Like != null'>AND (<foreach item='item' collection='custom2Like' separator=' OR ' > CUSTOM_2 LIKE #{item}</foreach>)</if> "
        + "<if test='custom3In != null'>AND CUSTOM_3 IN(<foreach item='item' collection='custom3In' separator=',' >#{item}</foreach>) </if> "
        + "<if test='custom3Like != null'>AND (<foreach item='item' collection='custom3Like' separator=' OR ' > CUSTOM_3 LIKE #{item}</foreach>)</if> "
        + "<if test='custom4In != null'>AND CUSTOM_4 IN(<foreach item='item' collection='custom4In' separator=',' >#{item}</foreach>) </if> "
        + "<if test='custom4Like != null'>AND (<foreach item='item' collection='custom4Like' separator=' OR ' > CUSTOM_4 LIKE #{item}</foreach>)</if> "
        + "<if test='custom5In != null'>AND CUSTOM_5 IN(<foreach item='item' collection='custom5In' separator=',' >#{item}</foreach>) </if> "
        + "<if test='custom5Like != null'>AND (<foreach item='item' collection='custom5Like' separator=' OR ' > CUSTOM_5 LIKE #{item}</foreach>)</if> "
        + "<if test='custom6In != null'>AND CUSTOM_6 IN(<foreach item='item' collection='custom6In' separator=',' >#{item}</foreach>) </if> "
        + "<if test='custom6Like != null'>AND (<foreach item='item' collection='custom6Like' separator=' OR ' > CUSTOM_6 LIKE #{item}</foreach>)</if> "
        + "<if test='custom7In != null'>AND CUSTOM_7 IN(<foreach item='item' collection='custom7In' separator=',' >#{item}</foreach>) </if> "
        + "<if test='custom7Like != null'>AND (<foreach item='item' collection='custom7Like' separator=' OR ' > CUSTOM_7 LIKE #{item}</foreach>)</if> "
        + "<if test='custom8In != null'>AND CUSTOM_8 IN(<foreach item='item' collection='custom8In' separator=',' >#{item}</foreach>) </if> "
        + "<if test='custom8Like != null'>AND (<foreach item='item' collection='custom8Like' separator=' OR ' > CUSTOM_8 LIKE #{item}</foreach>)</if> "
        + "</where>"
        + "</script>")
    Long countQueryClassifications(ClassificationQueryImpl classificationQuery);

    @Select("<script>SELECT COUNT(ID) FROM OBJECT_REFERENCE "
        + "<where>"
        + "<if test='company != null'>AND COMPANY IN(<foreach item='item' collection='company' separator=',' >#{item}</foreach>)</if> "
        + "<if test='system != null'>AND SYSTEM IN(<foreach item='item' collection='system' separator=',' >#{item}</foreach>)</if> "
        + "<if test='systemInstance != null'>AND SYSTEM_INSTANCE IN(<foreach item='item' collection='systemInstance' separator=',' >#{item}</foreach>)</if> "
        + "<if test='type != null'>AND TYPE IN(<foreach item='item' collection='type' separator=',' >#{item}</foreach>)</if> "
        + "<if test='value != null'>AND VALUE IN(<foreach item='item' collection='value' separator=',' >#{item}</foreach>)</if> "
        + "</where>"
        + "</script>")
    Long countQueryObjectReferences(ObjectReferenceQueryImpl objectReference);

    @Select("<script>SELECT COUNT(ID) from WORKBASKET w"
        + "<if test='accessId != null'>LEFT OUTER JOIN WORKBASKET_ACCESS_LIST a on w.KEY = a.WORKBASKET_KEY</if> "
        + "<where>"
        + "<if test='owner != null'>AND w.OWNER IN(<foreach item='item' collection='owner' separator=',' >#{item}</foreach>)</if> "
        + "<if test='keyIn != null'>AND UPPER(w.KEY) IN(<foreach item='item' collection='keyIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='keyLike != null'>AND (<foreach item='item' collection='keyLike' separator=' OR ' >UPPER(w.KEY) LIKE #{item}</foreach>)</if> "
        + "<if test='nameIn != null'>AND UPPER(w.NAME) IN(<foreach item='item' collection='nameIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='nameLike != null'>AND (<foreach item='item' collection='nameLike' separator=' OR ' >UPPER(w.NAME) LIKE #{item}</foreach>)</if> "
        + "<if test='keyOrNameLike != null'>AND (<foreach item='item' collection='keyOrNameLike' separator=' OR ' >UPPER(w.NAME) LIKE #{item} OR UPPER(w.KEY) LIKE #{item}</foreach>)</if> "
        + "<if test='domain != null'>AND w.DOMAIN IN(<foreach item='item' collection='domain' separator=',' >#{item}</foreach>)</if> "
        + "<if test='type!= null'>AND w.TYPE IN(<foreach item='item' collection='type' separator=',' >#{item}</foreach>)</if> "
        + "<if test='createdAfter != null'>AND w.CREATED &gt; #{createdAfter}</if> "
        + "<if test='createdBefore != null'>AND w.CREATED &lt; #{createdBefore}</if> "
        + "<if test='modifiedAfter != null'>AND w.MODIFIED &gt; #{modifiedAfter}</if> "
        + "<if test='modifiedBefore != null'>AND w.MODIFIED &lt; #{modifiedBefore}</if> "
        + "<if test='descriptionLike != null'>AND UPPER(w.DESCRIPTION) like #{descriptionLike}</if> "
        + "<if test='accessId != null'>AND a.ACCESS_ID IN(<foreach item='item' collection='accessId' separator=',' >#{item}</foreach>) AND PERM_READ = 1 </if> "
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
        + "<if test=\"authorization.name().equals('CUSTOM_8')\">PERM_CUSTOM_8</if>"
        + "<if test=\"authorization.name().equals('CUSTOM_9')\">PERM_CUSTOM_9</if>"
        + "<if test=\"authorization.name().equals('CUSTOM_10')\">PERM_CUSTOM_10</if>"
        + "<if test=\"authorization.name().equals('CUSTOM_11')\">PERM_CUSTOM_11</if>"
        + "<if test=\"authorization.name().equals('CUSTOM_12')\">PERM_CUSTOM_12</if> = 1 "
        + "</if>"
        + "</where>"
        + "</script>")
    Long countQueryWorkbaskets(WorkbasketQueryImpl workbasketQuery);

    @Select("<script>SELECT COUNT(ID) from WORKBASKET_ACCESS_LIST "
        + "<where>"
        + "<if test='workbasketKeyIn != null'>AND UPPER(WORKBASKET_KEY) IN(<foreach item='item' collection='workbasketKeyIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='accessIdIn != null'>AND ACCESS_ID IN(<foreach item='item' collection='accessIdIn' separator=',' >#{item}</foreach>) </if> "
        + "</where>"
        + "</script>")
    Long countQueryWorkbasketAccessItems(WorkbasketAccessItemQueryImpl accessItem);

}
