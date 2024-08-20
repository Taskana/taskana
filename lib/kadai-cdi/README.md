# kadai-cdi

this module is for EJB deployments.

## Testing procedure

1. deploy wildfly server locally
2. replace h2 with latest version
3. start wildfly
4. deploy ear
5. test application

### deploy wildfly server locally

we extract the wildfly server into the target directory via maven configuration

```xml
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-dependency-plugin</artifactId>
        <version>${version.maven.dependency}</version>
        <executions>
          <execution>
            <id>unpack-wildfly</id>
            <phase>process-test-classes</phase>
            <goals>
              <goal>unpack</goal>
            </goals>
            <configuration>
              <artifactItems>
                <artifactItem>
                  <groupId>org.wildfly</groupId>
                  <artifactId>wildfly-dist</artifactId>
                  <version>${version.wildfly}</version>
                  <type>zip</type>
                  <overWrite>false</overWrite>
                  <outputDirectory>${project.build.directory}</outputDirectory>
                </artifactItem>
              </artifactItems>
            </configuration>
          </execution>
          .
          .
          .
        </executions>
      </plugin>
```

### 2. replace h2 with latest version

for our tests we need the latest h2 version, so we need to replace the h2 jar in wildfly.

this happens in 2 steps.

first extract the dependency into the correct directory

```xml
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-dependency-plugin</artifactId>
        <version>${version.maven.dependency}</version>
        <executions>
          .
          .
          .
          <execution>
            <id>copy-latest-h2-db-driver</id>
            <phase>process-test-classes</phase>
            <goals>
              <goal>copy</goal>
            </goals>
            <configuration>
              <artifactItems>
                <artifactItem>
                  <groupId>com.h2database</groupId>
                  <artifactId>h2</artifactId>
                  <outputDirectory>
                    ${project.build.directory}/wildfly-${version.wildfly}/modules/system/layers/base/com/h2database/h2/main
                  </outputDirectory>
                </artifactItem>
              </artifactItems>
            </configuration>
          </execution>
        </executions>
      </plugin>
```

second step is to copy the `src/test/resources/module.xml` to required directory

```xml
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-resources-plugin</artifactId>
        <version>${version.maven.resources}</version>
        <executions>
          <execution>
            <id>copy-h2-module-xml</id>
            <phase>process-test-classes</phase>
            <goals>
              <goal>copy-resources</goal>
            </goals>
            <configuration>
              <outputDirectory>
                ${project.build.directory}/wildfly-${version.wildfly}/modules/system/layers/base/com/h2database/h2/main
              </outputDirectory>
              <resources>
                <resource>
                  <directory>src/test/resources</directory>
                  <includes>
                    <include>module.xml</include>
                  </includes>
                </resource>
              </resources>
            </configuration>
          </execution>
        </executions>
      </plugin>
```

### 3. start wildfly

starting and stopping wildfly happens with [arquillian](https://arquillian.org/)

```java
@RunWith(Arquillian.class)
public class KadaiProducersTest {}

```

the file `src/test/resources/arquillian.xml` contains additional server start settings. change vm settings here for
remote debugging.

the file `src/test/resources/int-test-standalone.xml` conatins the wildfly server config. Here are the datasources
configured, for example.

### 4. deploy ear

create the ear deployment happens inside the testcase

```java
  @Deployment(testable = false)
  public static Archive<?> createDeployment() throws Exception {
    EnterpriseArchive deployment = ShrinkWrap.create(EnterpriseArchive.class, "kadai.ear");

    File[] libs =
        Maven.resolver()
            .loadPomFromFile("pom.xml")
            .importRuntimeAndTestDependencies()
            .resolve()
            .withTransitivity()
            .asFile();
    deployment.addAsLibraries(libs);

    JavaArchive ejbModule = ShrinkWrap.create(JavaArchive.class, "kadai.jar");
    ejbModule.addClasses(KadaiProducers.class, KadaiEjb.class);
    ejbModule.addAsResource("kadai.properties");
    deployment.addAsModule(ejbModule);

    WebArchive webArchive =
        ShrinkWrap.create(WebArchive.class, "kadai.war")
            .addClasses(KadaiCdiTestRestController.class, RestApplication.class)
            .addAsWebInfResource("beans.xml")
            .addAsWebInfResource("int-test-jboss-web.xml", "jboss-web.xml");
    deployment.addAsModule(webArchive);

    deployment.addAsManifestResource("beans.xml");
    return deployment;
  }
```

