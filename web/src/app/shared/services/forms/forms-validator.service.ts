import { NgForm, FormArray } from '@angular/forms';
import { Injectable } from '@angular/core';
import { AlertService } from 'app/services/alert/alert.service';
import { AlertModel, AlertType } from 'app/models/alert';
import { AccessIdsService } from 'app/shared/services/access-ids/access-ids.service';

@Injectable()
export class FormsValidatorService {

    public formSubmitAttempt = false;

    constructor(
        private alertService: AlertService,
        private accessIdsService: AccessIdsService) {
    }

    public validateFormInformation(form: NgForm, toogleValidationMap: Map<any, boolean>): Promise<any> {
      let validSync = true;

      const forFieldsPromise = new Promise((resolve, reject) => {
        for (const control in form.form.controls) {
          if (control.indexOf('owner') === -1 && form.form.controls[control].invalid) {
            const validationState = toogleValidationMap.get(control);
            validationState ? toogleValidationMap.set(control, !validationState) : toogleValidationMap.set(control, true);
            validSync = false;
          }
        }
        resolve(validSync);
      });

      const ownerPromise = new Promise((resolve, reject) => {
          if (form.form.controls['workbasket.owner']) {
            this.accessIdsService.getAccessItemsInformation(form.form.controls['workbasket.owner'].value).subscribe(items => {
              const validationState = toogleValidationMap.get('workbasket.owner');
              validationState ? toogleValidationMap.set('workbasket.owner', !validationState) :
                toogleValidationMap.set('workbasket.owner', true);
              items.find(item => item.accessId === form.form.controls['workbasket.owner'].value) ? resolve(true) : resolve(false);
            });
          } else {
            const validationState = toogleValidationMap.get(form.form.controls['workbasket.owner']);
              validationState ? toogleValidationMap.set('workbasket.owner', !validationState) :
                toogleValidationMap.set('workbasket.owner', true);
            resolve(true);
          }
        });

      return Promise.all([forFieldsPromise, ownerPromise]).then(values => {
        if (!(values[0] && values[1])) {
          this.alertService.triggerAlert(new AlertModel(AlertType.WARNING, 'There are some empty fields which are required.'))
        }
        return values[0] && values[1];
      });
  }

  public validateFormAccess(form: FormArray, toogleValidationAccessIdMap: Map<any, boolean>): Promise<boolean> {
    const ownerPromise: Array<Promise<boolean>> = new Array<Promise<boolean>>();

    for (let i = 0; i < form.length; i++) {
      ownerPromise.push(new Promise((resolve, reject) => {
        const validationState = toogleValidationAccessIdMap.get(i);
          validationState ? toogleValidationAccessIdMap.set(i, !validationState) :
          toogleValidationAccessIdMap.set(i, true);
        this.accessIdsService.getAccessItemsInformation(form.controls[i].value['accessId']).subscribe(items => {
          items.length > 0 ? resolve(true) : resolve(false);
        })
      }));
    }

    let result = true;
    return Promise.all(ownerPromise).then(values => {
      for (let i = 0; i < values.length; i++) {
        result = result && values[i];
      }
      if (!result) {
        this.alertService.triggerAlert(new AlertModel(AlertType.WARNING, 'There are some empty fields which are required.'))
      }
      return result;
    });
  }

  public isFieldValid(ngForm: NgForm, field: string) {
      if (!ngForm || !ngForm.form.controls || !ngForm.form.controls[field]) {
          return false;
      }
      if (!this.formSubmitAttempt) {
          return true;
      }
      return (this.formSubmitAttempt && ngForm.form.controls[field].valid) ||
          (ngForm.form.controls[field].touched && ngForm.form.controls[field].valid);
  }
}
