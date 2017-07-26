package org.taskana.persistence;

import org.taskana.exceptions.NotAuthorizedException;
import org.taskana.model.Task;
import org.taskana.model.TaskState;

/**
 * TaskQuery for generating dynamic sql.
 */
public interface TaskQuery extends BaseQuery<Task> {

    /**
     * Add your tenant id to your query.
     * @param tenantId
     *            the tenant id as String
     * @return the query
     */
    TaskQuery tenantId(String tenantId);

    /**
     * Add your names to your query.
     * @param name
     *            the names as Strings
     * @return the query
     */
    TaskQuery name(String... name);

    /**
     * Add your description to your query. It will be compared in SQL with an LIKE.
     * If you use a wildcard like % tehn it will be transmitted to the database.
     * @param description
     *            your description
     * @return the query
     */
    TaskQuery descriptionLike(String description);

    /**
     * Add your priorities to your query.
     * @param priorities
     *            as a integer
     * @return the query
     */
    TaskQuery priority(int... priorities);

    /**
     * Add your state to your query.
     * @param states
     *            the states as {@link TaskState}
     * @return the query
     */
    TaskQuery state(TaskState... states);

    /**
     * Add your classification to your query. The classification query can be
     * obtained from the ClassificationService
     * @param classificationQuery
     *            the classification query
     * @return the query
     */
    TaskQuery classification(ClassificationQuery classificationQuery);

    /**
     * Add your workbasket id to the query.
     * @param workbasketIds
     *            the workbasket ids as String
     * @return the query
     * @throws NotAuthorizedException
     *             if the user have no rights
     */
    TaskQuery workbasketId(String... workbasketIds) throws NotAuthorizedException;

    /**
     * Add the owners to your query.
     * @param owners
     *            the owners as String
     * @return the query
     */
    TaskQuery owner(String... owners);

    /**
     * Add your objectReference to your query. The objectReference query can be
     * obtained from the TaskService
     * @param objectReferenceQuery
     *            the objectReference query
     * @return the query
     */
    TaskQuery objectReference(ObjectReferenceQuery objectReferenceQuery);

    /**
     * Add the isRead flag to the query.
     * @param isRead
     *            as Boolean. If null, it won't be integrated into the statement.
     *            You have to set false.
     * @return the query
     */
    TaskQuery read(Boolean isRead);

    /**
     * Add the isTransferred flag to the query.
     * @param isTransferred
     *            as Boolean. If null, it won't be integrated into the statement.
     *            You have to set false.
     * @return the query
     */
    TaskQuery transferred(Boolean isTransferred);

    /**
     * Filter the custom fields with this query. The scan will be run over all 10
     * fields.
     * @param customFields
     *            the value in the fields
     * @return the query
     */
    TaskQuery customFields(String... customFields);

    /**
     * This method provides a query builder for quering the database.
     * @return a {@link ObjectReferenceQuery}
     */
    ObjectReferenceQuery createObjectReferenceQuery();

}
