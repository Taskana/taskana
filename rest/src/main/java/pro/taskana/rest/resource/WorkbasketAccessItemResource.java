package pro.taskana.rest.resource;

import javax.validation.constraints.NotNull;

import org.springframework.hateoas.ResourceSupport;

public class WorkbasketAccessItemResource extends ResourceSupport {

    public String id;

    @NotNull
    public String workbasketId;

    @NotNull
    public String accessId;

    public boolean permRead;
    public boolean permOpen;
    public boolean permAppend;
    public boolean permTransfer;
    public boolean permDistribute;
    public boolean permCustom1;
    public boolean permCustom2;
    public boolean permCustom3;
    public boolean permCustom4;
    public boolean permCustom5;
    public boolean permCustom6;
    public boolean permCustom7;
    public boolean permCustom8;
    public boolean permCustom9;
    public boolean permCustom10;
    public boolean permCustom11;
    public boolean permCustom12;

    public WorkbasketAccessItemResource(String id, String workbasketId, String accessId, boolean permRead,
        boolean permOpen, boolean permAppend, boolean permTransfer, boolean permDistribute, boolean permCustom1,
        boolean permCustom2, boolean permCustom3, boolean permCustom4, boolean permCustom5, boolean permCustom6,
        boolean permCustom7, boolean permCustom8, boolean permCustom9, boolean permCustom10, boolean permCustom11,
        boolean permCustom12) {
        super();
        this.id = id;
        this.workbasketId = workbasketId;
        this.accessId = accessId;
        this.permRead = permRead;
        this.permOpen = permOpen;
        this.permAppend = permAppend;
        this.permTransfer = permTransfer;
        this.permDistribute = permDistribute;
        this.permCustom1 = permCustom1;
        this.permCustom2 = permCustom2;
        this.permCustom3 = permCustom3;
        this.permCustom4 = permCustom4;
        this.permCustom5 = permCustom5;
        this.permCustom6 = permCustom6;
        this.permCustom7 = permCustom7;
        this.permCustom8 = permCustom8;
        this.permCustom9 = permCustom9;
        this.permCustom10 = permCustom10;
        this.permCustom11 = permCustom11;
        this.permCustom12 = permCustom12;
    }
}
