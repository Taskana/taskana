package pro.taskana.example.rest.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** model for a user. */
@Getter
@Setter
@ToString
public class User {

  private String username;
  private String password;
}
