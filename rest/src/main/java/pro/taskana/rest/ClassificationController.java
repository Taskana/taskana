/**
 * 
 */
package pro.taskana.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.Classification;
import pro.taskana.ClassificationService;

@RestController
@RequestMapping(path = "/v1/classifications", produces = { MediaType.APPLICATION_JSON_VALUE })
public class ClassificationController {

	@Autowired
	private ClassificationService classificationService;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Classification>> getClassifications() {
		try {
			List<Classification> classificationTree = classificationService.getClassificationTree();
			return ResponseEntity.status(HttpStatus.OK).body(classificationTree);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}
	
	@RequestMapping(value = "/{classificationId}", method = RequestMethod.GET)
	public ResponseEntity<Classification> getClassification(@PathVariable String classificationId) {
	    try {
	        Classification classification = classificationService.getClassification(classificationId, "");
	        return ResponseEntity.status(HttpStatus.OK).body(classification);
	    } catch(Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}

	@RequestMapping(value = "/{classificationId}/{domain}", method = RequestMethod.GET)
	public ResponseEntity<Classification> getClassification(@PathVariable String classificationId, @PathVariable String domain) {
	    try {
            Classification classification = classificationService.getClassification(classificationId, domain);
            return ResponseEntity.status(HttpStatus.OK).body(classification);
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Classification> createClassification(@RequestBody Classification classification) {
		try {
			classificationService.createClassification(classification);
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
