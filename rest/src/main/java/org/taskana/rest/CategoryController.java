/**
 * 
 */
package org.taskana.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.taskana.CategoryService;
import org.taskana.model.Category;

@RestController
@RequestMapping(path = "/v1/categories", produces = { MediaType.APPLICATION_JSON_VALUE })
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@RequestMapping
	public List<Category> getCategories() {
		return categoryService.selectCategories();
	}

	@RequestMapping(value = "/{categoryId}")
	public Category getCategory(@PathVariable String categoryId) {
		return categoryService.selectCategoryById(categoryId);
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Category> createCategory(@RequestBody Category category) {
		try {
			categoryService.insertCategory(category);
			return ResponseEntity.status(HttpStatus.CREATED).body(category);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<Category> updateCategory(@RequestBody Category category) {
		try {
			categoryService.updateCategory(category);
			return ResponseEntity.status(HttpStatus.CREATED).body(category);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

}
