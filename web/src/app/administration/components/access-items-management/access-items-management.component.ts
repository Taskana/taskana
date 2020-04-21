import { Component, OnDestroy, OnInit } from '@angular/core';
import { Select } from '@ngxs/store';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';

import { Observable, Subscription } from 'rxjs';

import { FormsValidatorService } from 'app/shared/services/forms-validator/forms-validator.service';
import { AccessItemWorkbasketResource } from 'app/shared/models/access-item-workbasket-resource';
import { AccessItemWorkbasket } from 'app/shared/models/access-item-workbasket';
import { Sorting } from 'app/shared/models/sorting';
import { GeneralModalService } from 'app/shared/services/general-modal/general-modal.service';
import { RemoveConfirmationService } from 'app/shared/services/remove-confirmation/remove-confirmation.service';
import { AlertModel, AlertType } from 'app/shared/models/alert';
import { AlertService } from 'app/shared/services/alert/alert.service';
import { EngineConfigurationSelectors } from 'app/store/engine-configuration-store/engine-configuration.selectors';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';
import { AccessIdsService } from '../../../shared/services/access-ids/access-ids.service';
import { AccessIdDefinition } from '../../../shared/models/access-id';
import { ErrorsService } from '../../../shared/services/errors/errors.service';
import { ERROR_TYPES } from '../../../shared/models/errors';
import { AccessItemsCustomisation, CustomField, getCustomFields } from '../../../shared/models/customisation';
import { customFieldCount } from '../../../shared/models/workbasket-access-items';

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
  belongingGroups: Array<AccessIdDefinition>;
  sortingFields = new Map([['workbasket-key', 'Workbasket Key'], ['access-id', 'Access id']]);
  accessItemDefaultSortBy: string = 'workbasket-key';
  sortModel: Sorting = new Sorting(this.accessItemDefaultSortBy);
  isGroup: boolean;
  groupsKey = 'ou=groups';

  @Select(EngineConfigurationSelectors.accessItemsCustomisation) accessItemsCustomization$: Observable<AccessItemsCustomisation>;
  customFields$: Observable<CustomField[]>;

  constructor(private formBuilder: FormBuilder,
    private accessIdsService: AccessIdsService,
    private formsValidatorService: FormsValidatorService,
    private requestInProgressService: RequestInProgressService,
    private removeConfirmationService: RemoveConfirmationService,
    private alertService: AlertService,
    private generalModalService: GeneralModalService,
    private errorsService: ErrorsService) {
  }

  get accessItemsGroups(): FormArray {
    return this.AccessItemsForm ? this.AccessItemsForm.get('accessItemsGroups') as FormArray : null;
  }

  private static unSubscribe(subscription: Subscription): void {
    if (subscription) {
      subscription.unsubscribe();
    }
  }

  ngOnInit() {
    this.customFields$ = this.accessItemsCustomization$.pipe(getCustomFields(customFieldCount));
  }

  setAccessItemsGroups(accessItems: Array<AccessItemWorkbasket>) {
    const AccessItemsFormGroups = accessItems.map(accessItem => this.formBuilder.group(accessItem));
    AccessItemsFormGroups.forEach(accessItemGroup => {
      accessItemGroup.controls.accessId.setValidators(Validators.required);
      Object.keys(accessItemGroup.controls).forEach(key => {
        accessItemGroup.controls[key].disable();
      });
    });
    const AccessItemsFormArray = this.formBuilder.array(AccessItemsFormGroups);
    if (!this.AccessItemsForm) {
      this.AccessItemsForm = this.formBuilder.group({});
    }
    this.AccessItemsForm.setControl('accessItemsGroups', AccessItemsFormArray);
    if (!this.AccessItemsForm.value.workbasketKeyFilter) {
      this.AccessItemsForm.addControl('workbasketKeyFilter', new FormControl());
    }
    if (!this.AccessItemsForm.value.accessIdFilter) {
      this.AccessItemsForm.addControl('accessIdFilter', new FormControl());
    }
  }

  onSelectAccessId(selected: AccessIdDefinition) {
    if (!selected) {
      this.AccessItemsForm = null;
      return;
    }
    if (!this.AccessItemsForm || this.accessIdPrevious !== selected.accessId) {
      this.accessIdPrevious = selected.accessId;
      this.isGroup = selected.accessId.includes(this.groupsKey);

      AccessItemsManagementComponent.unSubscribe(this.accessItemInformationsubscription);
      this.accessItemInformationsubscription = this.accessIdsService.getAccessItemsInformation(selected.accessId, true)
        .subscribe((accessIdsWithGroups: Array<AccessIdDefinition>) => {
          this.accessIdsWithGroups = accessIdsWithGroups;
          this.belongingGroups = accessIdsWithGroups.filter(item => item.accessId.includes(this.groupsKey));
          this.searchForAccessItemsWorkbaskets();
        },
        error => {
          this.requestInProgressService.setRequestInProgress(false);
          this.errorsService.updateError(ERROR_TYPES.FETCH_ERR, error);
        });
    }
  }

  isFieldValid(field: string, index: number): boolean {
    return this.formsValidatorService.isFieldValid(this.accessItemsGroups[index], field);
  }

  sorting(sort: Sorting) {
    this.sortModel = sort;
    this.searchForAccessItemsWorkbaskets();
  }

  searchForAccessItemsWorkbaskets() {
    this.requestInProgressService.setRequestInProgress(true);
    AccessItemsManagementComponent.unSubscribe(this.accessItemPermissionsSubscription);
    this.accessItemPermissionsSubscription = this.accessIdsService.getAccessItemsPermissions(
      this.accessIdsWithGroups,
      this.AccessItemsForm ? this.AccessItemsForm.value.accessIdFilter : undefined,
      this.AccessItemsForm ? this.AccessItemsForm.value.workbasketKeyFilter : undefined,
      this.sortModel,
      true
    )
      .subscribe((accessItemsResource: AccessItemWorkbasketResource) => {
        this.setAccessItemsGroups(accessItemsResource ? accessItemsResource.accessItems : []);
        this.requestInProgressService.setRequestInProgress(false);
      },
      error => {
        this.requestInProgressService.setRequestInProgress(false);
        this.errorsService.updateError(ERROR_TYPES.FETCH_ERR_2, error);
      });
  }

  revokeAccess() {
    this.removeConfirmationService.setRemoveConfirmation(
      this.onRemoveConfirmed.bind(this),
      `You are going to delete all access related: ${
        this.accessIdSelected
      }. Can you confirm this action?`
    );
  }

  ngOnDestroy(): void {
    AccessItemsManagementComponent.unSubscribe(this.accessItemPermissionsSubscription);
    AccessItemsManagementComponent.unSubscribe(this.accessItemInformationsubscription);
  }

  private onRemoveConfirmed() {
    this.requestInProgressService.setRequestInProgress(true);
    this.accessIdsService.removeAccessItemsPermissions(this.accessIdSelected)
      .subscribe(
        // new Key: ALERT_TYPES.SUCCESS_ALERT
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
          this.errorsService.updateError(ERROR_TYPES.DELETE_ERR, error);
        }
      );
  }
}
