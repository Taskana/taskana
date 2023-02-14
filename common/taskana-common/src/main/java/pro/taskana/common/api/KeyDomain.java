package pro.taskana.common.api;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** This class encapsulates key - domain pairs for identification of workbaskets. */
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class KeyDomain {

  private String key;
  private String domain;
}
