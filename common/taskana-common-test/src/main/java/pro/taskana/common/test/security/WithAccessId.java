package pro.taskana.common.test.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pro.taskana.common.test.security.WithAccessId.WithAccessIds;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Repeatable(WithAccessIds.class)
public @interface WithAccessId {

  String user();

  String[] groups() default {};

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  @interface WithAccessIds {
    WithAccessId[] value();
  }
}
