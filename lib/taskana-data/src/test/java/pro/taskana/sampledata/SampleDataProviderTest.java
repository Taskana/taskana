package pro.taskana.sampledata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** Test SampleDataGenerator. */
class SampleDataProviderTest {

  @Test
  void getScriptsNotNull() {
    assertThat(SampleDataProvider.getSampleDataCreationScripts()).isNotNull();
    assertThat(SampleDataProvider.getScriptsWithEvents()).isNotNull();
  }

  @Test
  void getScriptsNotEmpty() {
    assertThat(SampleDataProvider.getSampleDataCreationScripts().count() > 0).isTrue();
    assertThat(SampleDataProvider.getScriptsWithEvents().count() > 0).isTrue();
  }

  @Test
  void getScriptsFileExists() {
    SampleDataProvider.getSampleDataCreationScripts()
        .map(SqlReplacer::getScriptBufferedStream)
        .forEach(script -> assertThat(script).isNotNull());
  }
}
