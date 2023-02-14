package pro.taskana.common.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import pro.taskana.monitor.rest.models.PriorityColumnHeaderRepresentationModel;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.internal.models.ObjectReferenceImpl;

@ControllerAdvice
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class InitBinderControllerAdvice {

  private final ObjectMapper objectMapper;

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
