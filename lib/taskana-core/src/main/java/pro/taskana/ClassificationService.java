package pro.taskana;

import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.model.Classification;

import java.util.List;

/**
 * This class manages the classifications.
 */
public interface ClassificationService {

    /**
     * Get all available Classifications as a tree.
     * @return The List of all Classifications
     */
    List<Classification> getClassificationTree() throws NotAuthorizedException;

    /**
     * Get all Classifications with the given id.
     * Returns also older and domain-specific versions of the classification.
     *
     * @param id
     * @return List with all versions of the Classification
     */
    List<Classification> getAllClassificationsWithId(String id, String domain);

    /**
     * Get the Classification for id and domain.
     * @param id
     * @param domain
     * @return If exist: domain-specific classification, else default classification
     */
    Classification getClassification(String id, String domain) throws ClassificationNotFoundException;

    /**
     * Insert a new Classification.
     * @param classification
     *            the classification to insert
     */
    void addClassification(Classification classification);

    /**
     * Update a Classification.
     * @param classification
     *            the Classification to update
     */
    void updateClassification(Classification classification);

    /**
     * This method provides a query builder for quering the database.
     * @return a {@link ClassificationQuery}
     */
    ClassificationQuery createClassificationQuery();
}
