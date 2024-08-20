package io.kadai.classification.internal;

import io.kadai.classification.internal.models.ClassificationSummaryImpl;
import java.util.List;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;

/** This class provides a mapper for all classification queries. */
@SuppressWarnings({"checkstyle:LineLength", "checkstyle:Indentation"})
public interface ClassificationQueryMapper {
  @Select(
      "<script>SELECT ID, KEY, PARENT_ID, PARENT_KEY, CATEGORY, TYPE, DOMAIN, VALID_IN_DOMAIN, CREATED, NAME, DESCRIPTION, PRIORITY, SERVICE_LEVEL, APPLICATION_ENTRY_POINT, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8 "
          + "FROM CLASSIFICATION "
          + "<where>"
          + "<if test='key != null'>AND KEY IN(<foreach item='item' collection='key' separator=',' >#{item}</foreach>)</if> "
          + "<if test='idIn != null'>AND ID IN(<foreach item='item' collection='idIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='parentId != null'>AND PARENT_ID IN(<foreach item='item' collection='parentId' separator=',' >#{item}</foreach>)</if> "
          + "<if test='parentKey != null'>AND PARENT_KEY IN(<foreach item='item' collection='parentKey' separator=',' >#{item}</foreach>)</if> "
          + "<if test='category != null'>AND CATEGORY IN(<foreach item='item' collection='category' separator=',' >#{item}</foreach>)</if> "
          + "<if test='type != null'>AND TYPE IN(<foreach item='item' collection='type' separator=',' >#{item}</foreach>)</if> "
          + "<if test='domain != null'>AND DOMAIN IN(<foreach item='item' collection='domain' separator=',' >#{item}</foreach>)</if> "
          + "<if test='validInDomain != null'>AND VALID_IN_DOMAIN = #{validInDomain}</if> "
          + "<if test='createdIn !=null'> AND ( <foreach item='item' collection='createdIn' separator=' OR ' > ( <if test='item.begin!=null'> CREATED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> CREATED &lt;=#{item.end} </if>)</foreach>)</if> "
          + "<if test='modifiedIn !=null'> AND ( <foreach item='item' collection='modifiedIn' separator=' OR ' > ( <if test='item.begin!=null'> MODIFIED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> MODIFIED &lt;=#{item.end} </if>)</foreach>)</if> "
          + "<if test='nameIn != null'>AND NAME IN(<foreach item='item' collection='nameIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='nameLike != null'>AND (<foreach item='item' collection='nameLike' separator=' OR '>LOWER(NAME) LIKE #{item}</foreach>)</if> "
          + "<if test='descriptionLike != null'>AND LOWER(DESCRIPTION) like #{descriptionLike}</if> "
          + "<if test='priority != null'>AND PRIORITY IN(<foreach item='item' collection='priority' separator=',' >#{item}</foreach>)</if> "
          + "<if test='serviceLevelIn != null'>AND SERVICE_LEVEL IN(<foreach item='item' collection='serviceLevelIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='serviceLevelLike != null'>AND (<foreach item='item' collection='serviceLevelLike' separator=' OR ' >LOWER(SERVICE_LEVEL) LIKE #{item}</foreach>)</if> "
          + "<if test='applicationEntryPointIn != null'>AND APPLICATION_ENTRY_POINT IN(<foreach item='item' collection='applicationEntryPointIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='applicationEntryPointLike != null'>AND (<foreach item='item' collection='applicationEntryPointLike' separator=' OR ' >LOWER(APPLICATION_ENTRY_POINT) LIKE #{item}</foreach>)</if> "
          + "<if test='custom1In != null'>AND CUSTOM_1 IN(<foreach item='item' collection='custom1In' separator=',' >#{item}</foreach>) </if> "
          + "<if test='custom1Like != null'>AND (<foreach item='item' collection='custom1Like' separator=' OR ' > LOWER(CUSTOM_1) LIKE #{item}</foreach>)</if> "
          + "<if test='custom2In != null'>AND CUSTOM_2 IN(<foreach item='item' collection='custom2In' separator=',' >#{item}</foreach>) </if> "
          + "<if test='custom2Like != null'>AND (<foreach item='item' collection='custom2Like' separator=' OR ' > LOWER(CUSTOM_2) LIKE #{item}</foreach>)</if> "
          + "<if test='custom3In != null'>AND CUSTOM_3 IN(<foreach item='item' collection='custom3In' separator=',' >#{item}</foreach>) </if> "
          + "<if test='custom3Like != null'>AND (<foreach item='item' collection='custom3Like' separator=' OR ' > LOWER(CUSTOM_3) LIKE #{item}</foreach>)</if> "
          + "<if test='custom4In != null'>AND CUSTOM_4 IN(<foreach item='item' collection='custom4In' separator=',' >#{item}</foreach>) </if> "
          + "<if test='custom4Like != null'>AND (<foreach item='item' collection='custom4Like' separator=' OR ' > LOWER(CUSTOM_4) LIKE #{item}</foreach>)</if> "
          + "<if test='custom5In != null'>AND CUSTOM_5 IN(<foreach item='item' collection='custom5In' separator=',' >#{item}</foreach>) </if> "
          + "<if test='custom5Like != null'>AND (<foreach item='item' collection='custom5Like' separator=' OR ' > LOWER(CUSTOM_5) LIKE #{item}</foreach>)</if> "
          + "<if test='custom6In != null'>AND CUSTOM_6 IN(<foreach item='item' collection='custom6In' separator=',' >#{item}</foreach>) </if> "
          + "<if test='custom6Like != null'>AND (<foreach item='item' collection='custom6Like' separator=' OR ' > LOWER(CUSTOM_6) LIKE #{item}</foreach>)</if> "
          + "<if test='custom7In != null'>AND CUSTOM_7 IN(<foreach item='item' collection='custom7In' separator=',' >#{item}</foreach>) </if> "
          + "<if test='custom7Like != null'>AND (<foreach item='item' collection='custom7Like' separator=' OR ' > LOWER(CUSTOM_7) LIKE #{item}</foreach>)</if> "
          + "<if test='custom8In != null'>AND CUSTOM_8 IN(<foreach item='item' collection='custom8In' separator=',' >#{item}</foreach>) </if> "
          + "<if test='custom8Like != null'>AND (<foreach item='item' collection='custom8Like' separator=' OR ' > LOWER(CUSTOM_8) LIKE #{item}</foreach>)</if> "
          + "</where>"
          + "<if test='!orderBy.isEmpty()'>ORDER BY <foreach item='item' collection='orderBy' separator=',' >${item}</foreach></if> "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  @Result(property = "id", column = "ID")
  @Result(property = "key", column = "KEY")
  @Result(property = "category", column = "CATEGORY")
  @Result(property = "type", column = "TYPE")
  @Result(property = "domain", column = "DOMAIN")
  @Result(property = "name", column = "NAME")
  @Result(property = "priority", column = "PRIORITY")
  @Result(property = "serviceLevel", column = "SERVICE_LEVEL")
  @Result(property = "parentId", column = "PARENT_ID")
  @Result(property = "parentKey", column = "PARENT_KEY")
  @Result(property = "applicationEntryPoint", column = "APPLICATION_ENTRY_POINT")
  @Result(property = "custom1", column = "CUSTOM_1")
  @Result(property = "custom2", column = "CUSTOM_2")
  @Result(property = "custom3", column = "CUSTOM_3")
  @Result(property = "custom4", column = "CUSTOM_4")
  @Result(property = "custom5", column = "CUSTOM_5")
  @Result(property = "custom6", column = "CUSTOM_6")
  @Result(property = "custom7", column = "CUSTOM_7")
  @Result(property = "custom8", column = "CUSTOM_8")
  List<ClassificationSummaryImpl> queryClassificationSummaries(
      ClassificationQueryImpl classificationQuery);

  @Select(
      "<script>SELECT COUNT(ID) FROM CLASSIFICATION "
          + "<where>"
          + "<if test='key != null'>AND KEY IN(<foreach item='item' collection='key' separator=',' >#{item}</foreach>)</if> "
          + "<if test='idIn != null'>AND ID IN(<foreach item='item' collection='idIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='parentId != null'>AND PARENT_ID IN(<foreach item='item' collection='parentId' separator=',' >#{item}</foreach>)</if> "
          + "<if test='parentKey != null'>AND PARENT_KEY IN(<foreach item='item' collection='parentKey' separator=',' >#{item}</foreach>)</if> "
          + "<if test='category != null'>AND CATEGORY IN(<foreach item='item' collection='category' separator=',' >#{item}</foreach>)</if> "
          + "<if test='type != null'>AND TYPE IN(<foreach item='item' collection='type' separator=',' >#{item}</foreach>)</if> "
          + "<if test='domain != null'>AND DOMAIN IN(<foreach item='item' collection='domain' separator=',' >#{item}</foreach>)</if> "
          + "<if test='validInDomain != null'>AND VALID_IN_DOMAIN = #{validInDomain}</if> "
          + "<if test='createdIn !=null'> AND ( <foreach item='item' collection='createdIn' separator=' OR ' > ( <if test='item.begin!=null'> CREATED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> CREATED &lt;=#{item.end} </if>)</foreach>)</if> "
          + "<if test='modifiedIn !=null'> AND ( <foreach item='item' collection='modifiedIn' separator=' OR ' > ( <if test='item.begin!=null'> MODIFIED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> MODIFIED &lt;=#{item.end} </if>)</foreach>)</if> "
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
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  Long countQueryClassifications(ClassificationQueryImpl classificationQuery);

  @Select(
      "<script>SELECT DISTINCT ${columnName} "
          + "FROM CLASSIFICATION"
          + "<where>"
          + "<if test='key != null'>AND KEY IN(<foreach item='item' collection='key' separator=',' >#{item}</foreach>)</if> "
          + "<if test='idIn != null'>AND ID IN(<foreach item='item' collection='idIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='parentId != null'>AND PARENT_ID IN(<foreach item='item' collection='parentId' separator=',' >#{item}</foreach>)</if> "
          + "<if test='parentKey != null'>AND PARENT_KEY IN(<foreach item='item' collection='parentKey' separator=',' >#{item}</foreach>)</if> "
          + "<if test='category != null'>AND CATEGORY IN(<foreach item='item' collection='category' separator=',' >#{item}</foreach>)</if> "
          + "<if test='type != null'>AND TYPE IN(<foreach item='item' collection='type' separator=',' >#{item}</foreach>)</if> "
          + "<if test='domain != null'>AND DOMAIN IN(<foreach item='item' collection='domain' separator=',' >#{item}</foreach>)</if> "
          + "<if test='validInDomain != null'>AND VALID_IN_DOMAIN = #{validInDomain}</if> "
          + "<if test='createdIn !=null'> AND ( <foreach item='item' collection='createdIn' separator=' OR ' > ( <if test='item.begin!=null'> CREATED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> CREATED &lt;=#{item.end} </if>)</foreach>)</if> "
          + "<if test='modifiedIn !=null'> AND ( <foreach item='item' collection='modifiedIn' separator=' OR ' > ( <if test='item.begin!=null'> MODIFIED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> MODIFIED &lt;=#{item.end} </if>)</foreach>)</if> "
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
          + "<if test='!orderBy.isEmpty()'>ORDER BY <foreach item='item' collection='orderBy' separator=',' >${item}</foreach></if> "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  List<String> queryClassificationColumnValues(ClassificationQueryImpl classificationQuery);
}
