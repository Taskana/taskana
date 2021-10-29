import { Selector } from '@ngxs/store';
import { SettingsState, SettingsStateModel } from './settings.state';
import { Settings } from '../../../settings/models/settings';

export class SettingsSelectors {
  @Selector([SettingsState])
  static getSettings(state: SettingsStateModel): Settings {
    return state.settings;
  }
}
