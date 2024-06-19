package pro.taskana.common.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.rest.ldap.LdapClient;
import pro.taskana.common.rest.models.AccessIdRepresentationModel;

/** Controller for Access Id validation. */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class AccessIdController {

  private final LdapClient ldapClient;
  private final TaskanaEngine taskanaEngine;

  @Autowired
  public AccessIdController(LdapClient ldapClient, TaskanaEngine taskanaEngine) {
    this.ldapClient = ldapClient;
    this.taskanaEngine = taskanaEngine;
  }

  /**
   * This endpoint searches a provided access Id in the configured ldap.
   *
   * @param searchFor the Access Id which should be searched for.
   * @return a list of all found Access Ids
   * @throws InvalidArgumentException if the provided search for Access Id is shorter than the
   *     configured one.
   * @throws NotAuthorizedException if the current user is not ADMIN or BUSINESS_ADMIN.
   * @title Search for Access Id (users and groups)
   */
  @Operation(
      summary = "Search for Access Id (users and groups)",
      description = "This endpoint searches a provided access Id in the configured ldap.",
      parameters = {
        @Parameter(
            name = "search-for",
            description = "the Access Id which should be searched for.",
            example = "max",
            required = true)
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "a list of all found Access Ids",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = AccessIdRepresentationModel[].class)))
      })
  @GetMapping(path = RestEndpoints.URL_ACCESS_ID)
  public ResponseEntity<List<AccessIdRepresentationModel>> searchUsersAndGroups(
      @RequestParam("search-for") String searchFor)
      throws InvalidArgumentException, NotAuthorizedException {
    taskanaEngine.checkRoleMembership(TaskanaRole.ADMIN, TaskanaRole.BUSINESS_ADMIN);

    List<AccessIdRepresentationModel> accessIdUsers = ldapClient.searchUsersAndGroups(searchFor);
    return ResponseEntity.ok(accessIdUsers);
  }

  /**
   * This endpoint searches AccessIds for a provided name or Access Id. It will only search and
   * return users and members of groups which are configured with the requested TASKANA role. This
   * search will only work if the users in the configured LDAP have an attribute that shows their
   * group memberships, e.g. "memberOf"
   *
   * @param nameOrAccessId the name or Access Id which should be searched for.
   * @param role the role for which all users should be searched for
   * @return a list of all found Access Ids (users)
   * @throws InvalidArgumentException if the provided search for Access Id is shorter than the
   *     configured one.
   * @throws NotAuthorizedException if the current user is not member of role USER, BUSINESS_ADMIN
   *     or ADMIN
   * @title Search for Access Id (users) in TASKANA user role
   */
  @Operation(
      summary = "Search for Access Id (users) in TASKANA user role",
      description =
          "This endpoint searches AccessIds for a provided name or Access Id. It will only search "
              + "and return users and members of groups which are configured with the requested "
              + "TASKANA role. This search will only work if the users in the configured LDAP have"
              + " an attribute that shows their group memberships, e.g. \"memberOf\"",
      parameters = {
        @Parameter(
            name = "search-for",
            description = "the name or Access Id which should be searched for.",
            example = "user-1",
            required = true),
        @Parameter(
            name = "role",
            description = "the role for which all users should be searched for",
            example = "user",
            required = true)
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "a list of all found Access Ids (users)",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = AccessIdRepresentationModel[].class)))
      })
  @GetMapping(path = RestEndpoints.URL_ACCESS_ID_WITH_NAME)
  public ResponseEntity<List<AccessIdRepresentationModel>> searchUsersByNameOrAccessIdForRole(
      @RequestParam("search-for") String nameOrAccessId, @RequestParam("role") String role)
      throws InvalidArgumentException, NotAuthorizedException {
    taskanaEngine.checkRoleMembership(
        TaskanaRole.USER, TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);

    if (!role.equals("user")) {
      throw new InvalidArgumentException(
          String.format(
              "Requested users for not supported role %s.  Only role 'user' is supported'", role));
    }
    List<AccessIdRepresentationModel> accessIdUsers =
        ldapClient.searchUsersByNameOrAccessIdInUserRole(nameOrAccessId);
    return ResponseEntity.ok(accessIdUsers);
  }

  /**
   * This endpoint retrieves all groups a given Access Id belongs to.
   *
   * @param accessId the Access Id whose groups should be determined.
   * @return a list of the group Access Ids the requested Access Id belongs to
   * @throws InvalidArgumentException if the requested Access Id does not exist or is not unique.
   * @throws NotAuthorizedException if the current user is not ADMIN or BUSINESS_ADMIN.
   * @title Get groups for Access Id
   */
  @Operation(
      summary = "Get groups for Access Id",
      description = "This endpoint retrieves all groups a given Access Id belongs to.",
      parameters = {
        @Parameter(
            name = "access-id",
            description = "the Access Id whose groups should be determined.",
            example = "teamlead-1",
            required = true)
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "a list of the group Access Ids the requested Access Id belongs to",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = AccessIdRepresentationModel[].class)))
      })
  @GetMapping(path = RestEndpoints.URL_ACCESS_ID_GROUPS)
  public ResponseEntity<List<AccessIdRepresentationModel>> getGroupsByAccessId(
      @RequestParam("access-id") String accessId)
      throws InvalidArgumentException, NotAuthorizedException {
    taskanaEngine.checkRoleMembership(TaskanaRole.ADMIN, TaskanaRole.BUSINESS_ADMIN);

    List<AccessIdRepresentationModel> accessIds =
        ldapClient.searchGroupsAccessIdIsMemberOf(accessId);

    return ResponseEntity.ok(accessIds);
  }
}
