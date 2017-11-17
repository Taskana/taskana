package pro.taskana.rest.model;

import java.util.List;

public class WorkbasketCounterDataDto {
    private List<Integer> data;
    private String label;
    public List<Integer> getData() {
        return data;
    }
    public void setData(List<Integer> data) {
        this.data = data;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
}
