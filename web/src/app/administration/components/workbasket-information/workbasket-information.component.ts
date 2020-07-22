import { Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, Subject, Subscription, timer } from 'rxjs';
import { NgForm, NgModel } from '@angular/forms';
import { Select, Store } from '@ngxs/store';

import { ICONTYPES } from 'app/shared/models/icon-types';
import { ACTION } from 'app/shared/models/action';
import { customFieldCount, Workbasket } from 'app/shared/models/workbasket';
import { TaskanaDate } from 'app/shared/util/taskana.date';

import { SavingWorkbasketService, SavingInformation } from 'app/administration/services/saving-workbaskets.service';
import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';
import { FormsValidatorService } from 'app/shared/services/forms-validator/forms-validator.service';
import { map, takeUntil } from 'rxjs/operators';
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

  @ViewChild('WorkbasketForm', { static: false })
  workbasketForm: NgForm;

  workbasketClone: Workbasket;
  allTypes: Map<string, string>;
  requestInProgress = false;
  badgeMessage = '';
  toogleValidationMap = new Map<string, boolean>();
  lookupField = false;

  @Select(EngineConfigurationSelectors.workbasketsCustomisation)
  workbasketsCustomisation$: Observable<WorkbasketsCustomisation>;

  customFields$: Observable<CustomField[]>;
  destroy$ = new Subject<void>();
  readonly lengthError = 'You have reached the maximum length';
  tooLongMap = new Map<string, boolean>();
  private timeout = new Map<string, Subscription>();

  constructor(
    private workbasketService: WorkbasketService,
    private route: ActivatedRoute,
    private router: Router,
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
      getCustomFields(customFieldCount)
    );
    this.workbasketsCustomisation$.pipe(takeUntil(this.destroy$)).subscribe((workbasketsCustomization) => {
      if (workbasketsCustomization.information.owner) {
        this.lookupField = workbasketsCustomization.information.owner.lookupField;
      }
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    this.workbasketClone = { ...this.workbasket };
    if (this.action === ACTION.CREATE) {
      this.badgeMessage = 'Creating new workbasket';
    } else if (this.action === ACTION.COPY) {
      this.badgeMessage = `Copying workbasket: ${this.workbasket.key}`;
    }
  }

  selectType(type: ICONTYPES) {
    this.workbasket.type = type;
  }

  onSubmit() {
    this.formsValidatorService.formSubmitAttempt = true;
    this.formsValidatorService.validateFormInformation(this.workbasketForm, this.toogleValidationMap).then((value) => {
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

  private onSave() {
    this.beforeRequest();
    if (!this.workbasket.workbasketId) {
      this.postNewWorkbasket();
      return;
    }
    this.store.dispatch(new UpdateWorkbasket(this.workbasket._links.self.href, this.workbasket)).subscribe((state) => {
      this.requestInProgressService.setRequestInProgress(false);
      this.workbasketClone = { ...this.workbasket };
    });
  }

  private beforeRequest() {
    this.requestInProgressService.setRequestInProgress(true);
  }

  private afterRequest() {
    this.requestInProgressService.setRequestInProgress(false);
    this.workbasketService.triggerWorkBasketSaved();
  }

  private postNewWorkbasket() {
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

  private addDateToWorkbasket() {
    const date = TaskanaDate.getDate();
    this.workbasket.created = date;
    this.workbasket.modified = date;
  }

  private onRemoveConfirmed() {
    this.beforeRequest();
    this.store.dispatch(new MarkWorkbasketForDeletion(this.workbasket._links.self.href)).subscribe(() => {
      this.afterRequest();
    });
  }

  getWorkbasketCustomProperty(custom: number) {
    return `custom${custom}`;
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onKeyPressed(model: NgModel, max: Number): void {
    if (this.timeout.has(model.name)) {
      this.timeout.get(model.name).unsubscribe();
    }
    console.log(model.name);
    if (model.value.length >= max) {
      this.tooLongMap.set(model.name, true);
      this.timeout.set(
        model.name,
        timer(3000).subscribe(() => this.tooLongMap.set(model.name, false))
      );
    }
  }
}
