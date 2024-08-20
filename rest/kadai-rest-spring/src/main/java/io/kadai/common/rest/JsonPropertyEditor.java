package io.kadai.common.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.beans.PropertyEditorSupport;
import java.net.URLDecoder;

public class JsonPropertyEditor extends PropertyEditorSupport {

  private final ObjectMapper objectMapper;
  private final Class<?> requiredType;

  public JsonPropertyEditor(ObjectMapper objectMapper, Class<?> requiredType) {
    this.objectMapper = objectMapper;
    this.requiredType = requiredType;
  }

  @Override
  public void setAsText(String text) throws IllegalArgumentException {
    if (text != null && !text.isEmpty()) {
      try {
        setValue(objectMapper.readValue(URLDecoder.decode(text, "UTF-8"), requiredType));
      } catch (Exception e) {
        throw new IllegalArgumentException(e);
      }
    }
  }
}
