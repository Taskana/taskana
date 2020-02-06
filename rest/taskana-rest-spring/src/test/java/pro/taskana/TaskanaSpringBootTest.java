package pro.taskana;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import pro.taskana.rest.RestConfiguration;

/** Use this annotation to test with a spring context and a standardized configuration. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ActiveProfiles({"test"})
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = RestConfiguration.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.main.allow-bean-definition-overriding=true")
public @interface TaskanaSpringBootTest {}
