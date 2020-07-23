import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild,
  SimpleChanges,
  OnChanges,
  HostListener,
  OnDestroy
} from '@angular/core';
import { Task } from 'app/workplace/models/task';
import { FormsValidatorService } from 'app/shared/services/forms-validator/forms-validator.service';
import { NgForm } from '@angular/forms';
import { DomainService } from 'app/shared/services/domain/domain.service';
import { Select } from '@ngxs/store';
import { Observable, Subject } from 'rxjs';
import { EngineConfigurationSelectors } from 'app/shared/store/engine-configuration-store/engine-configuration.selectors';
import { ClassificationsService } from '../../../shared/services/classifications/classifications.service';
import { Classification } from '../../../shared/models/classification';
import { TasksCustomisation } from '../../../shared/models/customisation';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'taskana-task-details-general-fields',
  templateUrl: './general-fields.component.html',
  styleUrls: ['./general-fields.component.scss']
})
export class TaskdetailsGeneralFieldsComponent implements OnInit, OnChanges, OnDestroy {
  @Input()
  task: Task;

  @Output() taskChange: EventEmitter<Task> = new EventEmitter<Task>();

  @Input()
  saveToggleTriggered: boolean;

  @Output() formValid: EventEmitter<boolean> = new EventEmitter<boolean>();

  @ViewChild('TaskForm', { static: false })
  taskForm: NgForm;

  toggleValidationMap = new Map<string, boolean>();
  requestInProgress = false;
  classifications: Classification[];

  readonly lengthError = 'You have reached the maximum length';
  inputOverflowMap = new Map<string, boolean>();
  validateKeypress: Function;

  @Select(EngineConfigurationSelectors.tasksCustomisation) tasksCustomisation$: Observable<TasksCustomisation>;
  private destroy$ = new Subject<void>();

  constructor(
    private classificationService: ClassificationsService,
    private formsValidatorService: FormsValidatorService,
    private domainService: DomainService
  ) {}

  ngOnInit() {
    this.getClassificationByDomain();
    this.formsValidatorService.inputOverflowObservable.pipe(takeUntil(this.destroy$)).subscribe((inputOverflowMap) => {
      this.inputOverflowMap = inputOverflowMap;
    });
    this.validateKeypress = (inputFieldModel, maxLength) => {
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

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  isFieldValid(field: string): boolean {
    return this.formsValidatorService.isFieldValid(this.taskForm, field);
  }

  updateDate($event) {
    if (new Date(this.task.due).toISOString() !== $event) {
      this.task.due = $event;
    }
  }

  private validate() {
    this.formsValidatorService.formSubmitAttempt = true;
    this.formsValidatorService.validateFormInformation(this.taskForm, this.toggleValidationMap).then((value) => {
      if (value) {
        this.formValid.emit(true);
      }
    });
  }

  changedClassification(itemSelected: Classification) {
    this.task.classificationSummary = itemSelected;
  }

  private async getClassificationByDomain() {
    this.requestInProgress = true;
    this.classifications = (
      await this.classificationService.getClassificationsByDomain(this.domainService.getSelectedDomainValue())
    ).classifications;
    this.requestInProgress = false;
  }
}
