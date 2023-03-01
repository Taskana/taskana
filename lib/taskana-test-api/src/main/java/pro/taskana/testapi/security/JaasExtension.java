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
package pro.taskana.testapi.security;

import static org.junit.platform.commons.support.AnnotationSupport.isAnnotated;
import static pro.taskana.common.internal.util.CheckedFunction.wrapExceptFor;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.security.auth.Subject;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.extension.DynamicTestInvocationContext;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.commons.support.AnnotationSupport;
import org.opentest4j.TestAbortedException;

import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.security.GroupPrincipal;
import pro.taskana.common.api.security.UserPrincipal;
import pro.taskana.testapi.security.WithAccessId.WithAccessIds;

/** Runner for integration tests that enables JAAS subject. */
public class JaasExtension implements InvocationInterceptor, TestTemplateInvocationContextProvider {

  private static final String ACCESS_IDS_STORE_KEY = "accessIds";

  // region InvocationInterceptor

  @Override
  public <T> T interceptTestClassConstructor(
      Invocation<T> invocation,
      ReflectiveInvocationContext<Constructor<T>> invocationContext,
      ExtensionContext extensionContext) {
    return extractAccessIdAndPerformInvocation(invocation, invocationContext.getExecutable());
  }

  @Override
  public void interceptBeforeAllMethod(
      Invocation<Void> invocation,
      ReflectiveInvocationContext<Method> invocationContext,
      ExtensionContext extensionContext) {
    extractAccessIdAndPerformInvocation(invocation, invocationContext.getExecutable());
  }

  @Override
  public void interceptBeforeEachMethod(
      Invocation<Void> invocation,
      ReflectiveInvocationContext<Method> invocationContext,
      ExtensionContext extensionContext) {
    extractAccessIdAndPerformInvocation(invocation, invocationContext.getExecutable());
  }

  @Override
  public void interceptTestMethod(
      Invocation<Void> invocation,
      ReflectiveInvocationContext<Method> invocationContext,
      ExtensionContext extensionContext) {
    if (isAnnotated(invocationContext.getExecutable(), WithAccessIds.class)) {
      throw new JUnitException("Please use @TestTemplate instead of @Test for multiple accessIds");
    }
    extractAccessIdAndPerformInvocation(invocation, invocationContext.getExecutable());
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T interceptTestFactoryMethod(
      Invocation<T> invocation,
      ReflectiveInvocationContext<Method> invocationContext,
      ExtensionContext extensionContext) {
    WithAccessIds annotation = invocationContext.getExecutable().getAnnotation(WithAccessIds.class);
    if (annotation != null) {
      // our goal is to run each test returned from the test factory X times. X is the amount of
      // WithAccessId annotations. In order to achieve this we are wrapping the result from the
      // factory (the returning tests) in a dynamicContainer for each accessId. Since we don't know
      // what the factory will return we have to check for every possible return type. All possible
      // return types can be found here:
      // https://junit.org/junit5/docs/current/user-guide/#writing-tests-dynamic-tests
      // After checking each return type we abuse the return type of T and hardly change it to
      // Stream<DynamicContainer> no matter what the factory returns. This return type is allowed
      // per definition (See link above), but is not the type T. Hence we have an unchecked cast at
      // the end to keep the compiler happy...

      // we are using the first annotation to run the factory method with.
      T factoryResult = performInvocationWithAccessId(invocation, annotation.value()[0]);

      Iterable<DynamicNode> newChildrenForDynamicContainer;
      // TestFactory must have one of the following return types. See link above for further details
      if (factoryResult instanceof DynamicNode) {
        newChildrenForDynamicContainer = Collections.singleton((DynamicNode) factoryResult);
      } else if (factoryResult instanceof Stream) {
        Stream<DynamicNode> nodes = (Stream<DynamicNode>) factoryResult;
        newChildrenForDynamicContainer = nodes.collect(Collectors.toList());
      } else if (factoryResult instanceof Iterable) {
        newChildrenForDynamicContainer = (Iterable<DynamicNode>) factoryResult;
      } else if (factoryResult instanceof Iterator) {
        newChildrenForDynamicContainer = () -> (Iterator<DynamicNode>) factoryResult;
      } else if (factoryResult instanceof DynamicNode[]) {
        newChildrenForDynamicContainer = Arrays.asList((DynamicNode[]) factoryResult);
      } else {
        throw new SystemException(
            String.format(
                "Testfactory '%s' did not return a proper type",
                invocationContext.getExecutable().getName()));
      }

      // Currently, a DynamicContainer has children from this type: Stream<DynamicNode>
      // Because of this the children can only be extracted once (Streams can only be operated
      // once). This is obviously not ok since we want to execute each node X times. So we have to
      // manually insert all children recursively to extract them X times...
      Map<String, List<DynamicNode>> childrenMap = new HashMap<>();
      persistDynamicContainerChildren(newChildrenForDynamicContainer, childrenMap);

      Function<WithAccessId, DynamicContainer> wrapTestsInDynamicContainer =
          accessId ->
              DynamicContainer.dynamicContainer(
                  getDisplayNameForAccessId(accessId),
                  StreamSupport.stream(newChildrenForDynamicContainer.spliterator(), false)
                      .map(x -> duplicateDynamicNode(x, childrenMap)));

      Store store = getMethodLevelStore(extensionContext);
      return (T)
          Stream.of(annotation.value())
              .peek(a -> store.put(ACCESS_IDS_STORE_KEY, a))
              .map(wrapTestsInDynamicContainer);
    }

    return extractAccessIdAndPerformInvocation(invocation, invocationContext.getExecutable());
  }

  @Override
  public void interceptTestTemplateMethod(
      Invocation<Void> invocation,
      ReflectiveInvocationContext<Method> invocationContext,
      ExtensionContext extensionContext) {
    WithAccessId accessId =
        getMethodLevelStore(extensionContext).get(ACCESS_IDS_STORE_KEY, WithAccessId.class);
    performInvocationWithAccessId(invocation, accessId);
  }

  @Override
  public void interceptDynamicTest(
      Invocation<Void> invocation,
      DynamicTestInvocationContext invocationContext,
      ExtensionContext extensionContext) {
    ExtensionContext testContext = getParentMethodExtensionContent(extensionContext);
    // Check if the test factory provided an accessId for this dynamic test.
    WithAccessId o = getMethodLevelStore(testContext).get(ACCESS_IDS_STORE_KEY, WithAccessId.class);
    if (o != null) {
      performInvocationWithAccessId(invocation, o);
    } else {
      extractAccessIdAndPerformInvocation(invocation, testContext.getRequiredTestMethod());
    }
  }

  @Override
  public void interceptAfterEachMethod(
      Invocation<Void> invocation,
      ReflectiveInvocationContext<Method> invocationContext,
      ExtensionContext extensionContext) {
    extractAccessIdAndPerformInvocation(invocation, invocationContext.getExecutable());
  }

  @Override
  public void interceptAfterAllMethod(
      Invocation<Void> invocation,
      ReflectiveInvocationContext<Method> invocationContext,
      ExtensionContext extensionContext) {
    extractAccessIdAndPerformInvocation(invocation, invocationContext.getExecutable());
  }

  // endregion

  // region TestTemplateInvocationContextProvider

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return isAnnotated(context.getElement(), WithAccessIds.class)
        || isAnnotated(context.getElement(), WithAccessId.class);
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(
      ExtensionContext context) {
    List<WithAccessId> accessIds =
        AnnotationSupport.findRepeatableAnnotations(context.getElement(), WithAccessId.class);
    Store store = getMethodLevelStore(context);
    return accessIds.stream()
        .peek(a -> store.put(ACCESS_IDS_STORE_KEY, a))
        .map(JaasExtensionInvocationContext::new);
  }

  // endregion

  private static void persistDynamicContainerChildren(
      Iterable<DynamicNode> nodes, Map<String, List<DynamicNode>> childrenMap) {
    nodes.forEach(
        node -> {
          if (node instanceof DynamicContainer) {
            DynamicContainer container = (DynamicContainer) node;
            List<DynamicNode> children = container.getChildren().collect(Collectors.toList());
            childrenMap.put(container.hashCode() + container.getDisplayName(), children);
            persistDynamicContainerChildren(children, childrenMap);
          }
        });
  }

  private static DynamicNode duplicateDynamicNode(
      DynamicNode node, Map<String, List<DynamicNode>> lookupMap) {
    if (node instanceof DynamicContainer) {
      DynamicContainer container = (DynamicContainer) node;
      Stream<DynamicNode> children =
          lookupMap.get(node.hashCode() + node.getDisplayName()).stream()
              .map(x -> duplicateDynamicNode(x, lookupMap));
      return DynamicContainer.dynamicContainer(container.getDisplayName(), children);
    }
    return node;
  }

  private static <T> T extractAccessIdAndPerformInvocation(
      Invocation<T> invocation, AnnotatedElement executable) {
    return performInvocationWithAccessId(invocation, executable.getAnnotation(WithAccessId.class));
  }

  private static <T> T performInvocationWithAccessId(
      Invocation<T> invocation, WithAccessId withAccessId) {
    Subject subject = new Subject();
    subject.getPrincipals().addAll(getPrincipals(withAccessId));

    Function<Invocation<T>, T> proceedInvocation =
        wrapExceptFor(Invocation::proceed, TestAbortedException.class);
    PrivilegedAction<T> performInvocation = () -> proceedInvocation.apply(invocation);
    return Subject.doAs(subject, performInvocation);
  }

  private static List<Principal> getPrincipals(WithAccessId withAccessId) {
    if (withAccessId != null) {
      return Stream.concat(
              Stream.of(withAccessId.user()).map(UserPrincipal::new),
              Arrays.stream(withAccessId.groups()).map(GroupPrincipal::new))
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  private ExtensionContext getParentMethodExtensionContent(ExtensionContext extensionContext) {
    Optional<ExtensionContext> parent = extensionContext.getParent();
    // the class MethodExtensionContext is part of junit-jupiter-engine and has only a
    // package-private visibility thus this workaround is needed.
    while (parent
        .map(Object::getClass)
        .map(Class::getName)
        .filter(s -> s.endsWith("MethodExtensionContext"))
        .isEmpty()) {
      parent = parent.flatMap(ExtensionContext::getParent);
    }
    return parent.orElseThrow(
        () ->
            new JUnitException(
                String.format(
                    "Test '%s' does not have a parent method", extensionContext.getUniqueId())));
  }

  private static Store getMethodLevelStore(ExtensionContext context) {
    return context.getStore(
        Namespace.create(context.getRequiredTestClass(), context.getRequiredTestMethod()));
  }

  private static String getDisplayNameForAccessId(WithAccessId withAccessId) {
    return String.format("for user '%s'", withAccessId.user());
  }

  private static class JaasExtensionInvocationContext implements TestTemplateInvocationContext {

    private final WithAccessId withAccessId;

    private JaasExtensionInvocationContext(WithAccessId withAccessId) {
      this.withAccessId = withAccessId;
    }

    @Override
    public String getDisplayName(int invocationIndex) {
      return getDisplayNameForAccessId(withAccessId);
    }

    @Override
    public List<Extension> getAdditionalExtensions() {
      return Collections.singletonList(new WithAccessIdParameterResolver());
    }

    private class WithAccessIdParameterResolver implements ParameterResolver {

      @Override
      public boolean supportsParameter(
          ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(WithAccessId.class);
      }

      @Override
      public Object resolveParameter(
          ParameterContext parameterContext, ExtensionContext extensionContext) {
        return withAccessId;
      }
    }
  }
}
