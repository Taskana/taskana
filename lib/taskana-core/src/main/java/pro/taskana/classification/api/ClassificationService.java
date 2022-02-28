package pro.taskana.classification.api;

import pro.taskana.classification.api.exceptions.ClassificationAlreadyExistException;
import pro.taskana.classification.api.exceptions.ClassificationInUseException;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.exceptions.MalformedServiceLevelException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;

/** This class manages {@linkplain Classification Classifications}. */
public interface ClassificationService {

  /**
   * Gets the {@linkplain Classification} identified by the provided {@linkplain
   * Classification#getKey() key} and {@linkplain Classification#getDomain() domain}. If there's no
   * {@linkplain Classification} in the given {@linkplain Classification#getDomain() domain},
   * returns the {@linkplain Classification} from the master domain.
   *
   * @param key the {@linkplain Classification#getKey() key} of the searched-for {@linkplain
   *     Classification}
   * @param domain the {@linkplain Classification#getDomain() domain} of the searched-for
   *     {@linkplain Classification}
   * @return if exist: domain-specific {@linkplain Classification}, else master {@linkplain
   *     Classification}
   * @throws ClassificationNotFoundException if no {@linkplain Classification} is found that matches
   *     the {@linkplain Classification#getKey() key} either in {@linkplain
   *     Classification#getDomain() domain} or in the master {@linkplain Classification#getDomain()
   *     domain}
   */
  Classification getClassification(String key, String domain)
      throws ClassificationNotFoundException;

  /**
   * Gets the {@linkplain Classification} identified by the provided {@linkplain
   * Classification#getId() id}.
   *
   * @param id the {@linkplain Classification#getId() id} of the searched-for {@linkplain
   *     Classification}
   * @return the {@linkplain Classification} identified by {@linkplain Classification#getId() id}
   * @throws ClassificationNotFoundException if no {@linkplain Classification} is found that matches
   *     the {@linkplain Classification#getId() id}
   */
  Classification getClassification(String id) throws ClassificationNotFoundException;

  /**
   * Deletes a {@linkplain Classification} with all child {@linkplain Classification
   * Classifications}.
   *
   * @param id the {@linkplain Classification#getId() id} of the searched-for {@linkplain
   *     Classification}
   * @throws ClassificationInUseException if there are {@linkplain
   *     pro.taskana.task.api.models.Task}s which refer to this {@linkplain Classification}
   * @throws ClassificationNotFoundException if no {@linkplain Classification} with the specified
   *     {@linkplain Classification#getId() id} is found
   * @throws NotAuthorizedException if the current user is not member of role {@linkplain
   *     pro.taskana.common.api.TaskanaRole#BUSINESS_ADMIN} or {@linkplain
   *     pro.taskana.common.api.TaskanaRole#ADMIN}
   */
  void deleteClassification(String id)
      throws ClassificationInUseException, ClassificationNotFoundException, NotAuthorizedException;

  /**
   * Deletes the {@linkplain Classification} identified by the provided {@linkplain
   * Classification#getKey() key} and {@linkplain Classification#getDomain() domain} with all its
   * child {@linkplain Classification Classifications}.
   *
   * @param classificationKey the {@linkplain Classification#getKey() key} of the {@linkplain
   *     Classification} you want to delete.
   * @param domain the {@linkplain Classification#getDomain() domain} of the the {@linkplain
   *     Classification} you want to delete. if "", the function tries to delete the "master domain"
   *     {@linkplain Classification} and any other {@linkplain Classification} with this {@linkplain
   *     Classification#getKey() key}.
   * @throws ClassificationInUseException if there are {@linkplain
   *     pro.taskana.task.api.models.Task}s which refer to this {@linkplain Classification}.
   * @throws ClassificationNotFoundException if no {@linkplain Classification} with the specified
   *     {@linkplain Classification#getKey() key} and {@linkplain Classification#getDomain() domain}
   *     is found.
   * @throws NotAuthorizedException if the current user is not member of role {@linkplain
   *     pro.taskana.common.api.TaskanaRole#BUSINESS_ADMIN} or {@linkplain
   *     pro.taskana.common.api.TaskanaRole#ADMIN}
   */
  void deleteClassification(String classificationKey, String domain)
      throws ClassificationInUseException, ClassificationNotFoundException, NotAuthorizedException;

  /**
   * Inserts a new {@linkplain Classification} after applying default values. <br>
   * The {@linkplain Classification} will be added to master-domain, too - if not already existing.
   * <br>
   * The default values are:
   *
   * <ul>
   *   <li><b>{@linkplain Classification#getId() id}</b> - generated by {@linkplain
   *       pro.taskana.common.internal.util.IdGenerator IdGenerator}
   *   <li><b>{@linkplain Classification#getParentId() parentId}</b> - ""
   *   <li><b>{@linkplain Classification#getParentKey() parentKey}</b> - ""
   *   <li><b>{@linkplain Classification#getServiceLevel() serviceLevel}</b> - "P0D"
   *   <li><b>{@linkplain Classification#getIsValidInDomain() isValidInDomain}</b> - {@code true}
   *       <br>
   *       if {@linkplain Classification#getDomain() domain} an empty string than {@code false}
   * </ul>
   *
   * @param classification the {@linkplain Classification} to insert
   * @return the inserted {@linkplain Classification} with unique {@linkplain Classification#getId()
   *     id}.
   * @throws ClassificationAlreadyExistException if the {@linkplain Classification} already exists
   *     in the given {@linkplain Classification#getDomain() domain}.
   * @throws NotAuthorizedException if the current user is not member of role {@linkplain
   *     pro.taskana.common.api.TaskanaRole#BUSINESS_ADMIN} or {@linkplain
   *     pro.taskana.common.api.TaskanaRole#ADMIN}
   * @throws DomainNotFoundException if the {@linkplain Classification#getDomain() domain} does not
   *     exist in the configuration
   * @throws MalformedServiceLevelException if the {@linkplain Classification#getServiceLevel()
   *     serviceLevel} does not comply with the ISO 8601 specification
   * @throws InvalidArgumentException if the {@linkplain Classification} contains invalid properties
   */
  Classification createClassification(Classification classification)
      throws ClassificationAlreadyExistException, NotAuthorizedException, DomainNotFoundException,
          InvalidArgumentException, MalformedServiceLevelException;

  /**
   * Updates the specified {@linkplain Classification}.
   *
   * @param classification the {@linkplain Classification} to update
   * @return the updated {@linkplain Classification}.
   * @throws ClassificationNotFoundException if the {@linkplain Classification} or its parent does
   *     not exist.
   * @throws NotAuthorizedException if the caller got no {@linkplain
   *     pro.taskana.common.api.TaskanaRole#BUSINESS_ADMIN} or {@linkplain
   *     pro.taskana.common.api.TaskanaRole#ADMIN} permissions.
   * @throws ConcurrencyException if the {@linkplain Classification} was modified in the meantime
   *     and is not the most up to date anymore; that's the case if the given modified timestamp
   *     differs from the one in the database
   * @throws MalformedServiceLevelException if the {@linkplain Classification#getServiceLevel()
   *     serviceLevel} does not comply with the ISO 8601 specification
   * @throws InvalidArgumentException if the {@linkplain Classification} contains invalid properties
   */
  Classification updateClassification(Classification classification)
      throws ClassificationNotFoundException, NotAuthorizedException, ConcurrencyException,
          InvalidArgumentException, MalformedServiceLevelException;

  /**
   * This method provides a query builder for querying the database.
   *
   * @return a {@linkplain ClassificationQuery}
   */
  ClassificationQuery createClassificationQuery();

  /**
   * Creating a new {@link Classification} initialized with unchangeable values for {@linkplain
   * Classification#getKey() key}, {@linkplain Classification#getDomain() domain} and {@linkplain
   * Classification#getType() type}. The classification will not be inserted into the database until
   * {@linkplain ClassificationService#createClassification(Classification)} call.
   *
   * @param key the {@linkplain Classification#getKey() key} of the {@linkplain Classification}
   * @param domain the {@linkplain Classification#getDomain() domain} of the new {@linkplain
   *     Classification}
   * @param type the {@linkplain Classification#getType() type} of the new {@linkplain
   *     Classification}
   * @return the initialized {@linkplain Classification}
   */
  Classification newClassification(String key, String domain, String type);
}
