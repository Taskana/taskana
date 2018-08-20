import {
  Component,
  OnInit,
  Input,
  OnDestroy,
  OnChanges,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { NgForm } from '@angular/forms';

import { ICONTYPES } from 'app/models/type';
import { ErrorModel } from 'app/models/modal-error';
import { ACTION } from 'app/models/action';
import { Workbasket } from 'app/models/workbasket';
import { AlertModel, AlertType } from 'app/models/alert';
import { TaskanaDate } from 'app/shared/util/taskana.date';

import { AlertService } from 'app/services/alert/alert.service';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import {
  SavingWorkbasketService,
  SavingInformation
} from 'app/administration/services/saving-workbaskets/saving-workbaskets.service';
import { WorkbasketService } from 'app/services/workbasket/workbasket.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { CustomFieldsService } from 'app/services/custom-fields/custom-fields.service';
import { RemoveConfirmationService } from 'app/services/remove-confirmation/remove-confirmation.service';
import { FormsValidatorService } from 'app/shared/services/forms/forms-validator.service';

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

  ownerField = this.customFieldsService.getCustomField(
    'Owner',
    'workbaskets.information.owner'
  );
  custom1Field = this.customFieldsService.getCustomField(
    'Custom 1',
    'workbaskets.information.custom1'
  );
  custom2Field = this.customFieldsService.getCustomField(
    'Custom 2',
    'workbaskets.information.custom2'
  );
  custom3Field = this.customFieldsService.getCustomField(
    'Custom 3',
    'workbaskets.information.custom3'
  );
  custom4Field = this.customFieldsService.getCustomField(
    'Custom 4',
    'workbaskets.information.custom4'
  );

  toogleValidationMap = new Map<string, boolean>();

  private workbasketSubscription: Subscription;
  private routeSubscription: Subscription;
  @ViewChild('WorkbasketForm')
  workbasketForm: NgForm;

  constructor(
    private workbasketService: WorkbasketService,
    private alertService: AlertService,
    private route: ActivatedRoute,
    private router: Router,
    private errorModalService: ErrorModalService,
    private savingWorkbasket: SavingWorkbasketService,
    private requestInProgressService: RequestInProgressService,
    private customFieldsService: CustomFieldsService,
    private removeConfirmationService: RemoveConfirmationService,
    private formsValidatorService: FormsValidatorService
  ) {
    this.allTypes = new Map([
      ['PERSONAL', 'Personal'],
      ['GROUP', 'Group'],
      ['CLEARANCE', 'Clearance'],
      ['TOPIC', 'Topic']
    ]);
  }

  ngOnInit(): void {}

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
          this.errorModalService.triggerError(
            new ErrorModel(
              `There was an error removing distribution target for ${
                this.workbasket.workbasketId
              }.`,
              error
            )
          );
          this.requestInProgressService.setRequestInProgress(false);
        }
      );
  }

  private onSave() {
    this.beforeRequest();
    if (!this.workbasket.workbasketId) {
      this.postNewWorkbasket();
      return true;
    }

    this.workbasketSubscription = this.workbasketService
      .updateWorkbasket(this.workbasket._links.self.href, this.workbasket)
      .subscribe(
        workbasketUpdated => {
          this.afterRequest();
          this.workbasket = workbasketUpdated;
          this.workbasketClone = { ...this.workbasket };
          this.alertService.triggerAlert(
            new AlertModel(
              AlertType.SUCCESS,
              `Workbasket ${workbasketUpdated.key} was saved successfully`
            )
          );
        },
        error => {
          this.afterRequest();
          this.errorModalService.triggerError(
            new ErrorModel(
              'There was error while saving your workbasket',
              error
            )
          );
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
        this.router.navigate(['../' + this.workbasket.workbasketId], {
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
        this.errorModalService.triggerError(
          new ErrorModel('There was an error creating a workbasket', error)
        );
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
      .deleteWorkbasket(this.workbasket._links.self.href)
      .subscribe(
        response => {
          this.requestInProgressService.setRequestInProgress(false);
          this.workbasketService.triggerWorkBasketSaved();
          this.alertService.triggerAlert(
            new AlertModel(
              AlertType.SUCCESS,
              `Workbasket ${
                this.workbasket.workbasketId
              } was removed successfully`
            )
          );
          this.router.navigate(['administration/workbaskets']);
        },
        error => {
          this.requestInProgressService.setRequestInProgress(false);
          this.errorModalService.triggerError(
            new ErrorModel(
              `There was an error deleting workbasket ${
                this.workbasket.workbasketId
              }`,
              error
            )
          );
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
}
