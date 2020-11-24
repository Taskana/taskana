import { Component, OnInit } from '@angular/core';
import { Select, Store } from '@ngxs/store';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Observable, Subject } from 'rxjs';
import { FormsValidatorService } from 'app/shared/services/forms-validator/forms-validator.service';
import { AccessItemWorkbasket } from 'app/shared/models/access-item-workbasket';
import { Direction, Sorting } from 'app/shared/models/sorting';
import { EngineConfigurationSelectors } from 'app/shared/store/engine-configuration-store/engine-configuration.selectors';
import { takeUntil } from 'rxjs/operators';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';
import { AccessIdDefinition } from '../../../shared/models/access-id';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { AccessItemsCustomisation, CustomField, getCustomFields } from '../../../shared/models/customisation';
import { customFieldCount } from '../../../shared/models/workbasket-access-items';
import {
  GetAccessItems,
  GetGroupsByAccessId,
  RemoveAccessItemsPermissions
} from '../../../shared/store/access-items-management-store/access-items-management.actions';
import { AccessItemsManagementSelector } from '../../../shared/store/access-items-management-store/access-items-management.selector';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'taskana-administration-access-items-management',
  templateUrl: './access-items-management.component.html',
  styleUrls: ['./access-items-management.component.scss']
})
export class AccessItemsManagementComponent implements OnInit {
  accessIdSelected: string;
  accessIdPrevious: string;
  isRequired: boolean = false;
  accessIdName: string;
  panelState: boolean = false;
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
  accessItems: any[];
  list_filtered: any[];

  @Select(EngineConfigurationSelectors.accessItemsCustomisation) accessItemsCustomization$: Observable<
    AccessItemsCustomisation
  >;
  @Select(AccessItemsManagementSelector.groups) groups$: Observable<AccessIdDefinition[]>;
  customFields$: Observable<CustomField[]>;
  destroy$ = new Subject<void>();

  constructor(
    private formBuilder: FormBuilder,
    private formsValidatorService: FormsValidatorService,
    private requestInProgressService: RequestInProgressService,
    private notificationService: NotificationService,
    private store: Store,
    public dialog: MatDialog
  ) {}

  ngOnInit() {
    this.groups$.pipe(takeUntil(this.destroy$)).subscribe((groups) => {
      this.groups = groups;
    });
  }

  onSelectAccessId(selected: AccessIdDefinition) {
    if (selected) {
      this.accessId = selected;
      if (this.accessIdPrevious !== selected.accessId) {
        this.accessIdPrevious = selected.accessId;
        this.accessIdName = selected.name;
        this.store.dispatch(new GetGroupsByAccessId(selected.accessId)).subscribe(() => {
          this.searchForAccessItemsWorkbaskets();
        });
      }
    } else {
      this.accessItemsForm = null;
    }
    this.customFields$ = this.accessItemsCustomization$.pipe(getCustomFields(customFieldCount));
  }

  searchForAccessItemsWorkbaskets() {
    this.removeFocus();
    this.store
      .dispatch(new GetAccessItems([this.accessId, ...this.groups], '', '', this.sortModel))
      .subscribe((state) => {
        this.setAccessItemsGroups(
          state['accessItemsManagement'].accessItemsResource
            ? state['accessItemsManagement'].accessItemsResource.accessItems
            : []
        );
      });
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
    this.accessItems = accessItems;
    if (this.accessItemsForm.value.workbasketKeyFilter || this.accessItemsForm.value.accessIdFilter) {
      this.filterAccessItems();
    }
  }

  filterAccessItems() {
    if (!this.accessItemsForm.value.accessIdFilter) {
      this.accessItems = this.accessItems.filter((value) =>
        value.workbasketKey.toLowerCase().includes(this.accessItemsForm.value.workbasketKeyFilter)
      );
    } else if (!this.accessItemsForm.value.workbasketKeyFilter) {
      this.accessItems = this.accessItems.filter((value) =>
        value.accessName.toLowerCase().includes(this.accessItemsForm.value.accessIdFilter)
      );
    } else {
      this.accessItems = this.accessItems.filter((value) =>
        value.workbasketKey.toLowerCase().includes(this.accessItemsForm.value.workbasketKeyFilter)
      );
      this.accessItems = this.accessItems.filter((value) =>
        value.accessName.toLowerCase().includes(this.accessItemsForm.value.accessIdFilter)
      );
    }
  }

  revokeAccess() {
    this.notificationService.showDialog(
      `You are going to delete all access related: ${this.accessId.accessId}. Can you confirm this action?`,
      () => {
        this.store.dispatch(new RemoveAccessItemsPermissions(this.accessId.accessId)).subscribe(() => {
          this.searchForAccessItemsWorkbaskets();
        });
      }
    );
  }

  get accessItemsGroups(): FormArray {
    return this.accessItemsForm ? (this.accessItemsForm.get('accessItemsGroups') as FormArray) : null;
  }

  isFieldValid(field: string, index: number): boolean {
    return this.formsValidatorService.isFieldValid(this.accessItemsGroups[index], field);
  }

  sorting(sort: Sorting) {
    this.sortModel = sort;
    this.searchForAccessItemsWorkbaskets();
  }

  removeFocus() {
    if (document.activeElement instanceof HTMLElement) {
      document.activeElement.focus();
    }
  }

  clearFilter() {
    if (this.accessItemsForm) {
      this.accessItemsForm.patchValue({
        workbasketKeyFilter: '',
        accessIdFilter: ''
      });
      this.searchForAccessItemsWorkbaskets();
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
