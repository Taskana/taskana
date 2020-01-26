package pro.taskana.sampledata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Test SampleDataGenerator. */
class SampleDataProviderTest {

  @Test
  void getScriptsNotNull() {
    assertThat(SampleDataProvider.getDefaultScripts()).isNotNull();
    assertThat(SampleDataProvider.getScriptsWithEvents()).isNotNull();
  }

  @Test
  void getScriptsNotEmpty() {
    assertThat(SampleDataProvider.getDefaultScripts().count() > 0).isTrue();
    assertThat(SampleDataProvider.getScriptsWithEvents().count() > 0).isTrue();
  }

  @Test
  void getScriptsFileExists() {
    SampleDataProvider.getDefaultScripts()
        .map(SqlReplacer::getScriptBufferedStream)
        .forEach(Assertions::assertNotNull);
  }
}
