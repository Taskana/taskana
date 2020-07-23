import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { Task } from 'app/workplace/models/task';
import { takeUntil } from 'rxjs/operators';
import { FormsValidatorService } from '../../../shared/services/forms-validator/forms-validator.service';
import { Subject } from 'rxjs';

@Component({
  selector: 'taskana-task-details-custom-fields',
  templateUrl: './custom-fields.component.html'
})
export class TaskdetailsCustomFieldsComponent implements OnInit, OnDestroy {
  @Input() task: Task;
  @Output() taskChange: EventEmitter<Task> = new EventEmitter<Task>();

  readonly lengthError = 'You have reached the maximum length';
  inputOverflowMap = new Map<string, boolean>();
  validateKeypress: Function;

  destroy$ = new Subject<void>();

  constructor(private formsValidatorService: FormsValidatorService) {}

  ngOnInit() {
    this.formsValidatorService.inputOverflowObservable.pipe(takeUntil(this.destroy$)).subscribe((inputOverflowMap) => {
      this.inputOverflowMap = inputOverflowMap;
    });
    this.validateKeypress = (inputFieldModel, maxLength) => {
      this.formsValidatorService.validateInputOverflow(inputFieldModel, maxLength);
    };
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
