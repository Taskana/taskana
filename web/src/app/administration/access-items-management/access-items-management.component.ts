import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormArray, Validators, FormGroup, FormControl } from '@angular/forms';
import { CustomFieldsService } from 'app/services/custom-fields/custom-fields.service';

import { Subscription } from 'rxjs';

import { FormsValidatorService } from 'app/shared/services/forms/forms-validator.service';
import { AccessIdDefinition } from '../../models/access-id';
import { AccessItemsWorkbasketResource } from 'app/models/access-item-workbasket-resource';
import { AccessItemWorkbasket } from 'app/models/access-item-workbasket';
import { AccessIdsService } from '../../shared/services/access-ids/access-ids.service';
import { SortingModel } from 'app/models/sorting';
import { RequestInProgressService } from '../../services/requestInProgress/request-in-progress.service';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { ErrorModel } from 'app/models/modal-error';
import { RemoveConfirmationService } from 'app/services/remove-confirmation/remove-confirmation.service';
import { AlertModel, AlertType } from 'app/models/alert';
import { AlertService } from 'app/services/alert/alert.service';

@Component({
  selector: 'taskana-access-items-management',
  templateUrl: './access-items-management.component.html',
  styleUrls: ['./access-items-management.component.scss']
})
export class AccessItemsManagementComponent implements OnInit, OnDestroy {

  accessIdSelected;
  accessIdPrevious;

  AccessItemsForm: FormGroup;
  toogleValidationAccessIdMap = new Map<number, boolean>();
  accessItemPermissionsSubscription: Subscription;
  accessItemInformationsubscription: Subscription;
  accessIdsWithGroups: Array<AccessIdDefinition>;
  sortingFields = new Map([['workbasket-key', 'Workbasket Key'], ['access-id', 'Access id']]);
  sortModel: SortingModel;


  accessIdField = this.customFieldsService.getCustomField('Owner', 'workbaskets.access-items.accessId');
  custom1Field = this.customFieldsService.getCustomField('Custom 1', 'workbaskets.access-items.custom1');
  custom2Field = this.customFieldsService.getCustomField('Custom 2', 'workbaskets.access-items.custom2');
  custom3Field = this.customFieldsService.getCustomField('Custom 3', 'workbaskets.access-items.custom3');
  custom4Field = this.customFieldsService.getCustomField('Custom 4', 'workbaskets.access-items.custom4');
  custom5Field = this.customFieldsService.getCustomField('Custom 5', 'workbaskets.access-items.custom5');
  custom6Field = this.customFieldsService.getCustomField('Custom 6', 'workbaskets.access-items.custom6');
  custom7Field = this.customFieldsService.getCustomField('Custom 7', 'workbaskets.access-items.custom7');
  custom8Field = this.customFieldsService.getCustomField('Custom 8', 'workbaskets.access-items.custom8');
  custom9Field = this.customFieldsService.getCustomField('Custom 9', 'workbaskets.access-items.custom9');
  custom10Field = this.customFieldsService.getCustomField('Custom 10', 'workbaskets.access-items.custom10');
  custom11Field = this.customFieldsService.getCustomField('Custom 11', 'workbaskets.access-items.custom11');
  custom12Field = this.customFieldsService.getCustomField('Custom 12', 'workbaskets.access-items.custom12');

  setAccessItemsGroups(accessItems: Array<AccessItemWorkbasket>) {
    const AccessItemsFormGroups = accessItems.map(accessItem => this.formBuilder.group(accessItem));
    AccessItemsFormGroups.map(accessItemGroup => {
      accessItemGroup.controls['accessId'].setValidators(Validators.required);
      for (const key of Object.keys(accessItemGroup.controls)) {
        accessItemGroup.controls[key].disable();
      }
    });
    const AccessItemsFormArray = this.formBuilder.array(AccessItemsFormGroups);
    if (!this.AccessItemsForm) { this.AccessItemsForm = this.formBuilder.group({}); }
    this.AccessItemsForm.setControl('accessItemsGroups', AccessItemsFormArray);
    if (!this.AccessItemsForm.value['workbasketKeyFilter']) { this.AccessItemsForm.addControl('workbasketKeyFilter', new FormControl()) }
    if (!this.AccessItemsForm.value['accessIdFilter']) { this.AccessItemsForm.addControl('accessIdFilter', new FormControl()) }
  };

  get accessItemsGroups(): FormArray {
    return this.AccessItemsForm ? this.AccessItemsForm.get('accessItemsGroups') as FormArray : null;
  };

  constructor(private formBuilder: FormBuilder,
    private customFieldsService: CustomFieldsService,
    private accessIdsService: AccessIdsService,
    private formsValidatorService: FormsValidatorService,
    private requestInProgressService: RequestInProgressService,
    private removeConfirmationService: RemoveConfirmationService,
    private alertService: AlertService,
    private errorModalService: ErrorModalService) { }


  ngOnInit() {
  }

  onSelectAccessId(selected: AccessIdDefinition) {
    if (!selected) {
      this.AccessItemsForm = null;
      return;
    }
    if (!this.AccessItemsForm || this.accessIdPrevious !== selected.accessId) {
      this.accessIdPrevious = selected.accessId

      this.unSubscribe(this.accessItemInformationsubscription)
      this.accessItemInformationsubscription = this.accessIdsService.getAccessItemsInformation(selected.accessId, true)
        .subscribe((accessIdsWithGroups: Array<AccessIdDefinition>) => {
          this.accessIdsWithGroups = accessIdsWithGroups;
          this.searchForAccessItemsWorkbaskets();
        },
          error => {
            this.requestInProgressService.setRequestInProgress(false);
            this.errorModalService.triggerError(
              new ErrorModel(
                'There was error while retrieving your access ids with groups',
                error
              )
            )
          }
        )
    }
  }

  isFieldValid(field: string, index: number): boolean {
    return this.formsValidatorService.isFieldValid(this.accessItemsGroups[index], field);
  }

  sorting(sort: SortingModel) {
    this.sortModel = sort;
    this.searchForAccessItemsWorkbaskets();
  }

  searchForAccessItemsWorkbaskets() {
    this.requestInProgressService.setRequestInProgress(true);
    this.unSubscribe(this.accessItemPermissionsSubscription)
    this.accessItemPermissionsSubscription = this.accessIdsService.getAccessItemsPermissions(
      this.accessIdsWithGroups,
      this.AccessItemsForm ? this.AccessItemsForm.value.accessIdFilter : undefined,
      this.AccessItemsForm ? this.AccessItemsForm.value.workbasketKeyFilter : undefined,
      this.sortModel,
      true)
      .subscribe((accessItemsResource: AccessItemsWorkbasketResource) => {
        this.setAccessItemsGroups(accessItemsResource._embedded ? accessItemsResource._embedded.accessItems : []);
        this.requestInProgressService.setRequestInProgress(false);
      },
        error => {
          this.requestInProgressService.setRequestInProgress(false);
          this.errorModalService.triggerError(
            new ErrorModel(
              'There was error while retrieving your access items',
              error
            )
          );
        })

  }

  revokeAccess() {
    this.removeConfirmationService.setRemoveConfirmation(
      this.onRemoveConfirmed.bind(this),
      `You are going to delete all access related: ${
        this.accessIdSelected
      }. Can you confirm this action?`
    );
  }

  private onRemoveConfirmed() {
    this.requestInProgressService.setRequestInProgress(true);
    this.accessIdsService.removeAccessItemsPermissions(this.accessIdSelected)
    .subscribe(
      response => {
        this.requestInProgressService.setRequestInProgress(false);
        this.alertService.triggerAlert(
          new AlertModel(
            AlertType.SUCCESS,
            `${this.accessIdSelected
            } was removed successfully`
          )
        );
        this.searchForAccessItemsWorkbaskets();
    },
    error => {
      this.requestInProgressService.setRequestInProgress(false);
      this.errorModalService.triggerError(
        new ErrorModel(
          `You can't delete a group`,
          error
        )
      );
    });
  }

  private unSubscribe(subscription: Subscription): void {
    if (subscription) { subscription.unsubscribe(); }
  }

  ngOnDestroy(): void {
    this.unSubscribe(this.accessItemPermissionsSubscription)
    this.unSubscribe(this.accessItemInformationsubscription)
  }
}
