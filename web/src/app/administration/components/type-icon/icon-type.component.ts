import { Component, OnInit, Input } from '@angular/core';
import { ICONTYPES } from 'app/models/type';

@Component({
	selector: 'taskana-icon-type',
	templateUrl: './icon-type.component.html',
	styleUrls: ['./icon-type.component.scss']
})
export class IconTypeComponent implements OnInit {


	@Input()
	type: ICONTYPES = ICONTYPES.PERSONAL;

	@Input()
	selected = false;

	@Input()
	tooltip = false;

	public static get allTypes(): Map<string, string> {
		return new Map([['', 'None'], ['PERSONAL', 'Personal'], ['GROUP', 'Group'], ['CLEARANCE', 'Clearance'], ['TOPIC', 'Topic']])
	};

	constructor() { }

	ngOnInit() {

	}

	getIconPath(type: string) {
		return 	type === 'PERSONAL' ? 'user.svg' :
				type === 'GROUP' ? 'users.svg' :
				type === 'TOPIC' ? 'topic.svg' :
				type === 'CLEARANCE' ? 'clearance.svg' :
				type === 'EXTERNAL' ?  'external.svg' :
				type === 'AUTOMATIC' ?  'automatic.svg' :
				type === 'MANUAL' ?  'manual.svg' :
				type === 'CLOSED' ?  'closed.svg' : '';

	}
}
