package pro.taskana;

/**
 * Interface for WorkbasketAccessItemExtended. This interface is used to control access of users to workbaskets.
 *
 * @author mmr
 */
public interface WorkbasketAccessItemExtended extends WorkbasketAccessItem {

    /**
     * Returns the Key of the referenced workbasket.
     *
     * @return the workbasket key
     */
    String getWorkbasketKey();
}
