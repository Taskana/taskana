package pro.taskana.common.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import pro.taskana.RestConfiguration;

/** Use this annotation to test with a spring context and a standardized configuration. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@Inherited
@ActiveProfiles({"test"})
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = RestConfiguration.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public @interface TaskanaSpringBootTest {}
