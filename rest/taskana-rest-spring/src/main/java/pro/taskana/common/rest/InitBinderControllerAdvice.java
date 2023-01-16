package pro.taskana.common.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import pro.taskana.monitor.rest.models.PriorityColumnHeaderRepresentationModel;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.internal.models.ObjectReferenceImpl;

@ControllerAdvice
public class InitBinderControllerAdvice {

  private final ObjectMapper objectMapper;

  @Autowired
  public InitBinderControllerAdvice(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    binder.registerCustomEditor(
        PriorityColumnHeaderRepresentationModel.class,
        new JsonPropertyEditor(objectMapper, PriorityColumnHeaderRepresentationModel.class));
    binder.registerCustomEditor(
        ObjectReference.class, new JsonPropertyEditor(objectMapper, ObjectReferenceImpl.class));
    binder.registerCustomEditor(String[].class, new StringArrayPropertyEditor(null));
  }
}
