package io.kadai.common.internal.util;

import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.common.internal.util.spi.ServiceProviderInterface;
import java.util.List;
import org.junit.jupiter.api.Test;

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
