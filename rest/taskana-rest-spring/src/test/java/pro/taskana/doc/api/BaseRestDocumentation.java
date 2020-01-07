package pro.taskana.doc.api;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import pro.taskana.RestHelper;
import pro.taskana.TaskanaSpringBootTest;

/** Base class for Rest Documentation tests. */
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@TaskanaSpringBootTest
public abstract class BaseRestDocumentation {

  @LocalServerPort protected int port;

  @Autowired protected WebApplicationContext context;

  @Autowired protected MockMvc mockMvc;

  @Autowired protected RestHelper restHelper;

  @BeforeEach
  public void setUpMockMvc() {
    document("{methodName}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()));
  }
}
