package pro.taskana.common.internal.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.exceptions.SystemException;

public class ObjectAttributeChangeDetector {

  private static final Logger LOGGER = LoggerFactory.getLogger(ObjectAttributeChangeDetector.class);

  private ObjectAttributeChangeDetector() {}

  /**
   * Determines changes in fields between two objects.
   *
   * @param oldObject the old object for the comparison
   * @param newObject the new object for the comparison
   * @param <T> The generic type parameter
   * @return the details of all changed fields as JSON string
   */
  public static <T> String determineChangesInAttributes(T oldObject, T newObject) {
    LOGGER.debug(
        "Entry to determineChangesInAttributes (oldObject = {}, newObject = {}",
        oldObject,
        newObject);

    List<Field> fields = new ArrayList<>();

    if (Objects.isNull(oldObject) || Objects.isNull(newObject)) {

      throw new SystemException(
          "Null was provided as a parameter. Please provide two objects of the same type");
    }

    Class<?> currentClass = oldObject.getClass();

    if (List.class.isAssignableFrom(currentClass)) {

      return compareLists(oldObject, newObject);

    } else {

      retrieveFields(fields, currentClass);
    }

    Predicate<Triplet<Field, Object, Object>> areFieldsNotEqual =
        fieldAndValuePairTriplet ->
            !Objects.equals(
                fieldAndValuePairTriplet.getMiddle(), fieldAndValuePairTriplet.getRight());
    Predicate<Triplet<Field, Object, Object>> isFieldNotCustomAttributes =
        fieldAndValuePairTriplet ->
            !fieldAndValuePairTriplet.getLeft().getName().equals("customAttributes");

    List<JSONObject> changedAttributes =
        fields.stream()
            .peek(field -> field.setAccessible(true))
            .map(
                CheckedFunction.wrap(
                    field -> new Triplet<>(field, field.get(oldObject), field.get(newObject))))
            .filter(areFieldsNotEqual.and(isFieldNotCustomAttributes))
            .map(
                fieldAndValuePairTriplet -> {
                  JSONObject changedAttribute = new JSONObject();
                  changedAttribute.put("fieldName", fieldAndValuePairTriplet.getLeft().getName());
                  changedAttribute.put(
                      "oldValue",
                      Optional.ofNullable(fieldAndValuePairTriplet.getMiddle()).orElse(""));
                  changedAttribute.put(
                      "newValue",
                      Optional.ofNullable(fieldAndValuePairTriplet.getRight()).orElse(""));
                  return changedAttribute;
                })
            .collect(Collectors.toList());

    JSONObject changes = new JSONObject();
    changes.put("changes", changedAttributes);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from determineChangesInAttributes(), returning {}", changes);
    }

    return changes.toString();
  }

  private static void retrieveFields(List<Field> fields, Class<?> currentClass) {
    while (currentClass.getSuperclass() != null) {
      fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
      currentClass = currentClass.getSuperclass();
    }
  }

  private static <T> String compareLists(T oldObject, T newObject) {

    LOGGER.debug(
        "Entry to determineChangesInAttributes (oldObject = {}, newObject = {}",
        oldObject,
        newObject);

    if (!oldObject.equals(newObject)) {
      JSONObject changedAttribute = new JSONObject();

      changedAttribute.put("oldValue", oldObject);
      changedAttribute.put("newValue", newObject);

      JSONObject changes = new JSONObject();

      changes.put("changes", changedAttribute);

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Exit from determineChangesInAttributes(), returning {}", changes);
      }
      return changes.toString();
    }

    LOGGER.debug(
        "Exit from determineChangesInAttributes(), "
            + "returning empty String because there are no changed attributes");

    return "";
  }
}
