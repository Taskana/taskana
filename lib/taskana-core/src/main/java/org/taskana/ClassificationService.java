package org.taskana;

import java.util.List;

import org.taskana.model.Classification;

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
}
