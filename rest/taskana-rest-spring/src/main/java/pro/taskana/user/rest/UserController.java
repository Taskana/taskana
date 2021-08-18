package pro.taskana.user.rest;

import static pro.taskana.common.rest.RestEndpoints.URL_USERS_ID;

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
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.user.api.UserService;
import pro.taskana.user.api.exceptions.UserAlreadyExistException;
import pro.taskana.user.api.exceptions.UserNotFoundException;
import pro.taskana.user.api.models.User;
import pro.taskana.user.rest.assembler.UserRepresentationModelAssembler;
import pro.taskana.user.rest.models.UserRepresentationModel;

/** Controller for all {@linkplain User} related endpoints. */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class UserController {
  private final UserService userService;
  private final UserRepresentationModelAssembler assembler;

  @Autowired
  UserController(UserService userService, UserRepresentationModelAssembler assembler) {
    this.userService = userService;
    this.assembler = assembler;
  }

  /**
   * This endpoint retrieves a User.
   *
   * @title Get a User
   * @param userId the id of the requested User
   * @return the requested User
   * @throws UserNotFoundException if the id has not been found
   */
  @GetMapping(URL_USERS_ID)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<UserRepresentationModel> getUser(@PathVariable String userId)
      throws UserNotFoundException {
    User user = userService.getUser(userId);

    return ResponseEntity.ok(assembler.toModel(user));
  }

  /**
   * This endpoint creates a User.
   *
   * @title Create a User
   * @param repModel the User which should be created
   * @return the inserted User
   * @throws InvalidArgumentException if the id has not been set
   * @throws UserAlreadyExistException if a User with id } is already existing
   * @throws NotAuthorizedException if the current user is no admin or business-admin
   */
  @PostMapping(RestEndpoints.URL_USERS)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<UserRepresentationModel> createUser(
      @RequestBody UserRepresentationModel repModel)
      throws InvalidArgumentException, UserAlreadyExistException, NotAuthorizedException {
    User user = assembler.toEntityModel(repModel);
    user = userService.createUser(user);

    return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(user));
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
   * @throws NotAuthorizedException if the current user is no admin or business-admin
   */
  @PutMapping(URL_USERS_ID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<UserRepresentationModel> updateUser(
      @PathVariable(value = "userId") String userId, @RequestBody UserRepresentationModel repModel)
      throws InvalidArgumentException, UserNotFoundException, NotAuthorizedException {
    if (!userId.equals(repModel.getUserId())) {
      throw new InvalidArgumentException(
          String.format(
              "UserId '%s' of the URI is not identical"
                  + " with the userId '%s' of the object in the payload.",
              userId, repModel.getUserId()));
    }
    User user = assembler.toEntityModel(repModel);
    user = userService.updateUser(user);

    return ResponseEntity.ok(assembler.toModel(user));
  }

  /**
   * This endpoint deletes a User.
   *
   * @title Delete a User
   * @param userId the id of the User to delete
   * @return no content
   * @throws UserNotFoundException if the id has not been found
   * @throws NotAuthorizedException if the current user is no admin or business-admin
   */
  @DeleteMapping(URL_USERS_ID)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<UserRepresentationModel> deleteUser(@PathVariable String userId)
      throws UserNotFoundException, NotAuthorizedException {
    userService.deleteUser(userId);

    return ResponseEntity.noContent().build();
  }
}
