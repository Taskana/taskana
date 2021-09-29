import { Component, OnDestroy, OnInit } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { Settings, SettingsMember, SettingTypes } from '../../models/settings';
import { Select, Store } from '@ngxs/store';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { SetSettings } from '../../../shared/store/settings-store/settings.actions';
import { SettingsSelectors } from '../../../shared/store/settings-store/settings.selectors';
import { takeUntil } from 'rxjs/operators';
import { validateForm } from './settings.validators';

@Component({
  selector: 'taskana-administration-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit, OnDestroy {
  settingTypes = SettingTypes;
  settings: Settings;
  oldSettings: Settings;
  groups: string[];
  members: string[][] = [];
  invalidMembers: string[] = [];
  destroy$ = new Subject<void>();

  @Select(SettingsSelectors.getSettings) settings$: Observable<Settings>;

  constructor(private store: Store, private notificationService: NotificationService) {}

  ngOnInit() {
    this.settings$.pipe(takeUntil(this.destroy$)).subscribe((settings) => {
      this.settings = this.deepCopy(settings);
      this.oldSettings = this.deepCopy(settings);
      this.getKeysOfSettings();
    });
  }

  deepCopy(settings: Settings): Settings {
    return JSON.parse(JSON.stringify(settings));
  }

  getKeysOfSettings() {
    this.groups = Object.keys(this.settings.schema);
    this.groups.forEach((group) => {
      let groupMembers = Object.keys(this.settings.schema[group].members);
      this.members.push(groupMembers);
      groupMembers.forEach((member) => {
        if (!(member in this.settings)) {
          this.notificationService.showWarning('SETTINGS_INVALID_DATA', { setting: member });
        }
      });
    });
  }

  onSave() {
    this.changeLabelColor('grey');
    this.invalidMembers = validateForm(this.members, this.settings, this.groups);
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

  getMember(group: string, member: string): SettingsMember {
    return this.settings.schema[group].members[member];
  }

  onColorChange(member: string) {
    this.settings[member] = (document.getElementById(member) as HTMLInputElement).value;
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
