import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { WorkbasketType } from 'app/shared/models/workbasket-type';

@Component({
  selector: 'taskana-administration-icon-type',
  templateUrl: './icon-type.component.html',
  styleUrls: ['./icon-type.component.scss']
})
export class IconTypeComponent implements OnInit, OnChanges {
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

  iconSize: string;
  iconColor: string;

  ngOnInit() {
    this.iconSize = this.size === 'large' ? '24' : '16';
  }

  ngOnChanges(changes: SimpleChanges) {
    this.iconColor = changes['selected']?.currentValue ? 'white' : '#555';
  }

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
