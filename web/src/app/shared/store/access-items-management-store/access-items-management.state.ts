import { Action, NgxsAfterBootstrap, State, StateContext } from '@ngxs/store';
import {
  GetAccessItems,
  GetPermissionsByAccessId,
  GetGroupsByAccessId,
  RemoveAccessItemsPermissions,
  SelectAccessId
} from './access-items-management.actions';
import { Observable, of } from 'rxjs';
import { AccessIdsService } from '../../services/access-ids/access-ids.service';
import { take, tap } from 'rxjs/operators';
import { AccessId } from '../../models/access-id';
import { NotificationService } from '../../services/notifications/notification.service';
import { WorkbasketAccessItemsRepresentation } from '../../models/workbasket-access-items-representation';
import { RequestInProgressService } from '../../services/request-in-progress/request-in-progress.service';
import { Injectable } from '@angular/core';

class InitializeStore {
  static readonly type = '[Access Items Management] Initializing state';
}

@Injectable()
@State<AccessItemsManagementStateModel>({ name: 'accessItemsManagement' })
export class AccessItemsManagementState implements NgxsAfterBootstrap {
  constructor(
    private accessIdsService: AccessIdsService,
    private notificationService: NotificationService,
    private requestInProgressService: RequestInProgressService
  ) {}

  @Action(SelectAccessId)
  selectAccessId(ctx: StateContext<AccessItemsManagementStateModel>, action: SelectAccessId): Observable<any> {
    const selectedAccessId = action.accessIdDefinition;
    ctx.patchState({
      selectedAccessId
    });
    return of(null);
  }

  @Action(GetGroupsByAccessId)
  getGroupsByAccessId(
    ctx: StateContext<AccessItemsManagementStateModel>,
    action: GetGroupsByAccessId
  ): Observable<any> {
    return this.accessIdsService.getGroupsByAccessId(action.accessId).pipe(
      take(1),
      tap(
        (groups: AccessId[]) => {
          ctx.patchState({
            groups
          });
        },
        () => {
          this.requestInProgressService.setRequestInProgress(false);
        }
      )
    );
  }

  @Action(GetPermissionsByAccessId)
  getPermissionsByAccessId(
    ctx: StateContext<AccessItemsManagementStateModel>,
    action: GetPermissionsByAccessId
  ): Observable<any> {
    return this.accessIdsService.getPermissionsByAccessId(action.accessId).pipe(
      take(1),
      tap(
        (permissions: AccessId[]) => {
          ctx.patchState({
            permissions
          });
        },
        () => {
          this.requestInProgressService.setRequestInProgress(false);
        }
      )
    );
  }

  @Action(GetAccessItems)
  getAccessItems(ctx: StateContext<AccessItemsManagementStateModel>, action: GetAccessItems): Observable<any> {
    this.requestInProgressService.setRequestInProgress(true);
    return this.accessIdsService
      .getAccessItems(action.filterParameter, action.sortParameter, action.pagingParameter)
      .pipe(
        take(1),
        tap(
          (accessItemsResource: WorkbasketAccessItemsRepresentation) => {
            this.requestInProgressService.setRequestInProgress(false);
            ctx.patchState({
              accessItemsResource
            });
          },
          () => {
            this.requestInProgressService.setRequestInProgress(false);
          }
        )
      );
  }

  @Action(RemoveAccessItemsPermissions)
  removeAccessItemsPermissions(
    ctx: StateContext<AccessItemsManagementStateModel>,
    action: RemoveAccessItemsPermissions
  ): Observable<any> {
    this.requestInProgressService.setRequestInProgress(true);
    return this.accessIdsService.removeAccessItemsPermissions(action.accessId).pipe(
      take(1),
      tap(
        () => {
          this.requestInProgressService.setRequestInProgress(false);
          this.notificationService.showSuccess('WORKBASKET_ACCESS_ITEM_REMOVE_PERMISSION', {
            accessId: action.accessId
          });
        },
        () => {
          this.requestInProgressService.setRequestInProgress(false);
        }
      )
    );
  }

  ngxsAfterBootstrap(ctx?: StateContext<any>): void {
    ctx.dispatch(new InitializeStore());
  }
}

export interface AccessItemsManagementStateModel {
  accessItemsResource: WorkbasketAccessItemsRepresentation;
  selectedAccessId: AccessId;
  groups: AccessId[];
  permissions: AccessId[];
}
