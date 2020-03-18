package pro.taskana.rest.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.hateoas.ResourceSupport;

/** Resource class for {@link TaskCommentResource} with Pagination. */
public class TaskCommentListResource extends ResourceSupport {

  private List<TaskCommentResource> content;

  public TaskCommentListResource() {
    super();
  }

  public TaskCommentListResource(List<TaskCommentResource> taskCommentResources) {
    this.content = taskCommentResources;
  }

  @JsonProperty("task comments")
  public List<TaskCommentResource> getContent() {
    return content;
  }
}
