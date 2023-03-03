/*-
 * #%L
 * pro.taskana:taskana-rest-spring-test-lib
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.rest.test;

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
public @interface TaskanaSpringBootTest {}
