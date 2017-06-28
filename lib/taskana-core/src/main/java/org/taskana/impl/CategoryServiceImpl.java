package org.taskana.impl;

import org.taskana.CategoryService;
import org.taskana.model.Category;
import org.taskana.model.mappings.CategoryMapper;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class CategoryServiceImpl implements CategoryService {
	
	private CategoryMapper categoryMapper;
	
	public CategoryServiceImpl(CategoryMapper categoryMapper) {
		super();
		this.categoryMapper = categoryMapper;
	}

	@Override
	public List<Category> selectCategories() {
		final List<Category> rootCategories = categoryMapper.findByParentId("");
		populateChildcategories(rootCategories);
		return rootCategories;
	}

	private void populateChildcategories(final List<Category> categories) {
		for (Category category : categories) {
			List<Category> childCategories = categoryMapper.findByParentId(category.getId());
			category.setChildren(childCategories);
			populateChildcategories(childCategories);
		}
	}

	@Override
	public List<Category> selectCategoriesByParentId(String parentId) {
		return categoryMapper.findByParentId(parentId);
	}

	@Override
	public void insertCategory(Category category) {
		category.setId(UUID.randomUUID().toString());
		category.setCreated(Date.valueOf(LocalDate.now()));
		category.setModified(Date.valueOf(LocalDate.now()));
		this.checkServiceLevel(category);

		categoryMapper.insert(category);
	}

	@Override
	public void updateCategory(Category category) {
		category.setModified(Date.valueOf(LocalDate.now()));
		this.checkServiceLevel(category);

		categoryMapper.update(category);
	}

	@Override
	public Category selectCategoryById(String id) {
		return categoryMapper.findById(id);
	}

	private void checkServiceLevel(Category category){
		if(category.getServiceLevel()!= null){
			try {
				java.time.Duration.parse(category.getServiceLevel());
			} catch (Exception e){
				throw new IllegalArgumentException("Invalid timestamp. Please use the format 'PddDThhHmmM'");
			}
		}
	}
}
