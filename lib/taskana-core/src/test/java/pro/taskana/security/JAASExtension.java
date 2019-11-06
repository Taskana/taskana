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

/**
 * Runner for integration tests that enables JAAS subject.
 */
public class JAASExtension implements InvocationInterceptor {

    @Override
    public void interceptTestMethod(Invocation<Void> invocation,
        ReflectiveInvocationContext<Method> invocationContext,
        ExtensionContext extensionContext) throws Throwable {

        //check for access
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
        Subject.doAs(subject, (PrivilegedExceptionAction<Object>) () -> {

            try {
                invocation.proceed();
            } catch (Throwable e) {
                throw new Exception(e);
            }
            return null;
        });

    }

}
