package org.taskana.model.mappings;

import org.apache.ibatis.annotations.*;
import org.taskana.model.Classification;

import java.util.List;

/**
 * This class is the mybatis mapping of classifications.
 */
public interface ClassificationMapper {

    @Select("SELECT ID, TENANT_ID, PARENT_CLASSIFICATION_ID, CATEGORY, TYPE, DOMAIN, VALID_IN_DOMAIN, CREATED, MODIFIED, NAME, DESCRIPTION, PRIORITY, SERVICE_LEVEL, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8 "
            + "FROM CLASSIFICATION "
            + "ORDER BY ID")
    @Results({@Result(property = "id", column = "ID"),
            @Result(property = "tenantId", column = "TENANT_ID"),
            @Result(property = "parentClassificationId", column = "PARENT_CLASSIFICATION_ID"),
            @Result(property = "category", column = "CATEGORY"),
            @Result(property = "type", column = "TYPE"),
            @Result(property = "domain", column = "DOMAIN"),
            @Result(property = "isValidInDomain", column = "VALID_IN_DOMAIN"),
            @Result(property = "created", column = "CREATED"),
            @Result(property = "modified", column = "MODIFIED"),
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
            @Result(property = "custom8", column = "CUSTOM_8")})
    List<Classification> findAll();

    @Select("SELECT ID, TENANT_ID, PARENT_CLASSIFICATION_ID, CATEGORY, TYPE, DOMAIN, VALID_IN_DOMAIN, CREATED, MODIFIED, NAME, DESCRIPTION, PRIORITY, SERVICE_LEVEL, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8 "
            + "FROM CLASSIFICATION "
            + "WHERE PARENT_CLASSIFICATION_ID = #{parentClassificationId} "
            + "ORDER BY ID")
    @Results({@Result(property = "id", column = "ID"),
            @Result(property = "tenantId", column = "TENANT_ID"),
            @Result(property = "parentClassificationId", column = "PARENT_CLASSIFICATION_ID"),
            @Result(property = "category", column = "CATEGORY"),
            @Result(property = "type", column = "TYPE"),
            @Result(property = "domain", column = "DOMAIN"),
            @Result(property = "isValidInDomain", column = "VALID_IN_DOMAIN"),
            @Result(property = "created", column = "CREATED"),
            @Result(property = "modified", column = "MODIFIED"),
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
            @Result(property = "custom8", column = "CUSTOM_8")})
    List<Classification> findByParentId(@Param("parentClassificationId") String parentId);

    @Select("SELECT ID, TENANT_ID, PARENT_CLASSIFICATION_ID, CATEGORY, TYPE, DOMAIN, VALID_IN_DOMAIN, CREATED, MODIFIED, NAME, DESCRIPTION, PRIORITY, SERVICE_LEVEL, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8 "
            + "FROM CLASSIFICATION "
            + "WHERE ID = #{id}")
    @Results({@Result(property = "id", column = "ID"),
            @Result(property = "tenantId", column = "TENANT_ID"),
            @Result(property = "parentClassificationId", column = "PARENT_CLASSIFICATION_ID"),
            @Result(property = "category", column = "CATEGORY"),
            @Result(property = "type", column = "TYPE"),
            @Result(property = "domain", column = "DOMAIN"),
            @Result(property = "isValidInDomain", column = "VALID_IN_DOMAIN"),
            @Result(property = "created", column = "CREATED"),
            @Result(property = "modified", column = "MODIFIED"),
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
            @Result(property = "custom8", column = "CUSTOM_8")})
    Classification findById(@Param("id") String id);

    @Insert("INSERT INTO CLASSIFICATION (ID, TENANT_ID, PARENT_CLASSIFICATION_ID, CATEGORY, TYPE, DOMAIN, VALID_IN_DOMAIN, CREATED, NAME, DESCRIPTION, PRIORITY, SERVICE_LEVEL, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8) VALUES (#{classification.id}, #{classification.tenantId}, #{classification.parentClassificationId}, #{classification.category}, #{classification.type}, #{classification.domain}, #{classification.isValidInDomain}, #{classification.created}, #{classification.name}, #{classification.description}, #{classification.priority}, #{classification.serviceLevel}, #{classification.custom1}, #{classification.custom2}, #{classification.custom3}, #{classification.custom4}, #{classification.custom5}, #{classification.custom6}, #{classification.custom7}, #{classification.custom8})")
    void insert(@Param("classification") Classification classification);

@Update(value = "UPDATE CLASSIFICATION SET TENANT_ID = #{classification.tenantId}, PARENT_CLASSIFICATION_ID = #{classification.parentClassificationId}, CATEGORY = #{classification.category}, TYPE = #{classification.type}, NAME = #{classification.name}, DESCRIPTION = #{classification.description}, PRIORITY = #{classification.priority}, SERVICE_LEVEL = #{classification.serviceLevel}, DOMAIN = #{classification.domain}, VALID_IN_DOMAIN = #{classification.isValidInDomain}, MODIFIED = #{classification.modified}, CUSTOM_1 = #{classification.custom1}, CUSTOM_2 = #{classification.custom2}, CUSTOM_3 = #{classification.custom3}, CUSTOM_4 = #{classification.custom4}, CUSTOM_5 = #{classification.custom5}, CUSTOM_6 = #{classification.custom6}, CUSTOM_7 = #{classification.custom7}, CUSTOM_8 = #{classification.custom8} WHERE ID = #{classification.id}")
    void update(@Param("classification") Classification classification);

    @Select("SELECT ID, TENANT_ID, PARENT_CLASSIFICATION_ID, CATEGORY, TYPE, DOMAIN, VALID_IN_DOMAIN, CREATED, MODIFIED, NAME, DESCRIPTION, PRIORITY, SERVICE_LEVEL, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8"
            + "FROM CLASSIFICATION "
            + "WHERE DOMAIN = #{domain}"
            + "ORDER BY ID")
    @Results({@Result(property = "id", column = "ID"),
            @Result(property = "tenantId", column = "TENANT_ID"),
            @Result(property = "parentClassificationId", column = "PARENT_CLASSIFICATION_ID"),
            @Result(property = "category", column = "CATEGORY"),
            @Result(property = "type", column = "TYPE"),
            @Result(property = "domain", column = "DOMAIN"),
            @Result(property = "isValidInDomain", column = "VALID_IN_DOMAIN"),
            @Result(property = "created", column = "CREATED"),
            @Result(property = "modified", column = "MODIFIED"),
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
            @Result(property = "custom8", column = "CUSTOM_8")})
    List<Classification> findByDomain(@Param("domain") String domain);

    @Select("<script>"
            + "SELECT * "
            + "FROM CLASSIFICATION "
            + "WHERE DOMAIN = #{domain} "
            + "AND CATEGORY = #{category} "
            + "</script>")
    List<Classification> getClassificationByDomainAndCategory(@Param("domain") String domain, @Param("category") String category);

    @Select("<script>"
            + "SELECT * "
            + "FROM CLASSIFICATION "
            + "WHERE DOMAIN = #{domain} "
            + "AND TYPE = #{type} "
            + "</script>")
    List<Classification> getClassificationByDomainAndType(@Param("domain") String domain, @Param("type") String type);

    @Select("<script>"
            + "SELECT * "
            + "FROM CLASSIFICATION "
            + "WHERE CATEGORY = #{category} "
            + "AND TYPE = #{type} "
            + "</script>")
    List<Classification> getClassificationByCategoryAndType(@Param("category") String category, @Param("type") String type);

}

