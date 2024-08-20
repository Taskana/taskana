package io.kadai.example.rest.controllers;

import io.kadai.common.internal.util.ResourceUtil;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ResourcesController {

  public static final String KADAI_CUSTOMIZATION_FILE_NAME = "kadai-customization.json";

  @GetMapping(
      value = "/environments/data-sources/kadai-customization.json",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> kadaiCustomization() throws IOException {
    return ResponseEntity.ok(readResourceAsString(KADAI_CUSTOMIZATION_FILE_NAME));
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
