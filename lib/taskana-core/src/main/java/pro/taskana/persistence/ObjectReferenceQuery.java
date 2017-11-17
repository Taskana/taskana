package pro.taskana.persistence;

import pro.taskana.model.ObjectReference;

/**
 * ObjectReferenceQuery for generating dynamic sql.
 */
public interface ObjectReferenceQuery extends BaseQuery<ObjectReference> {
    /**
     * Add your tenant id to your query.
     * @param tenantId
     *            the tenant id as String
     * @return the query
     */
    ObjectReferenceQuery tenantId(String tenantId);

    /**
     * Add your company to your query.
     * @param companies
     *            as Strings
     * @return the query
     */
    ObjectReferenceQuery company(String... companies);

    /**
     * Add your system to your query.
     * @param systems
     *            as Strings
     * @return the query
     */
    ObjectReferenceQuery system(String... systems);

    /**
     * Add your systemInstance to your query.
     * @param systemInstances
     *            as Strings
     * @return the query
     */
    ObjectReferenceQuery systemInstance(String... systemInstances);

    /**
     * Add your type to your query.
     * @param types
     *            as Strings
     * @return the query
     */
    ObjectReferenceQuery type(String... types);

    /**
     * Add your value to your query.
     * @param values
     *            as Strings
     * @return the query
     */
    ObjectReferenceQuery value(String... values);

}
