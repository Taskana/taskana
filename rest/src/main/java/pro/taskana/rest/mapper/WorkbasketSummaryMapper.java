package pro.taskana.rest.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.stereotype.Component;
import pro.taskana.WorkbasketSummary;
import pro.taskana.rest.dto.WorkbasketSummaryDto;

@Component
public class WorkbasketSummaryMapper extends ResourceSupport {

    @Autowired
    private ModelMapper modelMapper;

    public WorkbasketSummaryDto convertToDto(WorkbasketSummary workbasketSummary){
        WorkbasketSummaryDto dto = modelMapper.map(workbasketSummary, WorkbasketSummaryDto.class);
        dto.setWorkBasketSummaryId(workbasketSummary.getId());
        return dto;
    }
}
