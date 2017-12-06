package pro.taskana.security;

import java.util.List;

/**
 * SamplePrincipal.
 * @author KKL
 */
public class SamplePrincipal implements TaskanaPrincipal {

    private String name;
    private List<String> groups;

    public SamplePrincipal(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getGroupNames() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

}
