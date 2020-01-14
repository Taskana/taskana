import { Component, OnInit, Input } from '@angular/core';
import { ICONTYPES } from 'app/models/type';

@Component({
  selector: 'taskana-icon-type',
  templateUrl: './icon-type.component.html',
  styleUrls: ['./icon-type.component.scss']
})
export class IconTypeComponent implements OnInit {
  @Input()
  type: ICONTYPES = ICONTYPES.ALL;

  @Input()
  selected = false;

  @Input()
  tooltip = false;

  @Input()
  text: string;

  public static get allTypes(): Map<string, string> {
    return new Map([['PERSONAL', 'Personal'], ['GROUP', 'Group'], ['CLEARANCE', 'Clearance'], ['TOPIC', 'Topic']]);
  }

  ngOnInit() {

  }

  getIconPath(type: string) {
    switch (type) {
      case 'PERSONAL': return 'user.svg';
      case 'GROUP': return 'users.svg';
      case 'TOPIC': return 'topic.svg';
      case 'CLEARANCE': return 'clearance.svg';
      default: return 'asterisk.svg';
    }
  }
}
