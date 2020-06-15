import { Component,
  ElementRef,
  Input,
  OnChanges,
  OnDestroy, QueryList,
  SimpleChanges,
  ViewChildren } from '@angular/core';
import { Observable, Subscription } from 'rxjs';
import { Select } from '@ngxs/store';
import { FormArray, FormBuilder, FormControlDirective, Validators } from '@angular/forms';

import { Workbasket } from 'app/shared/models/workbasket';
import { customFieldCount, WorkbasketAccessItems } from 'app/shared/models/workbasket-access-items';
import { WorkbasketAccessItemsResource } from 'app/shared/models/workbasket-access-items-resource';
import { ACTION } from 'app/shared/models/action';

import { SavingInformation,
  SavingWorkbasketService } from 'app/administration/services/saving-workbaskets.service';
import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';
import { highlight } from 'theme/animations/validation.animation';
import { FormsValidatorService } from 'app/shared/services/forms-validator/forms-validator.service';
import { AccessIdDefinition } from 'app/shared/models/access-id';
import { EngineConfigurationSelectors } from 'app/shared/store/engine-configuration-store/engine-configuration.selectors';
import { NOTIFICATION_TYPES } from '../../../shared/models/notifications';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { AccessItemsCustomisation,
  CustomField,
  getCustomFields } from '../../../shared/models/customisation';

@Component({
  selector: 'taskana-workbasket-access-items',
  templateUrl: './workbasket-access-items.component.html',
  animations: [highlight],
  styleUrls: ['./workbasket-access-items.component.scss']
})
export class WorkbasketAccessItemsComponent implements OnChanges, OnDestroy {
  @Input()
  workbasket: Workbasket;

  @Input()
  action: string;

  @Input()
  active: string;

  @ViewChildren('htmlInputElement') inputs: QueryList<ElementRef>;


  badgeMessage = '';

  @Select(EngineConfigurationSelectors.accessItemsCustomisation) accessItemsCustomization$: Observable<AccessItemsCustomisation>;
  customFields$: Observable<CustomField[]>;

  accessItemsResource: WorkbasketAccessItemsResource;
  accessItemsClone: Array<WorkbasketAccessItems>;
  accessItemsResetClone: Array<WorkbasketAccessItems>;
  requestInProgress = false;
  accessItemsubscription: Subscription;
  savingAccessItemsSubscription: Subscription;
  AccessItemsForm = this.formBuilder.group({
    accessItemsGroups: this.formBuilder.array([])
  });

  toogleValidationAccessIdMap = new Map<number, boolean>();
  private initialized = false;
  private added = false;

  constructor(
    private workbasketService: WorkbasketService,
    private savingWorkbaskets: SavingWorkbasketService,
    private requestInProgressService: RequestInProgressService,
    private formBuilder: FormBuilder,
    private formsValidatorService: FormsValidatorService,
    private notificationsService: NotificationService
  ) {
  }

  get accessItemsGroups(): FormArray {
    return this.AccessItemsForm.get('accessItemsGroups') as FormArray;
  }

  ngOnInit() {
    this.customFields$ = this.accessItemsCustomization$.pipe(getCustomFields(customFieldCount));
  }

  ngAfterViewInit() {
    this.inputs.changes.subscribe(next => {
      if (this.added) next.last.nativeElement.focus();
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!this.initialized && changes.active && changes.active.currentValue === 'accessItems') {
      this.init();
    }
    if (changes.action) {
      this.setBadge();
    }
  }

  private init() {
    if (!this.workbasket._links.accessItems) {
      return;
    }
    this.requestInProgress = true;
    this.accessItemsubscription = this.workbasketService.getWorkBasketAccessItems(this.workbasket._links.accessItems.href)
      .subscribe((accessItemsResource: WorkbasketAccessItemsResource) => {
        this.accessItemsResource = accessItemsResource;
        this.setAccessItemsGroups(accessItemsResource.accessItems);
        this.accessItemsClone = this.cloneAccessItems(accessItemsResource.accessItems);
        this.accessItemsResetClone = this.cloneAccessItems(accessItemsResource.accessItems);
        this.requestInProgress = false;
      });
    this.savingAccessItemsSubscription = this.savingWorkbaskets.triggeredAccessItemsSaving()
      .subscribe((savingInformation: SavingInformation) => {
        if (this.action === ACTION.COPY) {
          this.accessItemsResource._links.self.href = savingInformation.url;
          this.setWorkbasketIdForCopy(savingInformation.workbasketId);
          this.onSave();
        }
      });
    this.initialized = true;
  }

  setAccessItemsGroups(accessItems: Array<WorkbasketAccessItems>) {
    const AccessItemsFormGroups = accessItems.map(accessItem => this.formBuilder.group(accessItem));
    AccessItemsFormGroups.forEach(accessItemGroup => {
      accessItemGroup.controls.accessId.setValidators(Validators.required);
    });
    const AccessItemsFormArray = this.formBuilder.array(AccessItemsFormGroups);
    this.AccessItemsForm.setControl('accessItemsGroups', AccessItemsFormArray);
  }

  addAccessItem() {
    const workbasketAccessItems = new WorkbasketAccessItems();
    workbasketAccessItems.workbasketId = this.workbasket.workbasketId;
    workbasketAccessItems.permRead = true;
    const newForm = this.formBuilder.group(workbasketAccessItems);
    newForm.controls.accessId.setValidators(Validators.required);
    this.accessItemsGroups.push(newForm);
    this.accessItemsClone.push(workbasketAccessItems);
    this.added = true;
  }

  clear() {
    this.formsValidatorService.formSubmitAttempt = false;
    this.AccessItemsForm.reset();
    this.setAccessItemsGroups(this.accessItemsResetClone);
    this.accessItemsClone = this.cloneAccessItems(this.accessItemsResetClone);
    this.notificationsService.showToast(NOTIFICATION_TYPES.INFO_ALERT);
  }

  remove(index: number) {
    this.accessItemsGroups.removeAt(index);
    this.accessItemsClone.splice(index, 1);
  }

  isFieldValid(field: string, index: number): boolean {
    return this.formsValidatorService.isFieldValid(this.accessItemsGroups[index], field);
  }

  onSubmit() {
    this.formsValidatorService.formSubmitAttempt = true;
    this.formsValidatorService.validateFormAccess(this.accessItemsGroups, this.toogleValidationAccessIdMap).then(value => {
      if (value) {
        this.onSave();
      }
    });
  }

  checkAll(row: number, value: any) {
    const checkAll = value.target.checked;
    const workbasketAccessItemsObj = new WorkbasketAccessItems();
    Object.keys(workbasketAccessItemsObj).forEach(property => {
      if (property !== 'accessId' && property !== '_links' && property !== 'workbasketId' && property !== 'accessItemId') {
        this.accessItemsGroups.controls[row].get(property).setValue(checkAll);
      }
    });
  }

  accessItemSelected(accessItem: AccessIdDefinition, row: number) {
    this.accessItemsGroups.controls[row].get('accessId').setValue(accessItem.accessId);
    this.accessItemsGroups.controls[row].get('accessName').setValue(accessItem.name);
  }

  private onSave() {
    this.requestInProgressService.setRequestInProgress(true);
    this.workbasketService.updateWorkBasketAccessItem(
      this.accessItemsResource._links.self.href, this.AccessItemsForm.value.accessItemsGroups
    )
      .subscribe(response => {
        this.accessItemsClone = this.cloneAccessItems(this.AccessItemsForm.value.accessItemsGroups);
        this.accessItemsResetClone = this.cloneAccessItems(this.AccessItemsForm.value.accessItemsGroups);
        this.notificationsService.showToast(NOTIFICATION_TYPES.SUCCESS_ALERT_7,
          new Map<string, string>([['workbasketKey', this.workbasket.key]]));
        this.requestInProgressService.setRequestInProgress(false);
      }, error => {
        this.notificationsService.triggerError(NOTIFICATION_TYPES.SAVE_ERR_2, error);
        this.requestInProgressService.setRequestInProgress(false);
      });
  }

  private setBadge() {
    if (this.action === ACTION.COPY) {
      this.badgeMessage = `Copying workbasket: ${this.workbasket.key}`;
    }
  }

  private cloneAccessItems(inputaccessItem): Array<WorkbasketAccessItems> {
    return this.AccessItemsForm.value.accessItemsGroups.map(
      (accessItems: WorkbasketAccessItems) => ({ ...accessItems })
    );
  }

  private setWorkbasketIdForCopy(workbasketId: string) {
    this.accessItemsGroups.value.forEach(element => {
      delete element.accessItemId;
      element.workbasketId = workbasketId;
    });
  }

  getAccessItemCustomProperty(customNumber: number): string {
    return `permCustom${customNumber}`;
  }

  focusNewInput(input: ElementRef) {
    if (this.added) {
      input.nativeElement.focus();
    }
  }


  ngOnDestroy(): void {
    if (this.accessItemsubscription) {
      this.accessItemsubscription.unsubscribe();
    }
    if (this.savingAccessItemsSubscription) {
      this.savingAccessItemsSubscription.unsubscribe();
    }
  }
}
