import { Component, Input, ViewChild, OnChanges, SimpleChanges, Output, EventEmitter } from '@angular/core';

declare let $: any; // jquery

@Component({
  selector: 'taskana-general-message-modal',
  templateUrl: './general-message-modal.component.html',
  styleUrls: ['./general-message-modal.component.scss']
})
export class GeneralMessageModalComponent implements OnChanges {
  @Input() message: string;
  @Output() messageChange = new EventEmitter<string>();

  @Input()
  title: string;

  @Input()
  type: string;

  @ViewChild('generalModal', { static: true })
  private modal;

  ngOnChanges(changes: SimpleChanges) {
    if (this.message) {
      $(this.modal.nativeElement).modal('toggle');
    }
  }

  removeMessage() {
    this.message = '';
    this.messageChange.emit(this.message);
  }
}
