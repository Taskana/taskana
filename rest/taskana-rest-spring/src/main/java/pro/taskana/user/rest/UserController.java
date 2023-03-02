package pro.taskana.user.rest;

import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.user.api.UserService;
import pro.taskana.user.api.exceptions.UserAlreadyExistException;
import pro.taskana.user.api.exceptions.UserNotFoundException;
import pro.taskana.user.api.models.User;
import pro.taskana.user.rest.assembler.UserRepresentationModelAssembler;
import pro.taskana.user.rest.models.UserCollectionRepresentationModel;
import pro.taskana.user.rest.models.UserRepresentationModel;

/** Controller for all {@linkplain User} related endpoints. */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class UserController {
  private final UserService userService;
  private final UserRepresentationModelAssembler userAssembler;

  /**
   * This endpoint retrieves a User.
   *
   * @title Get a User
   * @param userId the id of the requested User
   * @return the requested User
   * @throws UserNotFoundException if the id has not been found
   * @throws InvalidArgumentException if the id is null or empty
   */
  @GetMapping(RestEndpoints.URL_USERS_ID)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<UserRepresentationModel> getUser(@PathVariable String userId)
      throws UserNotFoundException, InvalidArgumentException {
    User user = userService.getUser(userId);

    return ResponseEntity.ok(userAssembler.toModel(user));
  }

  /**
   * This endpoint retrieves multiple Users. If a userId can't be found in the database it will be
   * ignored. If none of the given userIds is valid, the returned list will be empty.
   *
   * @title Get multiple Users
   * @param userIds the ids of the requested Users
   * @return the requested Users
   * @throws InvalidArgumentException if the userIds are null or empty
   */
  @GetMapping(RestEndpoints.URL_USERS)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<UserCollectionRepresentationModel> getUsers(
      @RequestParam(name = "user-id") String[] userIds) throws InvalidArgumentException {
    List<User> users = userService.getUsers(new HashSet<>(List.of(userIds)));

    return ResponseEntity.ok(userAssembler.toTaskanaCollectionModel(users));
  }

  /**
   * This endpoint creates a User.
   *
   * @title Create a User
   * @param repModel the User which should be created
   * @return the inserted User
   * @throws InvalidArgumentException if the id has not been set
   * @throws UserAlreadyExistException if a User with id } is already existing
   * @throws MismatchedRoleException if the current user is no admin or business-admin
   */
  @PostMapping(RestEndpoints.URL_USERS)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<UserRepresentationModel> createUser(
      @RequestBody UserRepresentationModel repModel)
      throws InvalidArgumentException, UserAlreadyExistException, MismatchedRoleException {
    User user = userAssembler.toEntityModel(repModel);
    user = userService.createUser(user);

    return ResponseEntity.status(HttpStatus.CREATED).body(userAssembler.toModel(user));
  }

  /**
   * This endpoint updates a User.
   *
   * @title Update a User
   * @param userId the id of the User to update
   * @param repModel the User with the updated fields
   * @return the updated User
   * @throws InvalidArgumentException if the id has not been set
   * @throws UserNotFoundException if a User with id is not existing in the database
   * @throws MismatchedRoleException if the current user is no admin or business-admin
   */
  @PutMapping(RestEndpoints.URL_USERS_ID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<UserRepresentationModel> updateUser(
      @PathVariable(value = "userId") String userId, @RequestBody UserRepresentationModel repModel)
      throws InvalidArgumentException, UserNotFoundException, MismatchedRoleException {
    if (!userId.equals(repModel.getUserId())) {
      throw new InvalidArgumentException(
          String.format(
              "UserId '%s' of the URI is not identical"
                  + " with the userId '%s' of the object in the payload.",
              userId, repModel.getUserId()));
    }
    User user = userAssembler.toEntityModel(repModel);
    user = userService.updateUser(user);

    return ResponseEntity.ok(userAssembler.toModel(user));
  }

  /**
   * This endpoint deletes a User.
   *
   * @title Delete a User
   * @param userId the id of the User to delete
   * @return no content
   * @throws UserNotFoundException if the id has not been found
   * @throws MismatchedRoleException if the current user is no admin or business-admin
   * @throws InvalidArgumentException if the id is null or empty
   */
  @DeleteMapping(RestEndpoints.URL_USERS_ID)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<UserRepresentationModel> deleteUser(@PathVariable String userId)
      throws UserNotFoundException, MismatchedRoleException, InvalidArgumentException {
    userService.deleteUser(userId);

    return ResponseEntity.noContent().build();
  }
}
