package pro.taskana;

import java.util.List;

import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.NotAuthorizedException;

/**
 * This class manages the classifications.
 */
public interface ClassificationService {

    /**
     * Get all available Classifications as a tree.
     * @return The List of all Classifications
     * @throws NotAuthorizedException TODO
     */
    List<Classification> getClassificationTree() throws NotAuthorizedException;

    /**
     * Get all Classifications with the given key.
     * Returns also older and domain-specific versions of the classification.
     *
     * @param key TODO
     * @param domain TODO
     * @return List with all versions of the Classification
     */
    List<Classification> getAllClassificationsWithKey(String key, String domain);

    /**
     * Get the Classification for key and domain. If there's no specification for the given domain, it returns the root domain.
     * @param key TODO
     * @param domain TODO
     * @return If exist: domain-specific classification, else root classification
     * @throws ClassificationNotFoundException TODO
     */
    Classification getClassification(String key, String domain) throws ClassificationNotFoundException;

    /**
     * Persist a new classification. If the classification does
     * already exist in a domain, it will just be updated.
     * @param classification
     *            the classification to insert
     * @throws ClassificationAlreadyExistException
     *            when the classification does already exists with same ID+domain.
     */
    void createClassification(Classification classification) throws ClassificationAlreadyExistException;

    /**
     * Update a Classification.
     * @param classification
     *            the Classification to update
     * @throws ClassificationNotFoundException when the classification does not exist already.
     */
    void updateClassification(Classification classification) throws ClassificationNotFoundException;

    /**
     * This method provides a query builder for quering the database.
     * @return a {@link ClassificationQuery}
     */
    ClassificationQuery createClassificationQuery();

    /**
     * Creating a new {@link Classification} with unchangeable default values.
     * It will be only generated and is not persisted until CREATE-call.
     * @return classification to specify
     */
    Classification newClassification();
}
