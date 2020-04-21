import { Component, EventEmitter, OnInit, Output, ViewChild } from '@angular/core';
import { Subscription } from 'rxjs';
import { ErrorModel } from '../../models/error-model';
import { ErrorsService } from '../../services/errors/errors.service';

@Component({
  selector: 'error-modal',
  templateUrl: './error-modal.component.html',
  styleUrls: ['./error-modal.component.scss']
})
export class ErrorModalComponent implements OnInit {
  error: ErrorModel;

  errorsSubscription: Subscription;

  constructor(private errorsService: ErrorsService) {
  }

  ngOnInit(): void {
    this.errorsSubscription = this.errorsService.getError().subscribe((error: ErrorModel) => {
      this.error = error;
    });
  }

  removeMessage() {
    delete this.error;
  }

  ngOnDestroy() {
    if (this.errorsSubscription) {
      this.errorsSubscription.unsubscribe();
    }
  }
}
