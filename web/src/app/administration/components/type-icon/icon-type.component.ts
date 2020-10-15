import { Component, Input } from '@angular/core';
import { ICONTYPES } from 'app/shared/models/icon-types';

@Component({
  selector: 'taskana-administration-icon-type',
  templateUrl: './icon-type.component.html',
  styleUrls: ['./icon-type.component.scss']
})
export class IconTypeComponent {
  @Input()
  type: ICONTYPES = ICONTYPES.ALL;

  @Input()
  selected = false;

  @Input()
  tooltip = false;

  @Input()
  text: string;

  @Input()
  size = 'small';

  public static get allTypes(): Map<string, string> {
    return new Map([
      ['PERSONAL', 'Personal'],
      ['GROUP', 'Group'],
      ['CLEARANCE', 'Clearance'],
      ['TOPIC', 'Topic']
    ]);
  }

  getIconPath(type: string) {
    switch (type) {
      case 'PERSONAL':
        return 'user.svg';
      case 'GROUP':
        return 'users.svg';
      case 'TOPIC':
        return 'topic.svg';
      case 'CLEARANCE':
        return 'clearance.svg';
      default:
        return 'asterisk.svg';
    }
  }
}
