package org.taskana;

import java.util.List;

import org.taskana.model.Classification;

public interface ClassificationService {

	/**
	 * Get all available Classifications
	 * 
	 * @return The List of all Classifications
	 */
	public List<Classification> selectClassifications();

	/**
	 * Get all Classifications with given parent
	 * 
	 * @param parentId
	 *            the ID of the parent Classification
	 * @return
	 */
	public List<Classification> selectClassificationsByParentId(String parentId);

	/**
	 * Get a Classification for a given id
	 * 
	 * @param id
	 * @return the requested Classification
	 */
	public Classification selectClassificationById(String id);

	/**
	 * Insert a new Classification
	 * 
	 * @param classification
	 *            the classification to insert
	 */
	public void insertClassification(Classification classification);

	/**
	 * Update a Classification
	 * 
	 * @param classification
	 *            the Classification to update
	 */
	public void updateClassification(Classification classification);
}
