package pro.taskana.security;

import java.security.Principal;
import java.util.List;

/**
 * This interface extends Principal by groupIds.
 * @author KKL
 */
public interface TaskanaPrincipal extends Principal {

    List<String> getGroupNames();
}
