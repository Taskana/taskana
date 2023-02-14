package pro.taskana.user.rest.models;

import java.util.Collections;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import pro.taskana.user.api.models.User;

/** The entityModel class for {@linkplain User}. */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class UserRepresentationModel extends RepresentationModel<UserRepresentationModel> {

  /** Unique Id. */
  @NotNull private String userId;
  /** The groups of the User. */
  private Set<String> groups;
  /**
   * The domains of the User.
   *
   * <p>The domains are derived from the WorkbasketPermissions and the according TASKANA property
   * taskana.user.minimalPermissionsToAssignDomains
   */
  private Set<String> domains = Collections.emptySet();
  /** The first name of the User. */
  private String firstName;
  /** The last name of the User. */
  private String lastName;
  /** The full name of the User. */
  private String fullName;
  /** The long name of the User. */
  private String longName;
  /** The email of the User. */
  private String email;
  /** The phone number of the User. */
  private String phone;
  /** The mobile phone number of the User. */
  private String mobilePhone;
  /** The fourth organisation level of the User. */
  private String orgLevel4;
  /** The third organisation level of the User. */
  private String orgLevel3;
  /** The second organisation level of the User. */
  private String orgLevel2;
  /** The first organisation level of the User. */
  private String orgLevel1;
  /** The data of the User. This field is used for additional information about the User. */
  private String data;
}
