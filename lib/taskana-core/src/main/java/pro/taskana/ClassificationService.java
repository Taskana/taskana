package pro.taskana;

import java.util.List;

import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;

/**
 * This class manages the classifications.
 */
public interface ClassificationService {

    /**
     * Get all available Classifications as a tree.
     *
     * @return The List of all Classifications
     * @throws NotAuthorizedException
     *             if the permissions are not granted for this specific interaction.
     * @throws InvalidArgumentException
     *             if the given permissions/UserContext or Base-Object is NULL.
     */
    List<Classification> getClassificationTree() throws NotAuthorizedException, InvalidArgumentException;

    /**
     * Get all Classifications with the given key. Returns also older and domain-specific versions of the
     * classification.
     *
     * @param key
     *            TODO
     * @param domain
     *            TODO
     * @return List with all versions of the Classification
     */
    List<Classification> getAllClassificationsWithKey(String key, String domain);

    /**
     * Get the Classification for key and domain. If there's no specification for the given domain, it returns the root
     * domain.
     *
     * @param key
     *            TODO
     * @param domain
     *            TODO
     * @return If exist: domain-specific classification, else root classification
     * @throws ClassificationNotFoundException
     *             TODO
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
     * @throws ClassificationNotFoundException
     *             when the classification does not exist already.
     */
    void updateClassification(Classification classification) throws ClassificationNotFoundException;

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
     * @return classification to specify
     */
    Classification newClassification();
}
