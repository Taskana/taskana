package pro.taskana.common.internal.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

import pro.taskana.common.internal.util.spi.ServiceProviderInterface;

class SpiLoaderTest {

  @Test
  void should_loadServiceProviders() {
    List<ServiceProviderInterface> serviceProviders =
        SpiLoader.load(ServiceProviderInterface.class);

    assertThat(serviceProviders)
        .isNotEmpty()
        .extracting(ServiceProviderInterface::doStuff)
        .containsExactly("doing Stuff");
  }
}
