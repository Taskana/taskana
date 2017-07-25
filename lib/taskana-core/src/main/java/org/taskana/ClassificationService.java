package org.taskana;

import org.taskana.model.Classification;
import org.taskana.persistence.ClassificationQuery;

import java.util.List;

/**
 * This class manages the classifications.
 */
public interface ClassificationService {

    /**
     * Get all available Classifications.
     * @return The List of all Classifications
     */
    List<Classification> selectClassifications();

    /**
     * Get all Classifications with given parent.
     * @param parentId
     *            the ID of the parent Classification
     * @return
     */
    List<Classification> selectClassificationsByParentId(String parentId);

    /**
     * Get a Classification for a given id.
     * @param id
     * @return the requested Classification
     */
    Classification selectClassificationById(String id);

    /**
     * Get all Classifications from a domain.
     * @param domain
     * @return
     */
    List<Classification> selectClassificationByDomain(String domain);

    /**
     * Get all Classifications from a domain with a specific type.
     * @param domain
     * @param type
     * @return
     */
    List<Classification> selectClassificationByDomainAndType(String domain, String type);

    /**
     * Get all Classifications from a category for a domain.
     * @param domain
     * @param category
     * @return
     */
    List<Classification> selectClassificationByDomainAndCategory(String domain, String category);

    /**
     * Get all Classifications from a category and a type.
     * @param category
     * @param type
     * @return
     */
    List<Classification> selectClassificationByCategoryAndType(String category, String type);

    /**
     * Insert a new Classification.
     * @param classification
     *            the classification to insert
     */
    void insertClassification(Classification classification);

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
