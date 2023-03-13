package pro.taskana.common.internal.util;

import static java.util.function.Predicate.not;
import static pro.taskana.common.internal.util.CheckedFunction.wrap;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.json.JSONObject;
import pro.taskana.common.api.exceptions.SystemException;

public class ObjectAttributeChangeDetector {

  private ObjectAttributeChangeDetector() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Determines changes in fields between two objects.
   *
   * @param oldObject the old object for the comparison
   * @param newObject the new object for the comparison
   * @param <T> The generic type parameter
   * @return the details of all changed fields as JSON string
   * @throws SystemException when any parameter is null or the class of oldObject and newObject do
   *     not match
   */
  public static <T> String determineChangesInAttributes(T oldObject, T newObject) {
    if (oldObject == null || newObject == null) {
      throw new SystemException(
          "Null was provided as a parameter. Please provide two objects of the same type");
    }

    Class<?> objectClass = oldObject.getClass();
    if (List.class.isAssignableFrom(objectClass)) {
      return compareLists(oldObject, newObject);
    }

    // this has to be checked after we deal with List data types, because
    // we want to allow different implementations of the List interface to work as well.
    if (!oldObject.getClass().equals(newObject.getClass())) {
      throw new SystemException(
          String.format(
              "The classes differ between the oldObject(%s) and newObject(%s). "
                  + "In order to detect changes properly they should not differ.",
              oldObject.getClass().getName(), newObject.getClass().getName()));
    }

    List<JSONObject> changedAttributes =
        ReflectionUtil.retrieveAllFields(objectClass).stream()
            .peek(field -> field.setAccessible(true))
            .filter(not(field -> "customAttributes".equals(field.getName())))
            .map(wrap(field -> Triplet.of(field, field.get(oldObject), field.get(newObject))))
            .filter(not(t -> Objects.equals(t.getMiddle(), t.getRight())))
            .map(t -> generateChangedAttribute(t.getLeft(), t.getMiddle(), t.getRight()))
            .collect(Collectors.toList());

    JSONObject changes = new JSONObject();
    changes.put("changes", changedAttributes);
    return changes.toString();
  }

  private static JSONObject generateChangedAttribute(
      Field field, Object oldValue, Object newValue) {
    JSONObject changedAttribute = new JSONObject();
    changedAttribute.put("fieldName", field.getName());
    changedAttribute.put(
        "oldValue", Optional.ofNullable(oldValue).map(JSONObject::wrap).orElse(""));
    changedAttribute.put(
        "newValue", Optional.ofNullable(newValue).map(JSONObject::wrap).orElse(""));
    return changedAttribute;
  }

  private static <T> String compareLists(T oldObject, T newObject) {
    if (oldObject.equals(newObject)) {
      return "";
    }

    JSONObject changedAttribute = new JSONObject();
    changedAttribute.put("oldValue", oldObject);
    changedAttribute.put("newValue", newObject);

    JSONObject changes = new JSONObject();
    changes.put("changes", changedAttribute);

    return changes.toString();
  }
}
