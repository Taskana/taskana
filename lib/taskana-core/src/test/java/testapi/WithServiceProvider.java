package testapi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import testapi.WithServiceProvider.WithServiceProviders;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(WithServiceProviders.class)
public @interface WithServiceProvider {

  Class<?> serviceProviderInterface();

  Class<?>[] serviceProviders();

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  @interface WithServiceProviders {
    WithServiceProvider[] value();
  }
}
