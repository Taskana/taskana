/*-
 * #%L
 * pro.taskana:taskana-spring-example
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
package pro.taskana.example;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;

/** Class to start an Application to test Taskana. */
@SpringBootApplication
@Import(TaskanaConfig.class)
@SuppressWarnings("checkstyle:hideutilityclassconstructor")
public class TaskanaConfigTestApplication {

  public static void main(String[] args) {
    new SpringApplicationBuilder(TaskanaConfigTestApplication.class).run(args);
  }
}
