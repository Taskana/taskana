package pro.taskana.security;

import static org.junit.platform.commons.support.AnnotationSupport.isAnnotated;
import static pro.taskana.common.internal.util.CheckedFunction.wrap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.security.auth.Subject;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.platform.commons.JUnitException;

import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.security.GroupPrincipal;
import pro.taskana.common.internal.security.UserPrincipal;

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
    boolean annotated = isAnnotated(invocationContext.getExecutable(), WithAccessIds.class);
    if (annotated) {
      throw new JUnitException("Please use @TestTemplate instead of @Test for multiple access Ids");
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
      // we are using the first annotation to run the factory method with.
      T t = performInvocationWithAccessId(invocation, annotation.value()[0]);

      Iterable<DynamicNode> iterable;
      // TestFactory must have one of the following return types. See link below for further details
      // https://junit.org/junit5/docs/current/user-guide/#writing-tests-dynamic-tests
      if (t instanceof DynamicNode) {
        iterable = Collections.singleton((DynamicNode) t);
      } else if (t instanceof Stream) {
        Stream<DynamicNode> tt = (Stream<DynamicNode>) t;
        iterable = tt.collect(Collectors.toList());
      } else if (t instanceof Iterable) {
        iterable = (Iterable<DynamicNode>) t;
      } else if (t instanceof Iterator) {
        iterable = () -> (Iterator<DynamicNode>) t;
        // TODO: add instanceof DynamicNode[]
      } else {
        throw new SystemException(
            String.format(
                "Testfactory '%s' did not return a proper type",
                invocationContext.getExecutable().getName()));
      }
      //      StreamSupport.stream(iterable.spliterator(), false).map(node -> {
      //        if (node instanceof DynamicContainer) {
      //          return DynamicContainer.dynamicContainer(node.getDisplayName(),
      // ((DynamicContainer) node).getChildren());
      //        } return node;
      //      }).forEach(System.out::println);

      Store store = getStore(extensionContext);
      return (T)
          Stream.of(annotation.value())
              .peek(a -> store.put(ACCESS_IDS_STORE_KEY, a))
              .map(a -> DynamicContainer.dynamicContainer(getDisplayNameForAccessId(a), iterable));
    }

    return extractAccessIdAndPerformInvocation(invocation, invocationContext.getExecutable());
  }

  @Override
  public void interceptTestTemplateMethod(
      Invocation<Void> invocation,
      ReflectiveInvocationContext<Method> invocationContext,
      ExtensionContext extensionContext) {
    WithAccessId accessId =
        getStore(extensionContext).get(ACCESS_IDS_STORE_KEY, WithAccessId.class);
    performInvocationWithAccessId(invocation, accessId);
  }

  @Override
  public void interceptDynamicTest(Invocation<Void> invocation, ExtensionContext extensionContext) {
    ExtensionContext testContext = getParentMethodExtensionContent(extensionContext);
    // Check if the test factory provided an access Id for this dynamic test.
    WithAccessId o = getStore(testContext).get(ACCESS_IDS_STORE_KEY, WithAccessId.class);
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
    return isAnnotated(context.getRequiredTestMethod(), WithAccessIds.class);
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(
      ExtensionContext context) {
    WithAccessIds annotation = context.getRequiredTestMethod().getAnnotation(WithAccessIds.class);
    Store store = getStore(context);
    return Stream.of(annotation.value())
        .peek(a -> store.put(ACCESS_IDS_STORE_KEY, a))
        .map(JaasExtensionInvocationContext::new);
  }
  // endregion

  private static <T> T extractAccessIdAndPerformInvocation(
      Invocation<T> invocation, Executable executable) {
    return performInvocationWithAccessId(invocation, executable.getAnnotation(WithAccessId.class));
  }

  private static <T> T performInvocationWithAccessId(
      Invocation<T> invocation, WithAccessId withAccessId) {
    Subject subject = new Subject();
    subject.getPrincipals().addAll(getPrincipals(withAccessId));

    Function<Invocation<T>, T> proceedInvocation = wrap(Invocation::proceed);
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
    // the class MethodExtensionContext is part of junit-jupiter-engine.
    // This is a workaround so that the engine dependency doesn't have to be included.
    while (!parent
        .map(Object::getClass)
        .map(Class::getName)
        .filter(s -> s.endsWith("MethodExtensionContext"))
        .isPresent()) {
      parent = parent.flatMap(ExtensionContext::getParent);
    }
    return parent.orElseThrow(
        () ->
            new JUnitException(
                String.format(
                    "Test '%s' does not have a parent method", extensionContext.getUniqueId())));
  }

  /**
   * Gets the store with a <b>method-level</b> scope.
   *
   * @param context context for current extension
   * @return The store
   */
  private Store getStore(ExtensionContext context) {
    return context.getStore(Namespace.create(getClass(), context.getRequiredTestMethod()));
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
  }
}
