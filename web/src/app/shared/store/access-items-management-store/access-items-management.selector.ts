import { Selector } from '@ngxs/store';
import { AccessItemsManagementState, AccessItemsManagementStateModel } from './access-items-management.state';
import { AccessId } from '../../models/access-id';

export class AccessItemsManagementSelector {
  @Selector([AccessItemsManagementState])
  static groups(state: AccessItemsManagementStateModel): AccessId[] {
    return state.groups;
  }

  @Selector([AccessItemsManagementState])
  static permissions(state: AccessItemsManagementStateModel): AccessId[] {
    return state.permissions;
  }
}
