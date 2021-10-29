import { Settings } from '../../../settings/models/settings';

export class RetrieveSettings {
  static readonly type = '[Settings] Get settings from backend';
}

export class SetSettings {
  static readonly type = '[Settings] Modify settings according to user input';
  constructor(public settings: Settings) {}
}
