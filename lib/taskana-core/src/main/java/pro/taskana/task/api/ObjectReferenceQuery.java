package pro.taskana.task.api;

import pro.taskana.common.api.BaseQuery;
import pro.taskana.task.api.models.ObjectReference;

/**
 * The ObjectReferenceQuery allows for a custom search across all {@linkplain
 * pro.taskana.task.api.models.ObjectReference ObjectReferences}.
 */
public interface ObjectReferenceQuery
    extends BaseQuery<ObjectReference, ObjectReferenceQueryColumnName> {

  /**
   * Selects only {@linkplain ObjectReference ObjectReferences} which have a {@linkplain
   * ObjectReference#getCompany()} value that is equal to any of the passed values.
   *
   * @param companies the values of interest
   * @return the query
   */
  ObjectReferenceQuery companyIn(String... companies);

  /**
   * Selects only {@linkplain ObjectReference ObjectReferences} which have a {@linkplain
   * ObjectReference#getSystem()} value that is equal to any of the passed values.
   *
   * @param systems the values of interest
   * @return the query
   */
  ObjectReferenceQuery systemIn(String... systems);

  /**
   * Selects only {@linkplain ObjectReference ObjectReferences} which have a {@linkplain
   * ObjectReference#getSystemInstance()} value that is equal to any of the passed values.
   *
   * @param systemInstances the values of interest
   * @return the query
   */
  ObjectReferenceQuery systemInstanceIn(String... systemInstances);

  /**
   * Selects only {@linkplain ObjectReference ObjectReferences} which have a {@linkplain
   * ObjectReference#getType()} value that is equal to any of the passed values.
   *
   * @param types the values of interest
   * @return the query
   */
  ObjectReferenceQuery typeIn(String... types);

  /**
   * Selects only {@linkplain ObjectReference ObjectReferences} which have a {@linkplain
   * ObjectReference#getValue()} that is equal to any of the passed values.
   *
   * @param values the values of interest
   * @return the query
   */
  ObjectReferenceQuery valueIn(String... values);
}
