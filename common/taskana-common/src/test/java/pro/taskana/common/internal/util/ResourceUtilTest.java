/*-
 * #%L
 * pro.taskana:taskana-common
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
package pro.taskana.common.internal.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ResourceUtilTest {

  @Test
  void should_loadResource() throws Exception {
    String resourceAsString = ResourceUtil.readResourceAsString(getClass(), "fileInClasspath.txt");

    assertThat(resourceAsString).isEqualTo("This file is in the classpath");
  }

  @Test
  void should_ReturnNull_When_ResourceDoesNotExist() throws Exception {
    String resourceAsString = ResourceUtil.readResourceAsString(getClass(), "doesNotExist");

    assertThat(resourceAsString).isNull();
  }
}
