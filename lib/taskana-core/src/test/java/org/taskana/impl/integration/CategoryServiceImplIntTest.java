package org.taskana.impl.integration;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.taskana.CategoryService;
import org.taskana.TaskanaEngine;
import org.taskana.configuration.TaskanaEngineConfiguration;
import org.taskana.model.Category;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.LocalDate;

public class CategoryServiceImplIntTest {
	static int counter = 0;
	private CategoryService categoryService;

	@Before
	public void setup() throws FileNotFoundException, SQLException, LoginException {
		JdbcDataSource ds = new JdbcDataSource();
		ds.setURL("jdbc:h2:mem:test-db-category" + counter++);
		ds.setPassword("sa");
		ds.setUser("sa");
		TaskanaEngineConfiguration taskEngineConfiguration = new TaskanaEngineConfiguration(ds, false);

		TaskanaEngine te = taskEngineConfiguration.buildTaskanaEngine();
		categoryService = te.getCategoryService();
	}

	@Test
	public void testInsertCategory() {
		Category category = new Category();
		category.setId("0");
		categoryService.insertCategory(category);

		Assert.assertNotNull(categoryService.selectCategoryById(category.getId()));
	}

	@Test
	public void testFindAllCategories() {
		Category category0 = new Category();
		category0.setId("0");
		category0.setParentCategoryId("");
		categoryService.insertCategory(category0);
		Category category1 = new Category();
		category1.setId("1");
		category1.setParentCategoryId("");
		categoryService.insertCategory(category1);

		Assert.assertEquals(2, categoryService.selectCategories().size());
	}

	@Test
	public void testFindByParentCategory() {
		Category category0 = new Category();
		category0.setId("0");
		category0.setParentCategoryId("0");
		categoryService.insertCategory(category0);
		Category category1 = new Category();
		category1.setId("1");
		category1.setParentCategoryId("0");
		categoryService.insertCategory(category1);

		Assert.assertEquals(2, categoryService.selectCategoriesByParentId("0").size());
	}

	@Test
	public void testModifiedCategory() {
		Category category = new Category();
		categoryService.insertCategory(category);
		category.setDescription("TEST EVERYTHING");
		categoryService.updateCategory(category);

		Assert.assertEquals(category.getModified().toString(), LocalDate.now().toString());
	}
}
