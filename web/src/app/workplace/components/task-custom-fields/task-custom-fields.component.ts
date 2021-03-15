import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { Task } from 'app/workplace/models/task';
import { takeUntil } from 'rxjs/operators';
import { FormsValidatorService } from '../../../shared/services/forms-validator/forms-validator.service';
import { Subject } from 'rxjs';

@Component({
  selector: 'taskana-task-custom-fields',
  templateUrl: './task-custom-fields.component.html',
  styleUrls: ['./task-custom-fields.component.scss']
})
export class TaskCustomFieldsComponent implements OnInit, OnDestroy {
  @Input() task: Task;
  @Output() taskChange: EventEmitter<Task> = new EventEmitter<Task>();

  readonly lengthError = 'You have reached the maximum length';
  inputOverflowMap = new Map<string, boolean>();
  validateKeypress: Function;
  customFields: string[];

  destroy$ = new Subject<void>();

  constructor(private formsValidatorService: FormsValidatorService) {}

  ngOnInit() {
    this.formsValidatorService.inputOverflowObservable.pipe(takeUntil(this.destroy$)).subscribe((inputOverflowMap) => {
      this.inputOverflowMap = inputOverflowMap;
    });
    this.validateKeypress = (inputFieldModel, maxLength) => {
      this.formsValidatorService.validateInputOverflow(inputFieldModel, maxLength);
    };

    this.customFields = Object.keys(this.task).filter(
      (attribute) => attribute.startsWith('custom') && /\d/.test(attribute)
    );
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
