export class WorkbasketAuthorization {
    id: string;
    workbasketId: string;
    userId: string;
    groupId: string;
    read: boolean;
    open: boolean;
    append: boolean;
    transfer: boolean;
    distribute: boolean;

    constructor(id: string,
        workbasketId: string,
        userId: string,
        groupId: string,
        read: boolean,
        open: boolean,
        append: boolean,
        transfer: boolean,
        distribute: boolean) { }
}