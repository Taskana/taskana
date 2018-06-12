package pro.taskana.impl;

/**
 * An item that contains information of a selected item of a Report. It is used to get the task ids of the selected item
 * of the Report.
 */
public class SelectedItem {

    private String key;
    private String subKey;
    private int upperAgeLimit;
    private int lowerAgeLimit;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSubKey() {
        return subKey;
    }

    public void setSubKey(String subKey) {
        this.subKey = subKey;
    }

    public int getUpperAgeLimit() {
        return upperAgeLimit;
    }

    public void setUpperAgeLimit(int upperAgeLimit) {
        this.upperAgeLimit = upperAgeLimit;
    }

    public int getLowerAgeLimit() {
        return lowerAgeLimit;
    }

    public void setLowerAgeLimit(int lowerAgeLimit) {
        this.lowerAgeLimit = lowerAgeLimit;
    }

    @Override
    public String toString() {
        return "Key: " + this.key + ", SubKey: " + this.subKey + ", Limits: (" + this.lowerAgeLimit + ","
            + this.getUpperAgeLimit() + ")";
    }

}
