
export class UserInfoModel {
    constructor(
        public userId: string = undefined,
        public groupIds: Array<string> = [],
        public roles: Array<string> = []) { };

}
