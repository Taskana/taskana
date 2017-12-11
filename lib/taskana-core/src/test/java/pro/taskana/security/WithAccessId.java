package pro.taskana.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify user id for JUnit JAASRunner.
 * @author bbr
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface WithAccessId {
    String userName();
    String[] groupNames() default {};
}
