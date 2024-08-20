package io.kadai.common.test.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

/** Use this annotation to test with a spring context and a standardized configuration. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
// DirtiesContext is required to make the integration tests run with embedded LDAP.
// Otherwise the LDAP server is not shut down correctly and will not come up again. (socket busy)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ActiveProfiles({"test"})
@SpringBootTest(
    classes = TestConfiguration.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public @interface KadaiSpringBootTest {}
