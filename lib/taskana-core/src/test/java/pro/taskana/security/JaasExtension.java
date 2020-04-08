package pro.taskana.security;

import java.lang.reflect.Method;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import javax.security.auth.Subject;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.platform.commons.JUnitException;

import pro.taskana.common.internal.security.GroupPrincipal;
import pro.taskana.common.internal.security.UserPrincipal;

/** Runner for integration tests that enables JAAS subject. */
public class JaasExtension implements InvocationInterceptor {

  @Override
  public void interceptBeforeEachMethod(
      Invocation<Void> invocation,
      ReflectiveInvocationContext<Method> invocationContext,
      ExtensionContext extensionContext)
      throws Throwable {
    extractAccessId(invocation, invocationContext.getExecutable());
  }

  @Override
  public void interceptTestMethod(
      Invocation<Void> invocation,
      ReflectiveInvocationContext<Method> invocationContext,
      ExtensionContext extensionContext)
      throws Throwable {
    extractAccessId(invocation, invocationContext.getExecutable());
  }

  @Override
  public <T> T interceptTestFactoryMethod(
      Invocation<T> invocation,
      ReflectiveInvocationContext<Method> invocationContext,
      ExtensionContext extensionContext)
      throws Throwable {
    return extractAccessId(invocation, invocationContext.getExecutable());
  }

  @Override
  public void interceptDynamicTest(Invocation<Void> invocation, ExtensionContext extensionContext)
      throws Throwable {
    extractAccessId(invocation, extensionContext.getParent().get().getRequiredTestMethod());
  }

  private <T> T extractAccessId(Invocation<T> invocation, Method method)
      throws PrivilegedActionException {
    // check for access
    Subject subject = new Subject();
    List<Principal> principalList = new ArrayList<>();

    WithAccessId withAccessId = method.getAnnotation(WithAccessId.class);
    if (withAccessId != null) {
      withAccessId.userName();
      principalList.add(new UserPrincipal(withAccessId.userName()));
      for (String groupName : withAccessId.groupNames()) {
        if (groupName != null) {
          principalList.add(new GroupPrincipal(groupName));
        }
      }
    }
    subject.getPrincipals().addAll(principalList);
    return Subject.doAs(subject, getObjectPrivilegedExceptionAction(invocation, method));
  }

  private <T> PrivilegedExceptionAction<T> getObjectPrivilegedExceptionAction(
      Invocation<T> invocation, Method invocationContext) {
    return () -> {
      try {
        return invocation.proceed();
      } catch (Exception | Error e) {
        throw e;
      } catch (Throwable e) {
        throw new JUnitException("Execution of test failed: " + invocationContext.getName(), e);
      }
    };
  }
}
