package pro.taskana.example.rest.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
    try (InputStream fileStream = getClass().getResourceAsStream(resource)) {
      if (fileStream == null) {
        return "{}";
      }
      try (Reader inputStreamReader = new InputStreamReader(fileStream);
          BufferedReader reader = new BufferedReader(inputStreamReader)) {
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
      }
    }
  }
}
