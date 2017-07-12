package org.taskana.model.mappings;

import org.apache.ibatis.annotations.*;
import org.taskana.model.Classification;

import java.util.List;

/**
 * This class is the mybatis mapping of classifications.
 */
public interface ClassificationMapper {

    @Select("SELECT ID, TENANT_ID, PARENT_CLASSIFICATION_ID, CATEGORY, TYPE, CREATED, MODIFIED, NAME, DESCRIPTION, PRIORITY, SERVICE_LEVEL "
            + "FROM CLASSIFICATION "
            + "ORDER BY ID")
    @Results({ @Result(property = "id", column = "ID"),
        @Result(property = "tenantId", column = "TENANT_ID"),
        @Result(property = "parentClassificationId", column = "PARENT_CLASSIFICATION_ID"),
        @Result(property = "category", column = "CATEGORY"),
        @Result(property = "type", column = "TYPE"),
        @Result(property = "created", column = "CREATED"),
        @Result(property = "modified", column = "MODIFIED"),
        @Result(property = "name", column = "NAME"),
        @Result(property = "description", column = "DESCRIPTION"),
        @Result(property = "priority", column = "PRIORITY"),
        @Result(property = "serviceLevel", column = "SERVICE_LEVEL") })
    List<Classification> findAll();

    @Select("SELECT ID, TENANT_ID, PARENT_CLASSIFICATION_ID, CATEGORY, TYPE, CREATED, MODIFIED, NAME, DESCRIPTION, PRIORITY, SERVICE_LEVEL "
            + "FROM CLASSIFICATION "
            + "WHERE PARENT_CLASSIFICATION_ID = #{parentClassificationId} "
            + "ORDER BY ID")
    @Results({ @Result(property = "id", column = "ID"),
        @Result(property = "tenantId", column = "TENANT_ID"),
        @Result(property = "parentClassificationId", column = "PARENT_CLASSIFICATION_ID"),
        @Result(property = "category", column = "CATEGORY"),
        @Result(property = "type", column = "TYPE"),
        @Result(property = "created", column = "CREATED"),
        @Result(property = "modified", column = "MODIFIED"),
        @Result(property = "name", column = "NAME"),
        @Result(property = "description", column = "DESCRIPTION"),
        @Result(property = "priority", column = "PRIORITY"),
        @Result(property = "serviceLevel", column = "SERVICE_LEVEL") })
    List<Classification> findByParentId(@Param("parentClassificationId") String parentId);

    @Select("SELECT ID, TENANT_ID, PARENT_CLASSIFICATION_ID, CATEGORY, TYPE, CREATED, MODIFIED, NAME, DESCRIPTION, PRIORITY, SERVICE_LEVEL "
            + "FROM CLASSIFICATION "
            + "WHERE ID = #{id}")
    @Results({ @Result(property = "id", column = "ID"),
        @Result(property = "tenantId", column = "TENANT_ID"),
        @Result(property = "parentClassificationId", column = "PARENT_CLASSIFICATION_ID"),
        @Result(property = "category", column = "CATEGORY"),
        @Result(property = "type", column = "TYPE"),
        @Result(property = "created", column = "CREATED"),
        @Result(property = "modified", column = "MODIFIED"),
        @Result(property = "name", column = "NAME"),
        @Result(property = "description", column = "DESCRIPTION"),
        @Result(property = "priority", column = "PRIORITY"),
        @Result(property = "serviceLevel", column = "SERVICE_LEVEL") })
    Classification findById(@Param("id") String id);

    @Insert("INSERT INTO CLASSIFICATION (ID, TENANT_ID, PARENT_CLASSIFICATION_ID, CATEGORY, TYPE, CREATED, NAME, DESCRIPTION, PRIORITY, SERVICE_LEVEL) VALUES (#{classification.id}, #{classification.tenantId}, #{classification.parentClassificationId}, #{classification.category}, #{classification.type}, #{classification.created}, #{classification.name}, #{classification.description}, #{classification.priority}, #{classification.serviceLevel})")
    void insert(@Param("classification") Classification classification);

    @Update(value = "UPDATE CLASSIFICATION SET TENANT_ID = #{classification.tenantId}, PARENT_CLASSIFICATION_ID = #{classification.parentClassificationId}, CATEGORY = #{classification.category}, TYPE = #{classification.type}, NAME = #{classification.name}, DESCRIPTION = #{classification.description}, PRIORITY = #{classification.priority}, SERVICE_LEVEL = #{classification.serviceLevel}, MODIFIED = #{classification.modified} WHERE ID = #{classification.id}")
    void update(@Param("classification") Classification classification);
}
