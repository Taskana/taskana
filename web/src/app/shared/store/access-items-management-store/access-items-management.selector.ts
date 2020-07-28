import { Selector } from '@ngxs/store';
import { AccessItemsManagementState, AccessItemsManagementStateModel } from './access-items-management.state';
import { AccessIdDefinition } from '../../models/access-id';

export class AccessItemsManagementSelector {
  @Selector([AccessItemsManagementState])
  static groups(state: AccessItemsManagementStateModel): AccessIdDefinition[] {
    return state.groups;
  }
}
