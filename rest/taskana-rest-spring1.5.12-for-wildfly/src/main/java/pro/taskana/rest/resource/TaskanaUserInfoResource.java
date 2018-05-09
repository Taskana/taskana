package pro.taskana.rest.resource;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.ResourceSupport;

import pro.taskana.TaskanaRole;

/**
 * Resource class for user information.
 */
public class TaskanaUserInfoResource extends ResourceSupport {

    private String userId;
    private List<String> groupIds = new ArrayList<>();
    private List<TaskanaRole> roles = new ArrayList<>();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<String> groupIds) {
        this.groupIds = groupIds;
    }

    public List<TaskanaRole> getRoles() {
        return roles;
    }

    public void setRoles(List<TaskanaRole> roles) {
        this.roles = roles;
    }

}
