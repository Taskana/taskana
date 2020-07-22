import { Component, OnInit } from '@angular/core';
import { Select } from '@ngxs/store';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';

import { Observable } from 'rxjs';

import { FormsValidatorService } from 'app/shared/services/forms-validator/forms-validator.service';
import { AccessItemWorkbasketResource } from 'app/shared/models/access-item-workbasket-resource';
import { AccessItemWorkbasket } from 'app/shared/models/access-item-workbasket';
import { Direction, Sorting } from 'app/shared/models/sorting';

import { EngineConfigurationSelectors } from 'app/shared/store/engine-configuration-store/engine-configuration.selectors';
import { take } from 'rxjs/operators';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';
import { AccessIdsService } from '../../../shared/services/access-ids/access-ids.service';
import { AccessIdDefinition } from '../../../shared/models/access-id';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { NOTIFICATION_TYPES } from '../../../shared/models/notifications';
import { AccessItemsCustomisation, CustomField, getCustomFields } from '../../../shared/models/customisation';
import { customFieldCount } from '../../../shared/models/workbasket-access-items';

@Component({
  selector: 'taskana-administration-access-items-management',
  templateUrl: './access-items-management.component.html',
  styleUrls: ['./access-items-management.component.scss']
})
export class AccessItemsManagementComponent implements OnInit {
  accessIdSelected;
  accessIdPrevious;

  accessItemsForm: FormGroup;
  toggleValidationAccessIdMap = new Map<number, boolean>();
  accessId: AccessIdDefinition;
  groups: AccessIdDefinition[];
  sortingFields = new Map([
    ['access-id', 'Access id'],
    ['workbasket-key', 'Workbasket Key']
  ]);
  sortModel: Sorting = new Sorting('access-id', Direction.DESC);
  isGroup: boolean = false;

  @Select(EngineConfigurationSelectors.accessItemsCustomisation) accessItemsCustomization$: Observable<
    AccessItemsCustomisation
  >;
  customFields$: Observable<CustomField[]>;

  constructor(
    private formBuilder: FormBuilder,
    private accessIdsService: AccessIdsService,
    private formsValidatorService: FormsValidatorService,
    private requestInProgressService: RequestInProgressService,
    private notificationService: NotificationService
  ) {}

  get accessItemsGroups(): FormArray {
    return this.accessItemsForm ? (this.accessItemsForm.get('accessItemsGroups') as FormArray) : null;
  }

  ngOnInit() {
    this.customFields$ = this.accessItemsCustomization$.pipe(getCustomFields(customFieldCount));
  }

  setAccessItemsGroups(accessItems: Array<AccessItemWorkbasket>) {
    const AccessItemsFormGroups = accessItems.map((accessItem) => this.formBuilder.group(accessItem));
    AccessItemsFormGroups.forEach((accessItemGroup) => {
      accessItemGroup.controls.accessId.setValidators(Validators.required);
      Object.keys(accessItemGroup.controls).forEach((key) => {
        accessItemGroup.controls[key].disable();
      });
    });
    const AccessItemsFormArray = this.formBuilder.array(AccessItemsFormGroups);
    if (!this.accessItemsForm) {
      this.accessItemsForm = this.formBuilder.group({});
    }
    this.accessItemsForm.setControl('accessItemsGroups', AccessItemsFormArray);
    if (!this.accessItemsForm.value.workbasketKeyFilter) {
      this.accessItemsForm.addControl('workbasketKeyFilter', new FormControl());
    }
    if (!this.accessItemsForm.value.accessIdFilter) {
      this.accessItemsForm.addControl('accessIdFilter', new FormControl());
    }
  }

  onSelectAccessId(selected: AccessIdDefinition) {
    if (!selected) {
      this.accessItemsForm = null;
      return;
    }

    if (this.accessIdPrevious !== selected.accessId) {
      this.accessIdPrevious = selected.accessId;

      this.accessIdsService
        .getGroupsByAccessId(selected.accessId)
        .pipe(take(1))
        .subscribe(
          (groups: AccessIdDefinition[]) => {
            this.accessId = selected;
            this.groups = groups;
            this.searchForAccessItemsWorkbaskets();
          },
          (error) => {
            this.requestInProgressService.setRequestInProgress(false);
            this.notificationService.triggerError(NOTIFICATION_TYPES.FETCH_ERR, error);
          }
        );
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
    this.accessIdsService
      .getAccessItems(
        [this.accessId, ...this.groups],
        this.accessItemsForm ? this.accessItemsForm.value.accessIdFilter : undefined,
        this.accessItemsForm ? this.accessItemsForm.value.workbasketKeyFilter : undefined,
        this.sortModel
      )
      .pipe(take(1))
      .subscribe(
        (accessItemsResource: AccessItemWorkbasketResource) => {
          this.setAccessItemsGroups(accessItemsResource ? accessItemsResource.accessItems : []);
          this.requestInProgressService.setRequestInProgress(false);
        },
        (error) => {
          this.requestInProgressService.setRequestInProgress(false);
          this.notificationService.triggerError(NOTIFICATION_TYPES.FETCH_ERR_2, error);
        }
      );
  }

  revokeAccess() {
    this.notificationService.showDialog(
      `You are going to delete all access related: ${this.accessIdSelected}. Can you confirm this action?`,
      this.onRemoveConfirmed.bind(this)
    );
  }

  private onRemoveConfirmed() {
    this.requestInProgressService.setRequestInProgress(true);
    this.accessIdsService
      .removeAccessItemsPermissions(this.accessIdSelected)
      .pipe(take(1))
      .subscribe(
        () => {
          this.requestInProgressService.setRequestInProgress(false);
          this.notificationService.showToast(
            NOTIFICATION_TYPES.SUCCESS_ALERT,
            new Map<string, string>([['accessId', this.accessIdSelected]])
          );
          this.searchForAccessItemsWorkbaskets();
        },
        (error) => {
          this.requestInProgressService.setRequestInProgress(false);
          this.notificationService.triggerError(NOTIFICATION_TYPES.DELETE_ERR, error);
        }
      );
  }
}
