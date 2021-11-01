import { GroupSetting } from './group-setting';

export interface Settings {
  schema: GroupSetting[];
  [setting: string]: any;
}

export enum SettingTypes {
  Text = 'text',
  Interval = 'interval',
  Color = 'color',
  Json = 'json'
}
