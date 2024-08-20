package io.kadai.example.wildfly;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinates;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.impl.maven.coordinate.MavenDependencyImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test class is configured to run with postgres DB. In order to run them on db2, change the
 * datasource.jndi to java:jboss/datasources/ExampleDS and set kadai.schemaName to KADAI
 */
@RunWith(Arquillian.class)
public class KadaiWildflyWithHistoryLoggerEnabledTest extends AbstractAccTest {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(KadaiWildflyWithHistoryLoggerEnabledTest.class);

  @Deployment(testable = false)
  public static Archive<?> createTestArchive() {

    String applicationPropertyFile = "application.properties";
    String dbType = System.getProperty("db.type");
    if (dbType != null && !dbType.isEmpty()) {
      applicationPropertyFile = "application-" + dbType + ".properties";
    }

    LOGGER.info(
        "Running with db.type '{}' and using property file '{}'", dbType, applicationPropertyFile);

    MavenCoordinate historyLoggerCoordinate =
        MavenCoordinates.createCoordinate(
            "io.kadai.history",
            "kadai-loghistory-provider",
            DEPENDENCY_VERSION,
            PackagingType.JAR,
            null);

    MavenDependency historyLoggerDependency =
        new MavenDependencyImpl(historyLoggerCoordinate, ScopeType.TEST, false);

    File[] files =
        Maven.resolver()
            .loadPomFromFile("pom.xml")
            .importCompileAndRuntimeDependencies()
            .resolve()
            .withTransitivity()
            .asFile();

    return ShrinkWrap.create(WebArchive.class, "kadai.war")
        .addPackages(true, "io.kadai")
        .addAsResource("kadai.properties")
        .addAsResource(applicationPropertyFile, "application.properties")
        .addAsResource("kadai-test.ldif")
        .addAsWebInfResource("int-test-web.xml", "web.xml")
        .addAsWebInfResource("int-test-jboss-web.xml", "jboss-web.xml")
        .addAsLibraries(files);
  }

  @Test
  @RunAsClient
  public void should_WriteHistoryEventIntoDatabase_And_LogEventToFile() throws Exception {

    String log = parseServerLog();

    assertThat(log).contains("AUDIT");
  }
}
