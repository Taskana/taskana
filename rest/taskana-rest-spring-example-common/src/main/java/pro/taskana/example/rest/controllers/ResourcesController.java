package pro.taskana.example.rest.controllers;

import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import pro.taskana.common.internal.util.ResourceUtil;

@Controller
public class ResourcesController {

  public static final String TASKANA_CUSTOMIZATION_FILE_NAME = "taskana-customization.json";

  @GetMapping(
      value = "/environments/data-sources/taskana-customization.json",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> taskanaCustomization() throws IOException {
    return ResponseEntity.ok(readResourceAsString(TASKANA_CUSTOMIZATION_FILE_NAME));
  }

  // the environment-information.json file will be served via "static" folder
  //  @GetMapping(
  //      value = "/environments/data-sources/environment-information.json",
  //      produces = MediaType.APPLICATION_JSON_VALUE)
  //  public ResponseEntity<String> environmentInformation() throws Exception {
  //    return ResponseEntity.ok(readResourceAsString("environment-information.json"));
  //  }

  private String readResourceAsString(String resource) throws IOException {
    String resourceAsString = ResourceUtil.readResourceAsString(getClass(), resource);
    if (resourceAsString == null) {
      return "{}";
    }
    return resourceAsString;
  }
}
