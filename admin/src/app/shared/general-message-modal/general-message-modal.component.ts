import { Component, OnInit, Input, ViewChild, OnChanges, SimpleChanges } from '@angular/core';
declare var $: any;

@Component({
	selector: 'taskana-general-message-modal',
	templateUrl: './general-message-modal.component.html',
	styleUrls: ['./general-message-modal.component.scss']
})
export class GeneralMessageModalComponent implements OnChanges {

	@Input()
	message: string = '';

	@Input()
	title: string = '';

	@Input()
	error: boolean = false;

	@ViewChild('generalModal')
	private modal;

	constructor() { }

	ngOnChanges(changes: SimpleChanges) {
		if (this.message) {
			$(this.modal.nativeElement).modal('toggle');
		}
	}

	removeMessage() {
		this.message = undefined;
	}

}
