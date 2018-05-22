package pro.taskana;

/**
 * Interface for WorkbasketAccessItem. This interface is used to control access of users to workbaskets.
 *
 * @author bbr
 */
public interface WorkbasketAccessItem {

    /**
     * Returns the current id of the WorkbasketAccessItem.
     *
     * @return Id
     */
    String getId();

    /**
     * Returns the Id of the referenced workbasket.
     *
     * @return the workbasket Id
     */
    String getWorkbasketId();

    /**
     * Returns the group id or user id for which this WorkbasketAccessItem controls access permissions.
     *
     * @return access id, this is the group id or user id
     */
    String getAccessId();

    /**
     * Returns the name of the group or user for which this WorkbasketAccessItem controls access permissions.
     *
     * @return access name, this is the name of the group or user
     */
    String getAccessName();

    /**
     * Set the name of the group or user for which this WorkbasketAccessItem controls access permissions.
     *
     * @param name
     *            the name of the group or user for which this WorkbasketAccessItem controls access permissions.
     */
    void setAccessName(String name);

    /**
     * Returns whether read of the referenced workbasket is permitted.
     *
     * @return read permission for the referenced workbasket
     */
    boolean isPermRead();

    /**
     * Sets read permission for the referenced workbasket.
     *
     * @param permRead
     *            specifies whether read is permitted for the referenced workbasket.
     */
    void setPermRead(boolean permRead);

    /**
     * Returns whether open of the referenced workbasket is permitted.
     *
     * @return open permission for the referenced workbasket
     */
    boolean isPermOpen();

    /**
     * Sets open permission for the referenced workbasket.
     *
     * @param permOpen
     *            specifies whether open is permitted for the referenced workbasket.
     */
    void setPermOpen(boolean permOpen);

    /**
     * Returns whether append to the referenced workbasket is permitted.
     *
     * @return append permission for the referenced workbasket
     */
    boolean isPermAppend();

    /**
     * Sets append permission for the referenced workbasket.
     *
     * @param permAppend
     *            specifies whether append to the referenced workbasket is permitted.
     */
    void setPermAppend(boolean permAppend);

    /**
     * Returns whether transfer from the referenced workbasket is permitted.
     *
     * @return transfer permission for the referenced workbasket
     */
    boolean isPermTransfer();

    /**
     * Sets transfer permission for the referenced workbasket.
     *
     * @param permTransfer
     *            specifies whether transfer from the referenced workbasket is permitted.
     */
    void setPermTransfer(boolean permTransfer);

    /**
     * Returns whether distribute from the referenced workbasket is permitted.
     *
     * @return distribute permission for the referenced workbasket
     */
    boolean isPermDistribute();

    /**
     * Sets distribute permission for the referenced workbasket.
     *
     * @param permDistribute
     *            specifies whether distribute from the referenced workbasket is permitted.
     */
    void setPermDistribute(boolean permDistribute);

    /**
     * Returns whether custom1 permission is granted for the referenced workbasket. The semantics of this custom
     * permission is transparent to taskana.
     *
     * @return custom1 permission for the referenced workbasket
     */
    boolean isPermCustom1();

    /**
     * Sets the custom1 permission for the referenced workbasket. The semantics of this custom permission is transparent
     * to taskana.
     *
     * @param permCustom1
     *            specifies whether custom1 permission is granted
     */
    void setPermCustom1(boolean permCustom1);

    /**
     * Returns whether custom2 permission is granted for the referenced workbasket. The semantics of this custom
     * permission is transparent to taskana.
     *
     * @return custom2 permission for the referenced workbasket
     */
    boolean isPermCustom2();

    /**
     * Sets the custom2 permission for the referenced workbasket. The semantics of this custom permission is transparent
     * to taskana.
     *
     * @param permCustom2
     *            specifies whether custom2 permission is granted
     */
    void setPermCustom2(boolean permCustom2);

    /**
     * Returns whether custom3 permission is granted for the referenced workbasket. The semantics of this custom
     * permission is transparent to taskana.
     *
     * @return custom3 permission for the referenced workbasket
     */
    boolean isPermCustom3();

    /**
     * Sets the custom3 permission for the referenced workbasket. The semantics of this custom permission is transparent
     * to taskana.
     *
     * @param permCustom3
     *            specifies whether custom3 permission is granted
     */
    void setPermCustom3(boolean permCustom3);

    /**
     * Returns whether custom4 permission is granted for the referenced workbasket. The semantics of this custom
     * permission is transparent to taskana.
     *
     * @return custom4 permission for the referenced workbasket
     */
    boolean isPermCustom4();

    /**
     * Sets the custom4 permission for the referenced workbasket. The semantics of this custom permission is transparent
     * to taskana.
     *
     * @param permCustom4
     *            specifies whether custom4 permission is granted
     */
    void setPermCustom4(boolean permCustom4);

    /**
     * Returns whether custom5 permission is granted for the referenced workbasket. The semantics of this custom
     * permission is transparent to taskana.
     *
     * @return custom5 permission for the referenced workbasket
     */
    boolean isPermCustom5();

    /**
     * Sets the custom5 permission for the referenced workbasket. The semantics of this custom permission is transparent
     * to taskana.
     *
     * @param permCustom5
     *            specifies whether custom5 permission is granted
     */
    void setPermCustom5(boolean permCustom5);

    /**
     * Returns whether custom6 permission is granted for the referenced workbasket. The semantics of this custom
     * permission is transparent to taskana.
     *
     * @return custom6 permission for the referenced workbasket
     */
    boolean isPermCustom6();

    /**
     * Sets the custom6 permission for the referenced workbasket. The semantics of this custom permission is transparent
     * to taskana.
     *
     * @param permCustom6
     *            specifies whether custom6 permission is granted
     */
    void setPermCustom6(boolean permCustom6);

    /**
     * Returns whether custom7 permission is granted for the referenced workbasket. The semantics of this custom
     * permission is transparent to taskana.
     *
     * @return custom7 permission for the referenced workbasket
     */
    boolean isPermCustom7();

    /**
     * Sets the custom7 permission for the referenced workbasket. The semantics of this custom permission is transparent
     * to taskana.
     *
     * @param permCustom7
     *            specifies whether custom7 permission is granted
     */
    void setPermCustom7(boolean permCustom7);

    /**
     * Returns whether custom8 permission is granted for the referenced workbasket. The semantics of this custom
     * permission is transparent to taskana.
     *
     * @return custom8 permission for the referenced workbasket
     */
    boolean isPermCustom8();

    /**
     * Sets the custom8 permission for the referenced workbasket. The semantics of this custom permission is transparent
     * to taskana.
     *
     * @param permCustom8
     *            specifies whether custom8 permission is granted
     */
    void setPermCustom8(boolean permCustom8);

    /**
     * Returns whether custom9 permission is granted for the referenced workbasket. The semantics of this custom
     * permission is transparent to taskana.
     *
     * @return custom9 permission for the referenced workbasket
     */
    boolean isPermCustom9();

    /**
     * Sets the custom9 permission for the referenced workbasket. The semantics of this custom permission is transparent
     * to taskana.
     *
     * @param permCustom9
     *            specifies whether custom9 permission is granted
     */
    void setPermCustom9(boolean permCustom9);

    /**
     * Returns whether custom10 permission is granted for the referenced workbasket. The semantics of this custom
     * permission is transparent to taskana.
     *
     * @return custom10 permission for the referenced workbasket
     */
    boolean isPermCustom10();

    /**
     * Sets the custom10 permission for the referenced workbasket. The semantics of this custom permission is
     * transparent to taskana.
     *
     * @param permCustom10
     *            specifies whether custom10 permission is granted
     */
    void setPermCustom10(boolean permCustom10);

    /**
     * Returns whether custom11 permission is granted for the referenced workbasket. The semantics of this custom
     * permission is transparent to taskana.
     *
     * @return custom11 permission for the referenced workbasket
     */
    boolean isPermCustom11();

    /**
     * Sets the custom11 permission for the referenced workbasket. The semantics of this custom permission is
     * transparent to taskana.
     *
     * @param permCustom11
     *            specifies whether custom11 permission is granted
     */
    void setPermCustom11(boolean permCustom11);

    /**
     * Returns whether custom12 permission is granted for the referenced workbasket. The semantics of this custom
     * permission is transparent to taskana.
     *
     * @return custom12 permission for the referenced workbasket
     */
    boolean isPermCustom12();

    /**
     * Sets the custom12 permission for the referenced workbasket. The semantics of this custom permission is
     * transparent to taskana.
     *
     * @param permCustom12
     *            specifies whether custom12 permission is granted
     */
    void setPermCustom12(boolean permCustom12);
}
