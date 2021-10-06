package testapi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import testapi.extensions.TaskanaDependencyInjectionExtension;
import testapi.extensions.TaskanaInitializationExtension;
import testapi.extensions.TestContainerExtension;

import pro.taskana.common.test.security.JaasExtension;

@ExtendWith({
  // ORDER IS IMPORTANT!
  JaasExtension.class,
  TestContainerExtension.class,
  TaskanaInitializationExtension.class,
  TaskanaDependencyInjectionExtension.class,
})
@TestInstance(Lifecycle.PER_CLASS)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskanaIntegrationTest {}
