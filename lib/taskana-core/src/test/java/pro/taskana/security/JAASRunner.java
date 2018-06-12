package pro.taskana.security;

import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.Subject;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * Runner for integration tests that enables JAAS subject.
 */
public class JAASRunner extends BlockJUnit4ClassRunner {

    public JAASRunner(Class<?> c) throws InitializationError {
        super(c);
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {

        Subject subject = new Subject();
        List<Principal> principalList = new ArrayList<>();

        if (test != null) {
            WithAccessId withAccessId = method.getMethod().getAnnotation(WithAccessId.class);
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
        }

        final Statement base = super.methodInvoker(method, test);
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                try {
                    Subject.doAs(subject, new PrivilegedExceptionAction<Object>() {

                        @Override
                        public Object run() throws Exception {

                            try {
                                base.evaluate();
                            } catch (Throwable e) {
                                throw new Exception(e);
                            }
                            return null;
                        }
                    });
                } catch (PrivilegedActionException e) {
                    Throwable cause = e.getCause();
                    Throwable nestedCause = null;
                    if (cause != null) {
                        nestedCause = cause.getCause();
                    }
                    if (nestedCause != null) {
                        throw nestedCause;
                    } else if (cause != null) {
                        throw cause;
                    } else {
                        throw e;
                    }
                }
            }
        };
    }

}
