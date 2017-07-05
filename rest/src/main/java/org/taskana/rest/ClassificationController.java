/**
 * 
 */
package org.taskana.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.taskana.ClassificationService;
import org.taskana.model.Classification;

import java.util.List;

@RestController
@RequestMapping(path = "/v1/classifications", produces = { MediaType.APPLICATION_JSON_VALUE })
public class ClassificationController {

	@Autowired
	private ClassificationService classificationService;

	@RequestMapping
	public List<Classification> getClassifications() {
		return classificationService.selectClassifications();
	}

	@RequestMapping(value = "/{classificationId}")
	public Classification getClassification(@PathVariable String classificationId) {
		return classificationService.selectClassificationById(classificationId);
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Classification> createClassification(@RequestBody Classification classification) {
		try {
			classificationService.insertClassification(classification);
			return ResponseEntity.status(HttpStatus.CREATED).body(classification);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<Classification> updateClassification(@RequestBody Classification classification) {
		try {
			classificationService.updateClassification(classification);
			return ResponseEntity.status(HttpStatus.CREATED).body(classification);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

}
