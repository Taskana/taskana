import { Component, OnInit, ViewChild } from '@angular/core';
import { RemoveConfirmationService } from 'app/services/remove-confirmation/remove-confirmation.service';

declare let $: any;

@Component({
  selector: 'taskana-remove-confirmation',
  templateUrl: './remove-confirmation.component.html',
  styleUrls: ['./remove-confirmation.component.scss']
})
export class RemoveConfirmationComponent implements OnInit {
  private confirmationCallback: Function;
  message: string;

  @ViewChild('removeConfirmationModal', { static: true })
  private modal;

  constructor(private removeConfirmationService: RemoveConfirmationService) { }

  ngOnInit() {
    this.removeConfirmationService.getRemoveConfirmation().subscribe(({ callback, message }) => {
      this.confirmationCallback = callback;
      this.message = message;
      $(this.modal.nativeElement).modal('toggle');
    });
  }

  confirmAction() {
    this.confirmationCallback();
  }
}
