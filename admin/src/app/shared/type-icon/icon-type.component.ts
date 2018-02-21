import { Component, OnInit, Input } from '@angular/core';


export enum ICONTYPES {
	NONE = 'NONE',
	PERSONAL = 'PERSONAL',
	GROUP = 'GROUP',
	CLEARANCE = 'CLEARANCE',
	TOPIC = 'TOPIC'
}


@Component({
	selector: 'taskana-icon-type',
	templateUrl: './icon-type.component.html',
	styleUrls: ['./icon-type.component.scss']
})
export class IconTypeComponent implements OnInit {

	public static get allTypes(): Map<string, string> {	return new Map([['NONE', 'None'], ['PERSONAL', 'Personal'], ['GROUP', 'Group'], ['CLEARANCE', 'Clearance'], ['TOPIC', 'Topic']])};
	
	constructor() { }

	@Input()
	type: ICONTYPES = ICONTYPES.PERSONAL;

	@Input()
	selected: boolean = false;

	@Input()
	tooltip: boolean = false;

	ngOnInit() {
	}

}
