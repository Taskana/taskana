package pro.taskana;

/**
 * This enum contains all roles that are known to taskana.
 */
public enum TaskanaRole {
    USER("taskana.roles.user"),
    BUSINESS_ADMIN("taskana.roles.businessadmin"),
    ADMIN("taskana.roles.admin");

    private final String propertyName;

    TaskanaRole(String propertyName) {
        this.propertyName = propertyName;
    }

    public static TaskanaRole fromPropertyName(String name) {
        if (USER.propertyName.equalsIgnoreCase(name)) {
            return TaskanaRole.USER;
        } else if (BUSINESS_ADMIN.propertyName.equalsIgnoreCase(name)) {
            return TaskanaRole.BUSINESS_ADMIN;
        } else if (ADMIN.propertyName.equalsIgnoreCase(name)) {
            return TaskanaRole.ADMIN;
        } else {
            return null;
        }
    }

    public String getPropertyName() {
        return propertyName;
    }
}
