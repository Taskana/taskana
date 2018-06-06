package pro.taskana;

/**
 * ObjectReferenceQuery for generating dynamic sql.
 */
public interface ObjectReferenceQuery extends BaseQuery<ObjectReference> {

    /**
     * Add your company to your query.
     *
     * @param companies
     *            as Strings
     * @return the query
     */
    ObjectReferenceQuery companyIn(String... companies);

    /**
     * Add your system to your query.
     *
     * @param systems
     *            as Strings
     * @return the query
     */
    ObjectReferenceQuery systemIn(String... systems);

    /**
     * Add your systemInstance to your query.
     *
     * @param systemInstances
     *            as Strings
     * @return the query
     */
    ObjectReferenceQuery systemInstanceIn(String... systemInstances);

    /**
     * Add your type to your query.
     *
     * @param types
     *            as Strings
     * @return the query
     */
    ObjectReferenceQuery typeIn(String... types);

    /**
     * Add your value to your query.
     *
     * @param values
     *            as Strings
     * @return the query
     */
    ObjectReferenceQuery valueIn(String... values);
}
