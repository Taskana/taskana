package pro.taskana;

import java.util.List;

import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;

/**
 * This class manages the classifications.
 */
public interface ClassificationService {

    /**
     * Get all available Classification summaries as a tree.
     *

     * @return The List of all Classification summaries
     */
    List<ClassificationSummary> getClassificationTree();

    /**
     * Get all ClassificationSummaries with the given key. Returns also older and domain-specific versions of the
     * classification.
     *
     * @param key
     *            the key of the searched-for classifications
     * @param domain
     *            the domain of the searched-for classifications
     * @return List with all versions of the Classification
     */
    List<ClassificationSummary> getAllClassifications(String key, String domain);

    /**
     * Get the Classification for key and domain. If there's no Classification in the given domain, return the
     * Classification from the root domain.
     *
     * @param key
     *            the key of the searched-for classifications
     * @param domain
     *            the domain of the searched-for classifications
     * @return If exist: domain-specific classification, else root classification
     * @throws ClassificationNotFoundException
     *             if no classification is found that matches the key either in domain or in the root domain.
     */
    Classification getClassification(String key, String domain) throws ClassificationNotFoundException;

    /**
     * Persists a new classification after adding default values. <br >
     * The classification will be added to root-domain, too - if not already existing.
     *
     * @param classification
     *            the classification to insert
     * @return classification which is persisted with unique ID.
     * @throws ClassificationAlreadyExistException
     *             when the classification does already exists at the given domain.
     */
    Classification createClassification(Classification classification)
        throws ClassificationAlreadyExistException;

    /**
     * Update a Classification.
     *
     * @param classification
     *            the Classification to update
     * @return the updated Classification.
     * @throws ClassificationNotFoundException
     *             when the classification does not exist already.
     */
    Classification updateClassification(Classification classification) throws ClassificationNotFoundException;

    /**
     * This method provides a query builder for quering the database.
     *
     * @return a {@link ClassificationQuery}
     */
    ClassificationQuery createClassificationQuery();

    /**
     * Creating a new {@link Classification} with unchangeable default values. It will be only generated and is not
     * persisted until CREATE-call.
     *
     * @param domain
     *            the domain of the new classification
     * @param key
     *            the key of the classification
     * @param type
     *            the type of the new classification
     * @return classification to specify
     */
    Classification newClassification(String domain, String key, String type);
}
