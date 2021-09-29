export interface SettingsMember {
  displayName: string;
  type: string;
  min?: number;
  max?: number;
}

export interface Settings {
  [setting: string]: any;
  schema: {
    [parameterGroup: string]: {
      displayName: string;
      members: {
        [memberName: string]: SettingsMember;
      };
    };
  };
}

export enum SettingTypes {
  TEXT = 'text',
  INTERVAL = 'interval',
  COLOR = 'color',
  JSON = 'json'
}
