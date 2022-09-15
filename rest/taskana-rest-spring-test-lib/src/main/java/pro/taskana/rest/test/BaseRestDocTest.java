package pro.taskana.rest.test;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import capital.scalable.restdocs.AutoDocumentation;
import capital.scalable.restdocs.SnippetRegistry;
import capital.scalable.restdocs.jackson.JacksonResultHandlers;
import capital.scalable.restdocs.response.ResponseModifyingPreprocessors;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Links;
import org.springframework.lang.NonNull;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.cli.CliDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.MockMvcConfigurerAdapter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

@TaskanaSpringBootTest
@ExtendWith(RestDocumentationExtension.class)
public class BaseRestDocTest {

  protected MockMvc mockMvc;
  @Autowired protected ObjectMapper objectMapper;
  @Autowired protected RestHelper restHelper;
  @Autowired private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

  @BeforeEach
  public void setUp(
      WebApplicationContext webApplicationContext,
      RestDocumentationContextProvider restDocumentation) {
    objectMapper = objectMapper.addMixIn(Links.class, MixInIgnoreType.class);
    this.mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(springSecurity())
            .apply(configureAdminHeadersAsDefault())
            .alwaysDo(JacksonResultHandlers.prepareJackson(objectMapper))
            .alwaysDo(commonDocumentation())
            .apply(
                MockMvcRestDocumentation.documentationConfiguration(restDocumentation)
                    .snippets()
                    .withDefaults(
                        CliDocumentation.curlRequest(),
                        HttpDocumentation.httpRequest(),
                        HttpDocumentation.httpResponse(),
                        AutoDocumentation.requestFields().failOnUndocumentedFields(true),
                        AutoDocumentation.responseFields().failOnUndocumentedFields(true),
                        AutoDocumentation.pathParameters().failOnUndocumentedParams(true),
                        AutoDocumentation.requestParameters().failOnUndocumentedParams(true),
                        AutoDocumentation.description(),
                        AutoDocumentation.methodAndPath(),
                        AutoDocumentation.modelAttribute(
                            requestMappingHandlerAdapter.getArgumentResolvers()),
                        AutoDocumentation.sectionBuilder()
                            .snippetNames(
                                SnippetRegistry.AUTO_AUTHORIZATION,
                                SnippetRegistry.AUTO_PATH_PARAMETERS,
                                SnippetRegistry.AUTO_REQUEST_PARAMETERS,
                                SnippetRegistry.AUTO_MODELATTRIBUTE,
                                SnippetRegistry.AUTO_REQUEST_FIELDS,
                                SnippetRegistry.AUTO_RESPONSE_FIELDS,
                                SnippetRegistry.AUTO_LINKS,
                                SnippetRegistry.HTTP_REQUEST,
                                SnippetRegistry.HTTP_RESPONSE)
                            .build()))
            .build();
  }

  protected RestDocumentationResultHandler commonDocumentation(Snippet... snippets) {
    return MockMvcRestDocumentation.document(
        "{ClassName}/{methodName}",
        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
        Preprocessors.preprocessResponse(
            ResponseModifyingPreprocessors.replaceBinaryContent(),
            ResponseModifyingPreprocessors.limitJsonArrayLength(objectMapper),
            Preprocessors.prettyPrint()),
        snippets);
  }

  private MockMvcConfigurerAdapter configureAdminHeadersAsDefault() {
    return new MockMvcConfigurerAdapter() {
      @Override
      public RequestPostProcessor beforeMockMvcCreated(
          @NonNull ConfigurableMockMvcBuilder<?> builder, @NonNull WebApplicationContext cxt) {
        builder.defaultRequest(
            MockMvcRequestBuilders.post("/test")
                .headers(RestHelper.generateHeadersForUser("admin")));
        return super.beforeMockMvcCreated(builder, cxt);
      }
    };
  }

  @JsonIgnoreType
  public static class MixInIgnoreType {}
}
