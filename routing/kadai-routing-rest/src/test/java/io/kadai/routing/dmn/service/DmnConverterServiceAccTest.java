package io.kadai.routing.dmn.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.kadai.KadaiConfiguration;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.KadaiEngine.ConnectionManagementMode;
import io.kadai.common.api.exceptions.KadaiRuntimeException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.test.config.DataSourceGenerator;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.sampledata.SampleDataGenerator;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.sql.DataSource;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.Rule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(JaasExtension.class)
class DmnConverterServiceAccTest {

  private static final String EXCEL_NAME = "testExcelRouting.xlsx";
  private static final String EXCEL_NAME_INVALID_OUTPUTS =
      "testExcelRoutingWithInvalidOutputs.xlsx";
  private static KadaiEngine kadaiEngine;

  @BeforeAll
  protected static void setupTest() throws Exception {
    resetDb(false);
  }

  protected static void resetDb(boolean dropTables) throws Exception {

    DataSource dataSource = DataSourceGenerator.createDataSourceForH2();
    String schemaName = "KADAI";
    KadaiConfiguration kadaiConfiguration =
        new KadaiConfiguration.Builder(dataSource, false, schemaName)
            .initKadaiProperties()
            .germanPublicHolidaysEnabled(true)
            .build();
    SampleDataGenerator sampleDataGenerator =
        new SampleDataGenerator(dataSource, kadaiConfiguration.getSchemaName());
    if (dropTables) {
      sampleDataGenerator.dropDb();
    }
    kadaiEngine =
        KadaiEngine.buildKadaiEngine(kadaiConfiguration, ConnectionManagementMode.AUTOCOMMIT);

    sampleDataGenerator.clearDb();
    sampleDataGenerator.generateTestData();
  }

  @Test
  @WithAccessId(user = "businessadmin")
  void should_ConvertExcelToDmn() throws Exception {

    File excelRoutingFile = new ClassPathResource(EXCEL_NAME).getFile();
    InputStream targetStream = new FileInputStream(excelRoutingFile);

    MultipartFile routingMultiPartFile = new MockMultipartFile(EXCEL_NAME, targetStream);

    DmnConverterService dmnConverterService = new DmnConverterService(kadaiEngine);
    dmnConverterService.setDmnUploadPath("target\\routing.dmn");
    DmnModelInstance dmnModelInstance = dmnConverterService.convertExcelToDmn(routingMultiPartFile);

    assertThat(dmnModelInstance.getModelElementsByType(Rule.class)).hasSize(3);
  }

  @Test
  @WithAccessId(user = "businessadmin")
  void should_ThrowException_When_ProvidingInvalidOutputKeyDomains() throws Exception {

    File excelRoutingFile = new ClassPathResource(EXCEL_NAME_INVALID_OUTPUTS).getFile();
    InputStream targetStream = new FileInputStream(excelRoutingFile);

    MultipartFile routingMultiPartFile =
        new MockMultipartFile(EXCEL_NAME_INVALID_OUTPUTS, targetStream);

    DmnConverterService dmnConverterService = new DmnConverterService(kadaiEngine);

    ThrowingCallable call = () -> dmnConverterService.convertExcelToDmn(routingMultiPartFile);
    assertThatThrownBy(call)
        .extracting(KadaiRuntimeException.class::cast)
        .extracting(KadaiRuntimeException::getMessage)
        .isEqualTo(
            "Unknown workbasket Key/Domain pairs defined in DMN Table: "
                + "[KeyDomain [key=GPK_KSC1, domain=DOMAIN_A], "
                + "KeyDomain [key=GPK_KSC, domain=DOMAIN_XZ]]");
  }

  @Test
  @WithAccessId(user = "user-1-1")
  void should_ThrowException_When_NotAuthorized() throws Exception {

    File excelRoutingFile = new ClassPathResource(EXCEL_NAME).getFile();
    InputStream targetStream = new FileInputStream(excelRoutingFile);

    MultipartFile routingMultiPartFile = new MockMultipartFile(EXCEL_NAME, targetStream);

    DmnConverterService dmnConverterService = new DmnConverterService(kadaiEngine);

    ThrowingCallable call = () -> dmnConverterService.convertExcelToDmn(routingMultiPartFile);
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }
}
