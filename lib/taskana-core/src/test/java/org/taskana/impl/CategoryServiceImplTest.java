package org.taskana.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.taskana.model.Category;
import org.taskana.model.mappings.CategoryMapper;

@RunWith(MockitoJUnitRunner.class)
public class CategoryServiceImplTest {

	@InjectMocks
	CategoryServiceImpl categoryService;

	@Mock
	CategoryMapper categoryMapper;

	@Test
	public void testInsertCategory() {
		doNothing().when(categoryMapper).insert(any());

		Category category = new Category();
		category.setId("0");
		categoryService.insertCategory(category);
		
		when(categoryMapper.findById(any())).thenReturn(category);

		Assert.assertNotNull(categoryService.selectCategoryById(category.getId()));
	}

	@Test
	public void testFindAllCategories() {
		doNothing().when(categoryMapper).insert(any());

		Category category0 = new Category();
		category0.setId("0");
		category0.setParentCategoryId("");
		categoryService.insertCategory(category0);
		Category category1 = new Category();
		category1.setId("1");
		category1.setParentCategoryId("");
		categoryService.insertCategory(category1);
		
		List<Category> categories = new ArrayList<>();
		categories.add(category0);
		when(categoryMapper.findByParentId("")).thenReturn(categories);

		verify(categoryMapper, atLeast(2)).insert(any());
		Assert.assertEquals(1, categoryService.selectCategories().size());
	}

	@Test
	public void testFindByParentCategory() {
		doNothing().when(categoryMapper).insert(any());

		Category category0 = new Category();
		category0.setId("0");
		category0.setParentCategoryId("0");
		categoryService.insertCategory(category0);
		Category category1 = new Category();
		category1.setId("1");
		category1.setParentCategoryId("0");
		categoryService.insertCategory(category1);

		List<Category> categories = new ArrayList<>();
		categories.add(category0);
		categories.add(category1);
		when(categoryMapper.findByParentId(any())).thenReturn(categories);

		verify(categoryMapper, atLeast(2)).insert(any());

		Assert.assertEquals(2, categoryService.selectCategoriesByParentId("0").size());
	}

	@Test
	public void testModifiedCategory() {
		doNothing().when(categoryMapper).insert(any());
		doNothing().when(categoryMapper).update(any());

		Category category = new Category();
		categoryService.insertCategory(category);
		category.setDescription("TEST EVERYTHING");
		categoryService.updateCategory(category);

		Assert.assertEquals(category.getModified().toString(), LocalDate.now().toString());
	}
}
