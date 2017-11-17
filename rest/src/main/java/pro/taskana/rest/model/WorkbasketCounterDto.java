package pro.taskana.rest.model;

import java.util.List;

public class WorkbasketCounterDto {

    private List<String> dates;
    private List<WorkbasketCounterDataDto> data;

    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    public List<WorkbasketCounterDataDto> getData() {
        return data;
    }

    public void setData(List<WorkbasketCounterDataDto> data) {
        this.data = data;
    }
}
