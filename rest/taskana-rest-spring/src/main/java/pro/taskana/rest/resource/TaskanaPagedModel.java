package pro.taskana.rest.resource;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.RepresentationModel;

/**
 * Optional Paging model for RepresentationModels.
 *
 * @param <T> The class of the paginated content
 */
public class TaskanaPagedModel<T extends RepresentationModel<T>>
    extends RepresentationModel<TaskanaPagedModel<T>> {

  @JsonIgnore
  private TaskanaPagedModelKeys key;
  @JsonIgnore
  private Collection<? extends T> content;

  @JsonProperty(value = "page", access = Access.WRITE_ONLY)
  private PageMetadata metadata;

  @SuppressWarnings("unused") // needed for jackson
  private TaskanaPagedModel() {
  }

  /**
   * Creates a new {@link TaskanaPagedModel} from the given content.
   *
   * @param property property which will be used for serialization.
   * @param content  must not be {@literal null}.
   * @param metadata the metadata. Can be null. If null, no metadata will be serialized.
   */
  public TaskanaPagedModel(
      TaskanaPagedModelKeys property, Collection<? extends T> content, PageMetadata metadata) {
    this.content = content;
    this.metadata = metadata;
    this.key = property;
  }

  public Collection<T> getContent() {
    return Collections.unmodifiableCollection(content);
  }

  public PageMetadata getMetadata() {
    return metadata;
  }

  @JsonAnySetter
  private void deserialize(String propertyName, Collection<T> content) {
    TaskanaPagedModelKeys.getEnumFromPropertyName(propertyName)
        .ifPresent(
            pagedModelKey -> {
              this.key = pagedModelKey;
              this.content = content;
            });
  }

  @JsonAnyGetter
  private Map<String, Object> serialize() {
    HashMap<String, Object> jsonMap = new HashMap<>();
    if (metadata != null) {
      jsonMap.put("page", metadata);
    }
    jsonMap.put(key.getPropertyName(), content);
    return jsonMap;
  }
}
