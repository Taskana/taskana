package io.kadai.example.boot;

import io.kadai.KadaiConfiguration;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.internal.SpringKadaiEngine;
import io.kadai.common.internal.configuration.DbSchemaCreator;
import io.kadai.sampledata.SampleDataGenerator;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ExampleRestConfiguration {

  @Bean
  public PlatformTransactionManager txManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  @Bean
  @DependsOn("kadaiConfiguration") // generate sample data after schema was inserted
  public SampleDataGenerator generateSampleData(
      KadaiConfiguration kadaiConfiguration,
      DataSource dataSource,
      @Value("${generateSampleData:true}") boolean generateSampleData)
      throws SQLException {
    DbSchemaCreator dbSchemaCreator =
        new DbSchemaCreator(dataSource, kadaiConfiguration.getSchemaName());
    dbSchemaCreator.run();
    SampleDataGenerator sampleDataGenerator =
        new SampleDataGenerator(dataSource, kadaiConfiguration.getSchemaName());
    if (generateSampleData) {
      sampleDataGenerator.generateSampleData();
    }
    return sampleDataGenerator;
  }

  @Bean
  @DependsOn("generateSampleData")
  public KadaiEngine getKadaiEngine(KadaiConfiguration kadaiConfiguration) throws SQLException {
    return SpringKadaiEngine.buildKadaiEngine(kadaiConfiguration);
  }

  // only required to let the adapter example connect to the same database
  @Bean(initMethod = "start", destroyMethod = "stop")
  public Server inMemoryH2DatabaseaServer() throws SQLException {
    return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9095");
  }
}
