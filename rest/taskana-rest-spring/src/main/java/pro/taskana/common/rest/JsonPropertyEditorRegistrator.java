package pro.taskana.common.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import pro.taskana.monitor.rest.models.PriorityColumnHeaderRepresentationModel;

@ControllerAdvice
public class JsonPropertyEditorRegistrator {

  private final ObjectMapper objectMapper;

  @Autowired
  public JsonPropertyEditorRegistrator(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    binder.registerCustomEditor(
        PriorityColumnHeaderRepresentationModel.class,
        new JsonPropertyEditor(objectMapper, PriorityColumnHeaderRepresentationModel.class));
  }
}
