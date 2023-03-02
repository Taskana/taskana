package pro.taskana.common.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.beans.PropertyEditorSupport;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JsonPropertyEditor extends PropertyEditorSupport {

  private final ObjectMapper objectMapper;
  private final Class<?> requiredType;

  @Override
  public void setAsText(String text) throws IllegalArgumentException {
    if (text != null && !text.isEmpty()) {
      try {
        setValue(
            objectMapper.readValue(URLDecoder.decode(text, StandardCharsets.UTF_8), requiredType));
      } catch (Exception e) {
        throw new IllegalArgumentException(e);
      }
    }
  }
}
