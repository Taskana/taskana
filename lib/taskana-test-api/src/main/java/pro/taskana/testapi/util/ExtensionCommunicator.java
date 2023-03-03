/*-
 * #%L
 * pro.taskana:taskana-test-api
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
package pro.taskana.testapi.util;

import static org.junit.platform.commons.support.AnnotationSupport.isAnnotated;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

import pro.taskana.testapi.CleanTaskanaContext;
import pro.taskana.testapi.TaskanaEngineConfigurationModifier;
import pro.taskana.testapi.WithServiceProvider;

public class ExtensionCommunicator {

  private ExtensionCommunicator() {
    throw new IllegalStateException("utility class");
  }

  public static boolean isTopLevelClass(Class<?> testClass) {
    return testClass.getEnclosingClass() == null;
  }

  public static Store getClassLevelStore(ExtensionContext context) {
    return getClassLevelStore(context, context.getRequiredTestClass());
  }

  public static Store getClassLevelStore(ExtensionContext context, Class<?> testClass) {
    return context.getStore(determineNamespace(testClass));
  }

  public static Class<?> getTopLevelClass(Class<?> testClazz) {
    Class<?> parent = testClazz;
    while (parent.getEnclosingClass() != null) {
      parent = parent.getEnclosingClass();
    }
    return parent;
  }

  private static Namespace determineNamespace(Class<?> testClass) {
    if (isTopLevelClass(testClass)) {
      return Namespace.create(testClass);
    } else if (isAnnotated(testClass, CleanTaskanaContext.class)) {
      return Namespace.create(getTopLevelClass(testClass), testClass, CleanTaskanaContext.class);
    } else if (isAnnotated(testClass, WithServiceProvider.class)) {
      return Namespace.create(getTopLevelClass(testClass), testClass, WithServiceProvider.class);
    } else if (TaskanaEngineConfigurationModifier.class.isAssignableFrom(testClass)) {
      return Namespace.create(
          getTopLevelClass(testClass), testClass, TaskanaEngineConfigurationModifier.class);
    }
    return Namespace.create(getTopLevelClass(testClass));
  }
}
