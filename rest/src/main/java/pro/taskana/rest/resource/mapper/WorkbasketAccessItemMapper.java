package pro.taskana.rest.resource.mapper;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketService;
import pro.taskana.impl.WorkbasketAccessItemImpl;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.WorkbasketAccessItemResource;

public class WorkbasketAccessItemMapper {

    @Autowired
    private WorkbasketService workbasketService;

    public WorkbasketAccessItemResource toResource(WorkbasketAccessItem wbAccItem) {
        WorkbasketAccessItemResource resource = new WorkbasketAccessItemResource(wbAccItem.getId(),
            wbAccItem.getWorkbasketId(),
            wbAccItem.getAccessId(), wbAccItem.isPermRead(), wbAccItem.isPermOpen(), wbAccItem.isPermAppend(),
            wbAccItem.isPermTransfer(),
            wbAccItem.isPermDistribute(), wbAccItem.isPermCustom1(), wbAccItem.isPermCustom2(),
            wbAccItem.isPermCustom3(), wbAccItem.isPermCustom4(),
            wbAccItem.isPermCustom5(), wbAccItem.isPermCustom6(), wbAccItem.isPermCustom7(), wbAccItem.isPermCustom8(),
            wbAccItem.isPermCustom9(),
            wbAccItem.isPermCustom10(), wbAccItem.isPermCustom11(), wbAccItem.isPermCustom12());

        // Add self-decription link to hateoas
        resource.add(
            linkTo(methodOn(WorkbasketController.class).getWorkbasketAuthorizations(wbAccItem.getWorkbasketId()))
                .withSelfRel());
        return resource;
    }

    public WorkbasketAccessItem toModel(WorkbasketAccessItemResource wbAccItemRecource) {
        WorkbasketAccessItemImpl wbAccItemModel = (WorkbasketAccessItemImpl) workbasketService
            .newWorkbasketAccessItem(wbAccItemRecource.workbasketId, wbAccItemRecource.accessId);
        wbAccItemModel.setId(wbAccItemRecource.accessItemId);
        wbAccItemModel.setPermRead(wbAccItemRecource.permRead);
        wbAccItemModel.setPermOpen(wbAccItemRecource.permOpen);
        wbAccItemModel.setPermAppend(wbAccItemRecource.permAppend);
        wbAccItemModel.setPermTransfer(wbAccItemRecource.permTransfer);
        wbAccItemModel.setPermDistribute(wbAccItemRecource.permDistribute);
        wbAccItemModel.setPermCustom1(wbAccItemRecource.permCustom1);
        wbAccItemModel.setPermCustom2(wbAccItemRecource.permCustom2);
        wbAccItemModel.setPermCustom3(wbAccItemRecource.permCustom3);
        wbAccItemModel.setPermCustom4(wbAccItemRecource.permCustom4);
        wbAccItemModel.setPermCustom5(wbAccItemRecource.permCustom5);
        wbAccItemModel.setPermCustom6(wbAccItemRecource.permCustom6);
        wbAccItemModel.setPermCustom7(wbAccItemRecource.permCustom7);
        wbAccItemModel.setPermCustom8(wbAccItemRecource.permCustom8);
        wbAccItemModel.setPermCustom9(wbAccItemRecource.permCustom9);
        wbAccItemModel.setPermCustom10(wbAccItemRecource.permCustom10);
        wbAccItemModel.setPermCustom11(wbAccItemRecource.permCustom11);
        wbAccItemModel.setPermCustom12(wbAccItemRecource.permCustom12);
        return wbAccItemModel;
    }
}
