package org.taskana.model.mappings;

import org.apache.ibatis.annotations.*;
import org.taskana.model.Category;

import java.util.List;

public interface CategoryMapper {

	@Select("SELECT * FROM BUSINESS_CATEGORY ORDER BY ID")
	@Results({
		@Result(property="id", column="ID"),
		@Result(property="tenantId", column="TENANT_ID"),
		@Result(property="parentCategoryId", column="PARENT_CATEGORY_ID"),
		@Result(property="created", column="CREATED"),
		@Result(property="modified", column="MODIFIED"),
		@Result(property="name", column="NAME"),
		@Result(property="description", column="DESCRIPTION"),
		@Result(property="priority", column="PRIORITY"),
		@Result(property="serviceLevel", column="SERVICE_LEVEL")
	})
	List<Category> findAll();
	
	@Select("SELECT * FROM BUSINESS_CATEGORY WHERE PARENT_CATEGORY_ID = #{parentCategoryId} ORDER BY ID")
	List<Category> findByParentId(@Param("parentCategoryId") String parentId);
	
	@Select("SELECT * FROM BUSINESS_CATEGORY WHERE ID = #{categoryId}")
	Category findById(@Param("categoryId") String categoryId);
	
	@Insert("INSERT INTO BUSINESS_CATEGORY (ID, TENANT_ID, PARENT_CATEGORY_ID, CREATED, NAME, DESCRIPTION, PRIORITY, SERVICE_LEVEL) VALUES (#{category.id}, #{category.tenantId}, #{category.parentCategoryId}, #{category.created}, #{category.name}, #{category.description}, #{category.priority}, #{category.serviceLevel})")
	void insert(@Param("category") Category category);
	
	@Update(value = "UPDATE BUSINESS_CATEGORY SET TENANT_ID = #{category.tenantId}, PARENT_CATEGORY_ID = #{category.parentCategoryId}, NAME = #{category.name}, DESCRIPTION = #{category.description}, PRIORITY = #{category.priority}, SERVICE_LEVEL = #{category.serviceLevel}, MODIFIED = #{category.modified} WHERE ID = #{category.id}")
	void update(@Param("category") Category category);
}
