package io.kadai.common.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kadai.monitor.rest.models.PriorityColumnHeaderRepresentationModel;
import io.kadai.task.api.models.ObjectReference;
import io.kadai.task.internal.models.ObjectReferenceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice
public class InitBinderControllerAdvice {

  private final ObjectMapper objectMapper;

  @Autowired
  public InitBinderControllerAdvice(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @InitBinder
  @SuppressWarnings("ConstantConditions")
  public void initBinder(WebDataBinder binder) {
    binder.registerCustomEditor(
        PriorityColumnHeaderRepresentationModel.class,
        new JsonPropertyEditor(objectMapper, PriorityColumnHeaderRepresentationModel.class));
    binder.registerCustomEditor(
        ObjectReference.class, new JsonPropertyEditor(objectMapper, ObjectReferenceImpl.class));

    // @see https://stackoverflow.com/questions/75133732/spring-boot-rest-controller-array-handling
    binder.registerCustomEditor(String[].class, new StringArrayPropertyEditor(null));
  }
}
