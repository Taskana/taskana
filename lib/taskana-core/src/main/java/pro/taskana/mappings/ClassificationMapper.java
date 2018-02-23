package pro.taskana.mappings;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import pro.taskana.impl.ClassificationImpl;
import pro.taskana.impl.ClassificationSummaryImpl;

/**
 * This class is the mybatis mapping of classifications.
 */
public interface ClassificationMapper {

    @Select("SELECT ID, KEY, PARENT_ID, CATEGORY, TYPE, DOMAIN, VALID_IN_DOMAIN, CREATED, NAME, DESCRIPTION, PRIORITY, SERVICE_LEVEL, APPLICATION_ENTRY_POINT, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8 "
        + "FROM CLASSIFICATION "
        + "WHERE KEY = #{key}"
        + "AND DOMAIN = #{domain}")
    @Results({@Result(property = "id", column = "ID"),
        @Result(property = "key", column = "KEY"),
        @Result(property = "parentId", column = "PARENT_ID"),
        @Result(property = "category", column = "CATEGORY"),
        @Result(property = "type", column = "TYPE"),
        @Result(property = "domain", column = "DOMAIN"),
        @Result(property = "isValidInDomain", column = "VALID_IN_DOMAIN"),
        @Result(property = "created", column = "CREATED"),
        @Result(property = "name", column = "NAME"),
        @Result(property = "description", column = "DESCRIPTION"),
        @Result(property = "priority", column = "PRIORITY"),
        @Result(property = "serviceLevel", column = "SERVICE_LEVEL"),
        @Result(property = "applicationEntryPoint", column = "APPLICATION_ENTRY_POINT"),
        @Result(property = "custom1", column = "CUSTOM_1"),
        @Result(property = "custom2", column = "CUSTOM_2"),
        @Result(property = "custom3", column = "CUSTOM_3"),
        @Result(property = "custom4", column = "CUSTOM_4"),
        @Result(property = "custom5", column = "CUSTOM_5"),
        @Result(property = "custom6", column = "CUSTOM_6"),
        @Result(property = "custom7", column = "CUSTOM_7"),
        @Result(property = "custom8", column = "CUSTOM_8")})
    ClassificationImpl findByKeyAndDomain(@Param("key") String key, @Param("domain") String domain);

    @Select("SELECT ID, KEY, PARENT_ID, CATEGORY, TYPE, DOMAIN, VALID_IN_DOMAIN, CREATED, NAME, DESCRIPTION, PRIORITY, SERVICE_LEVEL, APPLICATION_ENTRY_POINT, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8 "
        + "FROM CLASSIFICATION "
        + "WHERE ID = #{id}")
    @Results({@Result(property = "id", column = "ID"),
        @Result(property = "key", column = "KEY"),
        @Result(property = "parentId", column = "PARENT_ID"),
        @Result(property = "category", column = "CATEGORY"),
        @Result(property = "type", column = "TYPE"),
        @Result(property = "domain", column = "DOMAIN"),
        @Result(property = "isValidInDomain", column = "VALID_IN_DOMAIN"),
        @Result(property = "created", column = "CREATED"),
        @Result(property = "name", column = "NAME"),
        @Result(property = "description", column = "DESCRIPTION"),
        @Result(property = "priority", column = "PRIORITY"),
        @Result(property = "serviceLevel", column = "SERVICE_LEVEL"),
        @Result(property = "applicationEntryPoint", column = "APPLICATION_ENTRY_POINT"),
        @Result(property = "custom1", column = "CUSTOM_1"),
        @Result(property = "custom2", column = "CUSTOM_2"),
        @Result(property = "custom3", column = "CUSTOM_3"),
        @Result(property = "custom4", column = "CUSTOM_4"),
        @Result(property = "custom5", column = "CUSTOM_5"),
        @Result(property = "custom6", column = "CUSTOM_6"),
        @Result(property = "custom7", column = "CUSTOM_7"),
        @Result(property = "custom8", column = "CUSTOM_8")})
    ClassificationImpl findById(@Param("id") String id);

    @Insert("INSERT INTO CLASSIFICATION (ID, KEY, PARENT_ID, CATEGORY, TYPE, DOMAIN, VALID_IN_DOMAIN, CREATED, NAME, DESCRIPTION, PRIORITY, SERVICE_LEVEL, APPLICATION_ENTRY_POINT, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8) VALUES (#{classification.id}, #{classification.key}, #{classification.parentId}, #{classification.category}, #{classification.type}, #{classification.domain}, #{classification.isValidInDomain}, #{classification.created}, #{classification.name}, #{classification.description}, #{classification.priority}, #{classification.serviceLevel}, #{classification.applicationEntryPoint}, #{classification.custom1}, #{classification.custom2}, #{classification.custom3}, #{classification.custom4}, #{classification.custom5}, #{classification.custom6}, #{classification.custom7}, #{classification.custom8})")
    void insert(@Param("classification") ClassificationImpl classification);

    @Update(
        value = "UPDATE CLASSIFICATION SET KEY = #{classification.key}, PARENT_ID = #{classification.parentId}, CATEGORY = #{classification.category}, TYPE = #{classification.type}, NAME = #{classification.name}, DESCRIPTION = #{classification.description}, PRIORITY = #{classification.priority}, SERVICE_LEVEL = #{classification.serviceLevel}, DOMAIN = #{classification.domain}, VALID_IN_DOMAIN = #{classification.isValidInDomain}, APPLICATION_ENTRY_POINT = #{classification.applicationEntryPoint}, CUSTOM_1 = #{classification.custom1}, CUSTOM_2 = #{classification.custom2}, CUSTOM_3 = #{classification.custom3}, CUSTOM_4 = #{classification.custom4}, CUSTOM_5 = #{classification.custom5}, CUSTOM_6 = #{classification.custom6}, CUSTOM_7 = #{classification.custom7}, CUSTOM_8 = #{classification.custom8} WHERE ID = #{classification.id}")
    void update(@Param("classification") ClassificationImpl classification);

    @Delete("DELETE FROM CLASSIFICATION "
        + "WHERE KEY = #{classificationKey}"
        + "AND DOMAIN = #{domain}")
    void deleteClassificationInDomain(@Param("classificationKey") String classificationKey,
        @Param("domain") String domain);

    @Select("<script>"
        + "SELECT ID, KEY, CATEGORY, TYPE, DOMAIN, NAME, PARENT_ID "
        + "FROM CLASSIFICATION "
        + "WHERE KEY = #{key} "
        + "AND DOMAIN = #{domain}"
        + "ORDER BY KEY DESC"
        + "</script>")
    @Results({@Result(property = "id", column = "ID"),
        @Result(property = "key", column = "KEY"),
        @Result(property = "category", column = "CATEGORY"),
        @Result(property = "type", column = "TYPE"),
        @Result(property = "domain", column = "DOMAIN"),
        @Result(property = "name", column = "NAME"),
        @Result(property = "parentId", column = "PARENT_ID")})
    List<ClassificationSummaryImpl> getAllClassificationsWithKey(@Param("key") String key,
        @Param("domain") String domain);

    @Select("<script>"
        + "SELECT DOMAIN "
        + "FROM CLASSIFICATION "
        + "WHERE KEY = #{classification_key} "
        + "</script>")
    List<String> getDomainsForClassification(@Param("classification_key") String classificationKey);

    @Select("SELECT KEY "
        + "FROM CLASSIFICATION "
        + "WHERE PARENT_ID = #{parentId}")
    List<String> getChildrenOfClassification(@Param("parentId") String parentId);
}
