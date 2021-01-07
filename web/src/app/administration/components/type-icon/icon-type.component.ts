import { Component, Input } from '@angular/core';
import { WorkbasketType } from 'app/shared/models/workbasket-type';

@Component({
  selector: 'taskana-administration-icon-type',
  templateUrl: './icon-type.component.html',
  styleUrls: ['./icon-type.component.scss']
})
export class IconTypeComponent {
  @Input()
  type: WorkbasketType;

  @Input()
  selected = false;

  @Input()
  tooltip = false;

  @Input()
  text: string;

  @Input()
  size = 'small';

  getIconPath(type: WorkbasketType) {
    switch (type) {
      case WorkbasketType.PERSONAL:
        return 'user.svg';
      case WorkbasketType.GROUP:
        return 'users.svg';
      case WorkbasketType.TOPIC:
        return 'topic.svg';
      case WorkbasketType.CLEARANCE:
        return 'clearance.svg';
      default:
        return 'asterisk.svg';
    }
  }
}
