import { Action, NgxsAfterBootstrap, State, StateContext } from '@ngxs/store';
import {
  GetAccessItems,
  GetGroupsByAccessId,
  RemoveAccessItemsPermissions,
  SelectAccessId
} from './access-items-management.actions';
import { Observable, of } from 'rxjs';
import { AccessIdsService } from '../../services/access-ids/access-ids.service';
import { take, tap } from 'rxjs/operators';
import { AccessIdDefinition } from '../../models/access-id';
import { NOTIFICATION_TYPES } from '../../models/notifications';
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
        (groups: AccessIdDefinition[]) => {
          ctx.patchState({
            groups
          });
        },
        (error) => {
          this.requestInProgressService.setRequestInProgress(false);
          this.notificationService.triggerError(NOTIFICATION_TYPES.FETCH_ERR, error);
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
          (error) => {
            this.requestInProgressService.setRequestInProgress(false);
            this.notificationService.triggerError(NOTIFICATION_TYPES.FETCH_ERR_2, error);
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
          this.notificationService.showToast(
            NOTIFICATION_TYPES.SUCCESS_ALERT,
            new Map<string, string>([['accessId', action.accessId]])
          );
        },
        (error) => {
          this.requestInProgressService.setRequestInProgress(false);
          this.notificationService.triggerError(NOTIFICATION_TYPES.DELETE_ERR, error);
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
  selectedAccessId: AccessIdDefinition;
  groups: AccessIdDefinition[];
}
