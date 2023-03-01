/*-
 * #%L
 * pro.taskana.history:taskana-simplehistory-rest-spring
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
package pro.taskana.simplehistory.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;

/** Configuration for Taskana history REST service. */
@Configuration
@ComponentScan("pro.taskana")
@EnableTransactionManagement
public class TaskHistoryRestConfiguration {

  @Bean
  public SimpleHistoryServiceImpl getSimpleHistoryService() {
    return new SimpleHistoryServiceImpl();
  }
}
