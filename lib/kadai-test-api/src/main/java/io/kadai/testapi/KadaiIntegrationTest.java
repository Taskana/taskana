package io.kadai.testapi;

import io.kadai.testapi.extensions.KadaiDependencyInjectionExtension;
import io.kadai.testapi.extensions.KadaiInitializationExtension;
import io.kadai.testapi.extensions.TestContainerExtension;
import io.kadai.testapi.security.JaasExtension;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({
  // ORDER IS IMPORTANT!
  JaasExtension.class,
  TestContainerExtension.class,
  KadaiInitializationExtension.class,
  KadaiDependencyInjectionExtension.class,
})
@TestInstance(Lifecycle.PER_CLASS)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface KadaiIntegrationTest {}
