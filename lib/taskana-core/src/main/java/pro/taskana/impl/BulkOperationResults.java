package pro.taskana.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Returning type for a bulk db interaction with errors. This wrapper is storing them with a matching object ID.
 *
 * @param <K>
 *            unique keys for the logs.
 * @param <V>
 *            type of the stored informations
 */
public class BulkOperationResults<K, V> {

    private Optional<Map<K, V>> errorMap = Optional.of(new HashMap<K, V>());
    private static final Logger LOGGER = LoggerFactory.getLogger(BulkOperationResults.class);

    /**
     * Returning a list of current errors as map. If there are no errors the result will be empty.
     *
     * @return map of errors which can´t be null.
     */
    public Map<K, V> getErrorMap() {
        return this.errorMap.get();
    }

    /**
     * Adding an appearing error to the map and list them by a unique ID as key. NULL keys will be ignored.
     *
     * @param objectId
     *            unique key of a entity.
     * @param error
     *            occurred error of a interaction with the entity
     * @return status of adding the values.
     */
    public boolean addError(K objectId, V error) {
        boolean status = false;
        try {
            if (objectId != null) {
                this.errorMap.get().put(objectId, error);
                status = true;
            }
        } catch (Exception e) {
            LOGGER.warn(
                "Can´t add bulkoperation-error, because of a map failure. ID={}, error={} and current failure={}",
                objectId, error, e);
        }
        return status;
    }

    /**
     * Returning the status of a bulk-error-log.
     *
     * @return true if there are logged errors.
     */
    public boolean containErrors() {
        boolean isContainingErrors = false;
        if (!this.errorMap.get().isEmpty()) {
            isContainingErrors = true;
        }
        return isContainingErrors;
    }

    /**
     * Returns the stored error for a unique ID or NULL if there is no error stored or ID invalid.
     *
     * @param idKey
     *            which is mapped with an error
     * @return stored error for ID
     */
    public V getErrorForId(K idKey) {
        V result = null;
        if (idKey != null) {
            result = this.errorMap.get().get(idKey);
        }
        return result;
    }

    /**
     * Clearing the map - all entries will be removed.
     */
    public void clearErrors() {
        this.errorMap.get().clear();
    }
}
