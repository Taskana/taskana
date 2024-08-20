package acceptance.common;

import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.KadaiConfiguration;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.KadaiEngine.ConnectionManagementMode;
import io.kadai.common.internal.configuration.DB;
import io.kadai.common.internal.configuration.DbSchemaCreator;
import io.kadai.testapi.OracleSchemaHelper;
import io.kadai.testapi.extensions.TestContainerExtension;
import org.junit.jupiter.api.Test;

class KadaiEngineExplicitTest {

  @Test
  void should_CreateKadaiEngine_When_ExplizitModeIsActive() throws Exception {

    String schemaName = TestContainerExtension.determineSchemaName();
    if (DB.ORACLE == TestContainerExtension.EXECUTION_DATABASE) {
      OracleSchemaHelper.initOracleSchema(TestContainerExtension.DATA_SOURCE, schemaName);
    }

    KadaiConfiguration kadaiConfiguration =
        new KadaiConfiguration.Builder(TestContainerExtension.DATA_SOURCE, false, schemaName, true)
            .initKadaiProperties()
            .build();

    KadaiEngine.buildKadaiEngine(kadaiConfiguration, ConnectionManagementMode.EXPLICIT);

    DbSchemaCreator dsc =
        new DbSchemaCreator(kadaiConfiguration.getDataSource(), kadaiConfiguration.getSchemaName());
    assertThat(dsc.isValidSchemaVersion(KadaiEngine.MINIMAL_KADAI_SCHEMA_VERSION)).isTrue();
  }
}
