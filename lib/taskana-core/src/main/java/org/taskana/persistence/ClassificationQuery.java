package org.taskana.persistence;

import org.taskana.model.Classification;

/**
 * ClassificationQuery for generating dynamic sql.
 */
public interface ClassificationQuery extends BaseQuery<Classification> {
    /**
     * Add your tenant id to your query.
     * @param tenantId
     *            the tenant id as String
     * @return the query
     */
    ClassificationQuery tenantId(String tenantId);

    /**
     * Add your parentClassification to your query.
     * @param parentClassificationId
     *            as String
     * @return the query
     */
    ClassificationQuery parentClassification(String... parentClassificationId);

    /**
     * Add your category to your query.
     * @param category
     *            as String
     * @return the query
     */
    ClassificationQuery category(String... category);

    /**
     * Add your type to your query.
     * @param type
     *            as String
     * @return the query
     */
    ClassificationQuery type(String... type);

    /**
     * Add your name to your query.
     * @param name
     *            as String
     * @return the query
     */
    ClassificationQuery name(String... name);

    /**
     * Add your description to your query. It will be compared in SQL with an LIKE.
     * If you use a wildcard like % tehn it will be transmitted to the database.
     * @param description
     *            your description
     * @return the query
     */
    ClassificationQuery descriptionLike(String description);

    /**
     * Add your priority to your query.
     * @param priorities
     *            as integers
     * @return the query
     */
    ClassificationQuery priority(int... priorities);

    /**
     * Add your serviceLevel to your query.
     * @param serviceLevel
     *            as String
     * @return the query
     */
    ClassificationQuery serviceLevel(String... serviceLevel);

}
