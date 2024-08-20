package io.kadai.sampledata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** Test SampleDataGenerator. */
class SampleDataProviderTest {

  @Test
  void getScriptsNotNull() {
    assertThat(SampleDataProvider.getSampleDataCreationScripts()).isNotNull();
  }

  @Test
  void getScriptsNotEmpty() {
    assertThat(SampleDataProvider.getSampleDataCreationScripts()).isNotEmpty();
  }

  @Test
  void getScriptsFileExists() {
    assertThat(SampleDataProvider.getSampleDataCreationScripts())
        .extracting(SqlReplacer::getScriptBufferedStream)
        .doesNotContainNull();
  }
}
