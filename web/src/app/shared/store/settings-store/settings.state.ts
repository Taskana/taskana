import { Action, NgxsAfterBootstrap, State, StateContext } from '@ngxs/store';
import { Injectable } from '@angular/core';
import { RetrieveSettings, SetSettings } from './settings.actions';
import { Settings } from '../../../settings/models/settings';
import { SettingsService } from '../../../settings/services/settings-service';
import { take } from 'rxjs/operators';
import { NotificationService } from '../../services/notifications/notification.service';

@Injectable()
@State<SettingsStateModel>({ name: 'settings' })
export class SettingsState implements NgxsAfterBootstrap {
  constructor(private settingsService: SettingsService, private notificationService: NotificationService) {}

  @Action(RetrieveSettings)
  initializeStore(ctx: StateContext<SettingsStateModel>) {
    return this.settingsService
      .getSettings()
      .pipe(take(1))
      .subscribe((settings) => {
        if (!settings.schema) {
          this.notificationService.showError('SETTINGS_NO_SCHEMA');
        } else {
          ctx.patchState({
            settings: settings
          });
        }
      });
  }

  ngxsAfterBootstrap(ctx?: StateContext<any>): void {
    ctx.dispatch(new RetrieveSettings());
  }

  @Action(SetSettings)
  setSettings(ctx: StateContext<SettingsStateModel>, action: SetSettings) {
    return this.settingsService
      .updateSettings(action.settings)
      .pipe(take(1))
      .subscribe(() => {
        ctx.patchState({
          settings: action.settings
        });
      });
  }
}

export interface SettingsStateModel {
  settings: Settings;
}
