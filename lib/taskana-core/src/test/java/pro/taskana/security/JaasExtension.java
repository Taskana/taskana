package pro.taskana.security;

import java.lang.reflect.Method;
import java.security.Principal;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import javax.security.auth.Subject;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.platform.commons.JUnitException;

/** Runner for integration tests that enables JAAS subject. */
public class JaasExtension implements InvocationInterceptor {

  @Override
  public void interceptTestMethod(
      Invocation<Void> invocation,
      ReflectiveInvocationContext<Method> invocationContext,
      ExtensionContext extensionContext)
      throws Throwable {

    // check for access
    Subject subject = new Subject();
    List<Principal> principalList = new ArrayList<>();

    WithAccessId withAccessId = invocationContext.getExecutable().getAnnotation(WithAccessId.class);
    if (withAccessId != null) {
      if (withAccessId.userName() != null) {
        principalList.add(new UserPrincipal(withAccessId.userName()));
      }
      for (String groupName : withAccessId.groupNames()) {
        if (groupName != null) {
          principalList.add(new GroupPrincipal(groupName));
        }
      }
    }
    subject.getPrincipals().addAll(principalList);
    Subject.doAs(subject, getObjectPrivilegedExceptionAction(invocation, invocationContext));
  }

  private PrivilegedExceptionAction<Object> getObjectPrivilegedExceptionAction(
      Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext) {
    return () -> {
      try {
        invocation.proceed();
      } catch (Exception | Error e) {
        throw e;
      } catch (Throwable e) {
        throw new JUnitException(
            "Execution of test failed: " + invocationContext.getExecutable().getName(), e);
      }
      return null;
    };
  }
}
