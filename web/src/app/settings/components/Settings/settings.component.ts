import { Component, OnDestroy, OnInit } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { Settings, SettingTypes } from '../../models/settings';
import { Select, Store } from '@ngxs/store';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { SetSettings } from '../../../shared/store/settings-store/settings.actions';
import { SettingsSelectors } from '../../../shared/store/settings-store/settings.selectors';
import { takeUntil } from 'rxjs/operators';
import { validateSettings } from './settings.validators';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';

@Component({
  selector: 'taskana-administration-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit, OnDestroy {
  settingTypes = SettingTypes;
  settings: Settings;
  oldSettings: Settings;
  invalidMembers: string[] = [];
  destroy$ = new Subject<void>();

  @Select(SettingsSelectors.getSettings) settings$: Observable<Settings>;

  constructor(
    private store: Store,
    private notificationService: NotificationService,
    private requestInProgressService: RequestInProgressService
  ) {}

  ngOnInit() {
    this.settings$.pipe(takeUntil(this.destroy$)).subscribe((settings) => {
      this.requestInProgressService.setRequestInProgress(false);
      this.settings = this.deepCopy(settings);
      this.oldSettings = this.deepCopy(settings);
    });
  }

  deepCopy(settings: Settings): Settings {
    return JSON.parse(JSON.stringify(settings));
  }

  onSave() {
    this.changeLabelColor('grey');
    this.invalidMembers = validateSettings(this.settings);
    if (this.invalidMembers.length === 0) {
      this.store.dispatch(new SetSettings(this.settings)).subscribe(() => {
        this.notificationService.showSuccess('SETTINGS_SAVE');
      });
    } else {
      this.changeLabelColor('red');
      this.notificationService.showError('SETTINGS_SAVE');
    }
  }

  changeLabelColor(color: string) {
    this.invalidMembers.forEach((member) => {
      const elements = Array.from(document.getElementsByClassName(member));
      elements.forEach((element) => {
        (element as HTMLElement).style.color = color;
      });
    });
  }

  onReset() {
    this.changeLabelColor('grey');
    this.settings = this.deepCopy(this.oldSettings);
  }

  onColorChange(key: string) {
    this.settings[key] = (document.getElementById(key) as HTMLInputElement).value;
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
