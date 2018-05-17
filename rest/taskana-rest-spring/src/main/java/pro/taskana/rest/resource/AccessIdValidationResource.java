package pro.taskana.rest.resource;

/**
 * resource class for access id validation.
 *
 * @author bbr
 */
public class AccessIdValidationResource {

    public String name;
    public String accessId;

    public AccessIdValidationResource() {

    }

    public AccessIdValidationResource(String name, String accessId) {
        this.accessId = accessId;
        this.name = name;
    }

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
