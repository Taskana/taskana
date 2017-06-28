package org.taskana;

import org.taskana.model.Category;

import java.util.List;

public interface CategoryService {

	/**
	 * Get all available Categories
	 * 
	 * @return The List of all Categories
	 */
	public List<Category> selectCategories();

	/**
	 * Get all Categories with given parent
	 * 
	 * @param parentId
	 *            the ID of the parent Category
	 * @return
	 */
	public List<Category> selectCategoriesByParentId(String parentId);

	/**
	 * Get a Category for a given id
	 * 
	 * @param id 
	 * @return the requested Category
	 */
	public Category selectCategoryById(String id);

	/**
	 * Insert a new Category
	 * 
	 * @param category
	 *            the category to insert
	 */
	public void insertCategory(Category category);

	/**
	 * Update a Category
	 * 
	 * @param category
	 *            the Category to update
	 */
	public void updateCategory(Category category);
}
