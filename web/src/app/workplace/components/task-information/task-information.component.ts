import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild,
  SimpleChanges,
  OnChanges,
  OnDestroy
} from '@angular/core';
import { Task } from 'app/workplace/models/task';
import { FormsValidatorService } from 'app/shared/services/forms-validator/forms-validator.service';
import { NgForm } from '@angular/forms';
import { Select } from '@ngxs/store';
import { Observable, Subject } from 'rxjs';
import { EngineConfigurationSelectors } from 'app/shared/store/engine-configuration-store/engine-configuration.selectors';
import { ClassificationsService } from '../../../shared/services/classifications/classifications.service';
import { Classification } from '../../../shared/models/classification';
import { TasksCustomisation } from '../../../shared/models/customisation';
import { takeUntil } from 'rxjs/operators';
import { AccessId } from '../../../shared/models/access-id';

@Component({
  selector: 'kadai-task-information',
  templateUrl: './task-information.component.html',
  styleUrls: ['./task-information.component.scss']
})
export class TaskInformationComponent implements OnInit, OnChanges, OnDestroy {
  @Input()
  task: Task;

  @Output() taskChange: EventEmitter<Task> = new EventEmitter<Task>();

  @Input()
  saveToggleTriggered: boolean;

  @Output() formValid: EventEmitter<boolean> = new EventEmitter<boolean>();

  @ViewChild('TaskForm')
  taskForm: NgForm;

  toggleValidationMap = new Map<string, boolean>();
  requestInProgress = false;
  classifications: Classification[];
  isClassificationEmpty: boolean;
  isOwnerValid: boolean = true;

  readonly lengthError = 'You have reached the maximum length';
  inputOverflowMap = new Map<string, boolean>();
  validateInputOverflow: Function;

  @Select(EngineConfigurationSelectors.tasksCustomisation) tasksCustomisation$: Observable<TasksCustomisation>;
  private destroy$ = new Subject<void>();

  constructor(
    private classificationService: ClassificationsService,
    private formsValidatorService: FormsValidatorService
  ) {}

  ngOnInit() {
    this.getClassificationByDomain();
    this.formsValidatorService.inputOverflowObservable.pipe(takeUntil(this.destroy$)).subscribe((inputOverflowMap) => {
      this.inputOverflowMap = inputOverflowMap;
    });
    this.validateInputOverflow = (inputFieldModel, maxLength) => {
      this.formsValidatorService.validateInputOverflow(inputFieldModel, maxLength);
    };
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (
      changes.saveToggleTriggered &&
      changes.saveToggleTriggered.currentValue !== changes.saveToggleTriggered.previousValue
    ) {
      this.validate();
    }
  }

  isFieldValid(field: string): boolean {
    return this.formsValidatorService.isFieldValid(this.taskForm, field);
  }

  updateDate($event) {
    const newDate = $event.value;
    if (typeof newDate !== 'undefined' && newDate !== null) {
      const newDateISOString = newDate.toISOString();
      const currentDate = new Date(this.task.due);
      if (typeof currentDate === 'undefined' || currentDate !== newDate) {
        this.task.due = newDateISOString;
      }
    }
  }

  changedClassification(itemSelected: Classification) {
    this.task.classificationSummary = itemSelected;
    this.isClassificationEmpty = false;
  }

  private validate() {
    this.isClassificationEmpty = typeof this.task.classificationSummary === 'undefined';
    this.formsValidatorService.formSubmitAttempt = true;
    this.formsValidatorService.validateFormInformation(this.taskForm, this.toggleValidationMap).then((value) => {
      if (value && !this.isClassificationEmpty && this.isOwnerValid) {
        this.formValid.emit(true);
      }
    });
  }

  // TODO: this is currently called for every selected task and is only necessary when we switch the workbasket -> can be optimized.
  private getClassificationByDomain() {
    this.requestInProgress = true;
    this.classificationService
      .getClassifications({ domain: [this.task.workbasketSummary.domain] })
      .pipe(takeUntil(this.destroy$))
      .subscribe((classificationPagingList) => {
        this.classifications = classificationPagingList.classifications;
        this.requestInProgress = false;
      });
  }

  onSelectedOwner(owner: AccessId) {
    if (owner?.accessId) {
      this.task.owner = owner.accessId;
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
