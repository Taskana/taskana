import { Component, OnInit, Input, OnDestroy, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, Subscription } from 'rxjs';
import { NgForm } from '@angular/forms';
import { Select } from '@ngxs/store';

import { ICONTYPES } from 'app/models/type';
import { ACTION } from 'app/models/action';
import { customFieldCount, Workbasket } from 'app/models/workbasket';
import { AlertModel, AlertType } from 'app/models/alert';
import { TaskanaDate } from 'app/shared/util/taskana.date';

import { AlertService } from 'app/services/alert/alert.service';
import { GeneralModalService } from 'app/services/general-modal/general-modal.service';
import { SavingWorkbasketService, SavingInformation } from 'app/administration/services/saving-workbaskets.service';
import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { RemoveConfirmationService } from 'app/services/remove-confirmation/remove-confirmation.service';
import { FormsValidatorService } from 'app/shared/services/forms/forms-validator.service';
import { map } from 'rxjs/operators';
import { EngineConfigurationSelectors } from 'app/store/engine-configuration-store/engine-configuration.selectors';
import { ERROR_TYPES } from '../../../../models/errors';
import { ErrorsService } from '../../../../services/errors/errors.service';
import { CustomField, getCustomFields, WorkbasketsCustomisation } from '../../../../models/customisation';

@Component({
  selector: 'taskana-workbasket-information',
  templateUrl: './workbasket-information.component.html',
  styleUrls: ['./workbasket-information.component.scss']
})
export class WorkbasketInformationComponent
implements OnInit, OnChanges, OnDestroy {
  @Input()
  workbasket: Workbasket;

  workbasketClone: Workbasket;
  workbasketErrors;
  @Input()
  action: string;

  allTypes: Map<string, string>;
  requestInProgress = false;
  badgeMessage = '';

  @Select(EngineConfigurationSelectors.workbasketsCustomisation) workbasketsCustomisation$: Observable<WorkbasketsCustomisation>;
  customFields$: Observable<CustomField[]>;

  toogleValidationMap = new Map<string, boolean>();

  private workbasketSubscription: Subscription;
  private routeSubscription: Subscription;
  @ViewChild('WorkbasketForm', { static: false })
  workbasketForm: NgForm;

  constructor(
    private workbasketService: WorkbasketService,
    private alertService: AlertService,
    private route: ActivatedRoute,
    private router: Router,
    private generalModalService: GeneralModalService,
    private savingWorkbasket: SavingWorkbasketService,
    private requestInProgressService: RequestInProgressService,
    private removeConfirmationService: RemoveConfirmationService,
    private formsValidatorService: FormsValidatorService,
    private errorsService: ErrorsService
  ) {}

  ngOnInit(): void {
    this.allTypes = new Map([
      ['PERSONAL', 'Personal'],
      ['GROUP', 'Group'],
      ['CLEARANCE', 'Clearance'],
      ['TOPIC', 'Topic']
    ]);
    this.customFields$ = this.workbasketsCustomisation$.pipe(
      map(customisation => customisation.information),
      getCustomFields(customFieldCount)
    );
  }

  ngOnChanges(changes: SimpleChanges): void {
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
    this.formsValidatorService
      .validateFormInformation(this.workbasketForm, this.toogleValidationMap)
      .then(value => {
        if (value) {
          this.onSave();
        }
      });
  }

  isFieldValid(field: string): boolean {
    return this.formsValidatorService.isFieldValid(this.workbasketForm, field);
  }

  onClear() {
    this.formsValidatorService.formSubmitAttempt = false;
    this.alertService.triggerAlert(
      // new Key ALERT_TYPES.INFO_ALERT
      new AlertModel(AlertType.INFO, 'Reset edited fields')
    );
    this.workbasket = { ...this.workbasketClone };
  }

  removeWorkbasket() {
    this.removeConfirmationService.setRemoveConfirmation(
      this.onRemoveConfirmed.bind(this),
      `You are going to delete workbasket: ${
        this.workbasket.key
      }. Can you confirm this action?`
    );
  }

  copyWorkbasket() {
    this.router.navigate([{ outlets: { detail: ['copy-workbasket'] } }], {
      relativeTo: this.route.parent
    });
  }

  removeDistributionTargets() {
    this.requestInProgressService.setRequestInProgress(true);
    this.workbasketService
      .removeDistributionTarget(
        this.workbasket._links.removeDistributionTargets.href
      )
      .subscribe(
        reponse => {
          this.requestInProgressService.setRequestInProgress(false);
          // new Key ALERT_TYPES.SUCCESS_ALERT_9
          this.alertService.triggerAlert(
            new AlertModel(
              AlertType.SUCCESS,
              `DistributionTarget for workbasketID: ${
                this.workbasket.workbasketId
              } was removed successfully`
            )
          );
        },
        error => {
          this.errorsService.updateError(ERROR_TYPES.REMOVE_ERR_2,
            error,
            new Map<String, String>([['workbasketId', this.workbasket.workbasketId]]));
          this.requestInProgressService.setRequestInProgress(false);
        }
      );
  }

  private onSave() {
    this.beforeRequest();
    if (!this.workbasket.workbasketId) {
      this.postNewWorkbasket();
      return;
    }

    this.workbasketSubscription = this.workbasketService
      .updateWorkbasket(this.workbasket._links.self.href, this.workbasket)
      .subscribe(
        workbasketUpdated => {
          this.afterRequest();
          this.workbasket = workbasketUpdated;
          this.workbasketClone = { ...this.workbasket };
          // new Key ALERT_TYPES.SUCCESS_ALERT_10
          this.alertService.triggerAlert(
            new AlertModel(
              AlertType.SUCCESS,
              `Workbasket ${workbasketUpdated.key} was saved successfully`
            )
          );
        },
        error => {
          this.afterRequest();
          this.errorsService.updateError(ERROR_TYPES.SAVE_ERR_4, error);
        }
      );
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
    this.workbasketService.createWorkbasket(this.workbasket).subscribe(
      (workbasketUpdated: Workbasket) => {
        // new Key ALERT_TYPES.SUCCESS_ALERT_11
        this.alertService.triggerAlert(
          new AlertModel(
            AlertType.SUCCESS,
            `Workbasket ${workbasketUpdated.key} was created successfully`
          )
        );
        this.workbasket = workbasketUpdated;
        this.afterRequest();
        this.workbasketService.triggerWorkBasketSaved();
        this.workbasketService.selectWorkBasket(this.workbasket.workbasketId);
        this.router.navigate([`../${this.workbasket.workbasketId}`], {
          relativeTo: this.route
        });
        if (this.action === ACTION.COPY) {
          this.savingWorkbasket.triggerDistributionTargetSaving(
            new SavingInformation(
              this.workbasket._links.distributionTargets.href,
              this.workbasket.workbasketId
            )
          );
          this.savingWorkbasket.triggerAccessItemsSaving(
            new SavingInformation(
              this.workbasket._links.accessItems.href,
              this.workbasket.workbasketId
            )
          );
        }
      },
      error => {
        this.errorsService.updateError(ERROR_TYPES.CREATE_ERR_2, error);
        this.requestInProgressService.setRequestInProgress(false);
      }
    );
  }

  private addDateToWorkbasket() {
    const date = TaskanaDate.getDate();
    this.workbasket.created = date;
    this.workbasket.modified = date;
  }

  private onRemoveConfirmed() {
    this.requestInProgressService.setRequestInProgress(true);
    this.workbasketService
      .markWorkbasketForDeletion(this.workbasket._links.self.href)
      .subscribe(
        response => {
          this.requestInProgressService.setRequestInProgress(false);
          this.workbasketService.triggerWorkBasketSaved();
          if (response.status === 202) {
            this.errorsService.updateError(ERROR_TYPES.MARK_ERR,
              undefined,
              new Map<String, String>([['workbasketId', this.workbasket.workbasketId]]));
          } else {
            // new Key ALERT_TYPES.SUCCESS_ALERT_12
            this.alertService.triggerAlert(
              new AlertModel(AlertType.SUCCESS, `The Workbasket ${this.workbasket.workbasketId} has been deleted.`)
            );
          }
          this.router.navigate(['taskana/administration/workbaskets']);
        }
      );
  }

  ngOnDestroy() {
    if (this.workbasketSubscription) {
      this.workbasketSubscription.unsubscribe();
    }
    if (this.routeSubscription) {
      this.routeSubscription.unsubscribe();
    }
  }

  getWorkbasketCustomProperty(custom: number) {
    return `custom${custom}`;
  }
}
