import { NOTIFICATION_TYPES, notifications } from './notifications';
import { Pair } from './pair';

export class AlertModel {
  public readonly closingDelay = 2500;
  public readonly autoClosing = true;
  public message: string;

  constructor(
    public type: NOTIFICATION_TYPES = NOTIFICATION_TYPES.SUCCESS_ALERT,
    public additions: Map<string, string> = new Map<string, string>()
  ) {
    this.message = notifications.get(type).text;
    if (additions) {
      additions.forEach((value: string, replacementKey: string) => {
        this.message = this.message.replace(`{${replacementKey}}`, value);
      });
    }
  }
}
