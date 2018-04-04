package pro.taskana;

import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationInUseException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;

/**
 * This class manages the classifications.
 */
public interface ClassificationService {

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
     * Get the Classification by id.
     *
     * @param id
     *            the id of the searched-for classifications
     * @return the classification identified by id
     * @throws ClassificationNotFoundException
     *             if no classification is found that matches the id.
     */
    Classification getClassification(String id) throws ClassificationNotFoundException;

    /**
     * Delete a classification with all child classifications.
     *
     * @param classificationKey
     *            the key of the classification you want to delete.
     * @param domain
     *            the domains for which you want to delete the classification. if "", the function tries to delete the
     *            "master domain" classification and any other classification with this key.
     * @throws ClassificationInUseException
     *             if there are Task existing, which refer to this classification.
     * @throws ClassificationNotFoundException
     *             if for an domain no classification specification is found.
     * @throws NotAuthorizedException
     *             if the current user is not member of role BUSINESS_ADMIN or ADMIN
     */
    void deleteClassification(String classificationKey, String domain)
        throws ClassificationInUseException, ClassificationNotFoundException, NotAuthorizedException;

    /**
     * Persists a new classification after adding default values. <br >
     * The classification will be added to root-domain, too - if not already existing.
     *
     * @param classification
     *            the classification to insert
     * @return classification which is persisted with unique ID.
     * @throws ClassificationAlreadyExistException
     *             when the classification does already exists at the given domain.
     * @throws NotAuthorizedException
     *             if the current user is not member of role BUSINESS_ADMIN or ADMIN
     * @throws ClassificationNotFoundException
     *             if the current parentId is not NULL/EMPTY and can not be found.
     * @throws DomainNotFoundException
     *             if the domain does not exist in the configuration
     * @throws InvalidArgumentException
     *             if the ServiceLevel property does not comply with the ISO 8601 specification
     */
    Classification createClassification(Classification classification)
        throws ClassificationAlreadyExistException, NotAuthorizedException, ClassificationNotFoundException,
        DomainNotFoundException, InvalidArgumentException;

    /**
     * Updates a Classification.
     *
     * @param classification
     *            the Classification to update
     * @return the updated Classification.
     * @throws ClassificationNotFoundException
     *             when the classification OR it´s parent does not exist.
     * @throws NotAuthorizedException
     *             when the caller got no ADMIN or BUSINESS_ADMIN permissions.
     * @throws ConcurrencyException
     *             when the Classification was modified meanwhile and is not latest anymore.
     * @throws InvalidArgumentException
     *             if the ServiceLevel property does not comply with the ISO 8601 specification
     */
    Classification updateClassification(Classification classification)
        throws ClassificationNotFoundException, NotAuthorizedException, ConcurrencyException, InvalidArgumentException;

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
     * @param key
     *            the key of the classification
     * @param domain
     *            the domain of the new classification
     * @param type
     *            the type of the new classification
     * @return classification to specify
     */
    Classification newClassification(String key, String domain, String type);
}
