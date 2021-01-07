import { Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { NgForm } from '@angular/forms';
import { Select, Store } from '@ngxs/store';
import { ACTION } from 'app/shared/models/action';
import { customFieldCount, Workbasket } from 'app/shared/models/workbasket';
import { TaskanaDate } from 'app/shared/util/taskana.date';
import { SavingInformation, SavingWorkbasketService } from 'app/administration/services/saving-workbaskets.service';
import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';
import { FormsValidatorService } from 'app/shared/services/forms-validator/forms-validator.service';
import { filter, map, takeUntil } from 'rxjs/operators';
import { EngineConfigurationSelectors } from 'app/shared/store/engine-configuration-store/engine-configuration.selectors';
import { NOTIFICATION_TYPES } from '../../../shared/models/notifications';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { CustomField, getCustomFields, WorkbasketsCustomisation } from '../../../shared/models/customisation';
import {
  CopyWorkbasket,
  MarkWorkbasketForDeletion,
  RemoveDistributionTarget,
  SaveNewWorkbasket,
  UpdateWorkbasket
} from '../../../shared/store/workbasket-store/workbasket.actions';
import { WorkbasketComponent } from '../../models/workbasket-component';
import { WorkbasketSelectors } from '../../../shared/store/workbasket-store/workbasket.selectors';
import { ButtonAction } from '../../models/button-action';
import { AccessIdDefinition } from '../../../shared/models/access-id';

@Component({
  selector: 'taskana-administration-workbasket-information',
  templateUrl: './workbasket-information.component.html',
  styleUrls: ['./workbasket-information.component.scss']
})
export class WorkbasketInformationComponent implements OnInit, OnChanges, OnDestroy {
  @Input()
  workbasket: Workbasket;

  @Input()
  action: ACTION;

  @ViewChild('WorkbasketForm')
  workbasketForm: NgForm;

  workbasketClone: Workbasket;
  allTypes: Map<string, string>;
  badgeMessage = '';
  toggleValidationMap = new Map<string, boolean>();
  lookupField = false;

  readonly lengthError = 'You have reached the maximum length for this field';
  inputOverflowMap = new Map<string, boolean>();
  validateInputOverflow: Function;

  @Select(EngineConfigurationSelectors.workbasketsCustomisation)
  workbasketsCustomisation$: Observable<WorkbasketsCustomisation>;

  @Select(WorkbasketSelectors.buttonAction)
  buttonAction$: Observable<ButtonAction>;

  @Select(WorkbasketSelectors.selectedComponent)
  selectedComponent$: Observable<WorkbasketComponent>;

  customFields$: Observable<CustomField[]>;
  destroy$ = new Subject<void>();

  constructor(
    private workbasketService: WorkbasketService,
    private savingWorkbasket: SavingWorkbasketService,
    private requestInProgressService: RequestInProgressService,
    private formsValidatorService: FormsValidatorService,
    private notificationService: NotificationService,
    private store: Store
  ) {}

  ngOnInit() {
    this.allTypes = new Map([
      ['PERSONAL', 'Personal'],
      ['GROUP', 'Group'],
      ['CLEARANCE', 'Clearance'],
      ['TOPIC', 'Topic']
    ]);

    this.customFields$ = this.workbasketsCustomisation$.pipe(
      map((customisation) => customisation.information),
      getCustomFields(customFieldCount),
      map((customFields) => customFields.filter((customisation) => customisation.visible))
    );
    this.workbasketsCustomisation$.pipe(takeUntil(this.destroy$)).subscribe((workbasketsCustomization) => {
      if (workbasketsCustomization.information.owner) {
        this.lookupField = workbasketsCustomization.information.owner.lookupField;
      }
    });
    this.formsValidatorService.inputOverflowObservable.pipe(takeUntil(this.destroy$)).subscribe((inputOverflowMap) => {
      this.inputOverflowMap = inputOverflowMap;
    });
    this.validateInputOverflow = (inputFieldModel, maxLength) => {
      if (typeof inputFieldModel.value !== 'undefined') {
        this.formsValidatorService.validateInputOverflow(inputFieldModel, maxLength);
      }
    };
    this.buttonAction$
      .pipe(takeUntil(this.destroy$))
      .pipe(filter((buttonAction) => typeof buttonAction !== 'undefined'))
      .subscribe((button) => {
        switch (button) {
          case ButtonAction.SAVE:
            this.onSave();
            break;
          case ButtonAction.UNDO:
            this.onUndo();
            break;
          case ButtonAction.COPY:
            this.copyWorkbasket();
            break;
          case ButtonAction.REMOVE_AS_DISTRIBUTION_TARGETS:
            this.removeDistributionTargets();
            break;
          case ButtonAction.DELETE:
            this.removeWorkbasket();
            break;
          default:
            break;
        }
      });
  }

  ngOnChanges(changes?: SimpleChanges) {
    this.workbasketClone = { ...this.workbasket };
    if (this.action === ACTION.CREATE) {
      this.badgeMessage = 'Creating new workbasket';
    } else if (this.action === ACTION.COPY) {
      this.badgeMessage = `Copying workbasket: ${this.workbasket.key}`;
    }
  }

  onSubmit() {
    this.formsValidatorService.formSubmitAttempt = true;
    this.formsValidatorService.validateFormInformation(this.workbasketForm, this.toggleValidationMap).then((value) => {
      if (value) {
        this.onSave();
      }
    });
  }

  isFieldValid(field: string): boolean {
    return this.formsValidatorService.isFieldValid(this.workbasketForm, field);
  }

  onUndo() {
    this.formsValidatorService.formSubmitAttempt = false;
    this.notificationService.showToast(NOTIFICATION_TYPES.INFO_ALERT);
    this.workbasket = { ...this.workbasketClone };
  }

  removeWorkbasket() {
    this.notificationService.showDialog(
      `You are going to delete workbasket: ${this.workbasket.key}. Can you confirm this action?`,
      this.onRemoveConfirmed.bind(this)
    );
  }

  copyWorkbasket() {
    this.store.dispatch(new CopyWorkbasket(this.workbasket));
  }

  removeDistributionTargets() {
    this.store.dispatch(new RemoveDistributionTarget(this.workbasket._links.removeDistributionTargets.href));
  }

  onSave() {
    this.beforeRequest();
    if (!this.workbasket.workbasketId) {
      this.postNewWorkbasket();
      return;
    }
    this.store.dispatch(new UpdateWorkbasket(this.workbasket._links.self.href, this.workbasket)).subscribe(() => {
      this.requestInProgressService.setRequestInProgress(false);
      this.workbasketClone = { ...this.workbasket };
    });
  }

  beforeRequest() {
    this.requestInProgressService.setRequestInProgress(true);
  }

  afterRequest() {
    this.requestInProgressService.setRequestInProgress(false);
    this.workbasketService.triggerWorkBasketSaved();
  }

  postNewWorkbasket() {
    this.addDateToWorkbasket();
    this.store.dispatch(new SaveNewWorkbasket(this.workbasket)).subscribe(() => {
      this.afterRequest();
      if (this.action === ACTION.COPY) {
        this.savingWorkbasket.triggerDistributionTargetSaving(
          new SavingInformation(this.workbasket._links.distributionTargets.href, this.workbasket.workbasketId)
        );
        this.savingWorkbasket.triggerAccessItemsSaving(
          new SavingInformation(this.workbasket._links.accessItems.href, this.workbasket.workbasketId)
        );
      }
    });
  }

  addDateToWorkbasket() {
    const date = TaskanaDate.getDate();
    this.workbasket.created = date;
    this.workbasket.modified = date;
  }

  onRemoveConfirmed() {
    this.beforeRequest();
    this.store.dispatch(new MarkWorkbasketForDeletion(this.workbasket._links.self.href)).subscribe(() => {
      this.afterRequest();
    });
  }

  onSelectedOwner(owner: AccessIdDefinition) {
    if (owner?.accessId) {
      this.workbasket.owner = owner.accessId;
    }
  }

  getWorkbasketCustomProperty(custom: number) {
    return `custom${custom}`;
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
