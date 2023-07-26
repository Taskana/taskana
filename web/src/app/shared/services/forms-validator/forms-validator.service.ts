import { FormArray, NgForm, NgModel } from '@angular/forms';
import { Injectable } from '@angular/core';
import { AccessIdsService } from 'app/shared/services/access-ids/access-ids.service';
import { NotificationService } from '../notifications/notification.service';
import { Observable, Subject, Subscription, timer } from 'rxjs';

@Injectable()
export class FormsValidatorService {
  get inputOverflowObservable(): Observable<Map<string, boolean>> {
    return this.inputOverflow.asObservable();
  }
  formSubmitAttempt = false;
  private workbasketOwner = 'workbasket.owner';
  private inputOverflowInternalMap = new Map<string, boolean>();
  private inputOverflow = new Subject<Map<string, boolean>>();
  private overflowErrorSubscriptionMap = new Map<string, Subscription>();

  constructor(private notificationsService: NotificationService, private accessIdsService: AccessIdsService) {}

  async validateFormInformation(form: NgForm, toggleValidationMap: Map<any, boolean>): Promise<any> {
    let validSync = true;
    if (!form) {
      return false;
    }
    const forFieldsPromise = new Promise((resolve) => {
      Object.keys(form.form.controls).forEach((control) => {
        if (control.indexOf('owner') === -1 && form.form.controls[control].invalid) {
          const validationState = toggleValidationMap.get(control);
          toggleValidationMap.set(this.workbasketOwner, !validationState);
          validSync = false;
        }
      });
      resolve(validSync);
    });

    const ownerPromise = new Promise((resolve) => {
      const ownerString = 'owner';
      if (form.form.controls[this.workbasketOwner]) {
        this.accessIdsService.searchForAccessId(form.form.controls[this.workbasketOwner].value).subscribe((items) => {
          const validationState = toggleValidationMap.get(this.workbasketOwner);
          toggleValidationMap.set(this.workbasketOwner, !validationState);
          const valid = items.find((item) => item.accessId === form.form.controls[this.workbasketOwner].value);
          resolve(new ResponseOwner({ valid, field: ownerString }));
        });
      } else {
        const validationState = toggleValidationMap.get(form.form.controls[this.workbasketOwner]);
        toggleValidationMap.set(this.workbasketOwner, !validationState);
        resolve(new ResponseOwner({ valid: true, field: ownerString }));
      }
    });

    const values = await Promise.all([forFieldsPromise, ownerPromise]);
    const responseOwner = new ResponseOwner(values[1]);
    if (!(values[0] && responseOwner.valid)) {
      if (!responseOwner.valid) {
        this.notificationsService.showError('OWNER_NOT_VALID', { owner: responseOwner.field });
      } else {
        this.notificationsService.showError('EMPTY_FIELDS');
      }
    }
    return values[0] && responseOwner.valid;
  }

  async validateFormAccess(form: FormArray, toggleValidationAccessIdMap: Map<any, boolean>): Promise<boolean> {
    const ownerPromise: Array<Promise<boolean>> = new Array<Promise<boolean>>();

    for (let i = 0; i < form.length; i++) {
      ownerPromise.push(
        new Promise((resolve) => {
          const validationState = toggleValidationAccessIdMap.get(i);
          toggleValidationAccessIdMap.set(i, !validationState);
          this.accessIdsService.searchForAccessId(form.controls[i].value.accessId).subscribe((items) => {
            resolve(new ResponseOwner({ valid: items.length > 0, field: 'access id' }));
          });
        })
      );
    }

    let result = true;
    const values = await Promise.all(ownerPromise);
    let responseOwner;
    values.forEach((owner) => {
      responseOwner = new ResponseOwner(owner);
      result = result && responseOwner.valid;
    });
    if (!result) {
      this.notificationsService.showError('OWNER_NOT_VALID', {
        owner: responseOwner ? responseOwner.field : 'owner'
      });
    }

    form.controls.forEach((control) => {
      const { permEditTasks, permReadTasks, permRead } = control.value;

      if (permEditTasks && (!permReadTasks || !permRead)) {
        this.notificationsService.showWarning('PERM_EDIT_TASKS_MISSING_DEPENDING_PERMISSION');
      }

      if (permReadTasks && !permRead) {
        this.notificationsService.showWarning('PERM_READ_TASKS_MISSING_DEPENDING_PERMISSIONS');
      }
    });

    return result;
  }

  isFieldValid(ngForm: NgForm, field: string): boolean {
    if (!ngForm || !ngForm.form.controls || !ngForm.form.controls[field]) {
      return false;
    }
    if (!this.formSubmitAttempt) {
      return true;
    }
    return (
      (this.formSubmitAttempt && ngForm.form.controls[field].valid) ||
      (ngForm.form.controls[field].touched && ngForm.form.controls[field].valid)
    );
  }

  validateInputOverflow(inputFieldModel: NgModel, maxLength: Number, event?: any): void {
    if (this.overflowErrorSubscriptionMap.has(inputFieldModel.name)) {
      this.overflowErrorSubscriptionMap.get(inputFieldModel.name).unsubscribe();
    }
    if (inputFieldModel.value.length >= maxLength) {
      this.inputOverflowInternalMap.set(inputFieldModel.name, true);
      this.inputOverflow.next(this.inputOverflowInternalMap);
      this.overflowErrorSubscriptionMap.set(
        inputFieldModel.name,
        timer(3000).subscribe(() => {
          this.inputOverflowInternalMap.set(inputFieldModel.name, false);
          this.inputOverflow.next(this.inputOverflowInternalMap);
        })
      );
    } else {
      this.inputOverflowInternalMap.set(inputFieldModel.name, false);
    }
  }
}

function ResponseOwner(owner) {
  this.valid = owner.valid;
  this.field = owner.field;
}
