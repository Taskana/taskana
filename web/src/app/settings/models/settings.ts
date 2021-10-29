export interface SettingsMember {
  displayName: string;
  key: string;
  type: string;
  min?: number;
  max?: number;
}

export interface GroupSetting {
  displayName: string;
  members: SettingsMember[];
}

export interface Settings {
  schema: GroupSetting[];

  [setting: string]: any;
}

export enum SettingTypes {
  TEXT = 'text',
  INTERVAL = 'interval',
  COLOR = 'color',
  JSON = 'json'
}
