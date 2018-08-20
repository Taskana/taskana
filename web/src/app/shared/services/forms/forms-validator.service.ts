import { NgForm, FormArray } from '@angular/forms';
import { Injectable } from '@angular/core';
import { AlertService } from 'app/services/alert/alert.service';
import { AlertModel, AlertType } from 'app/models/alert';
import { AccessIdsService } from 'app/shared/services/access-ids/access-ids.service';

@Injectable()
export class FormsValidatorService {

    public formSubmitAttempt = false;
    private workbasketOwner = 'workbasket.owner';

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
        const ownerString = 'owner';
          if (form.form.controls[this.workbasketOwner]) {
            this.accessIdsService.getAccessItemsInformation(form.form.controls[this.workbasketOwner].value).subscribe(items => {
              const validationState = toogleValidationMap.get(this.workbasketOwner);
              validationState ? toogleValidationMap.set(this.workbasketOwner, !validationState) :
                toogleValidationMap.set(this.workbasketOwner, true);
              items.find(item => item.accessId === form.form.controls[this.workbasketOwner].value) ?
              resolve(new ResponseOwner({valid: true, field: ownerString})) :
              resolve(new ResponseOwner({valid: false, field: ownerString}));
            });
          } else {
            const validationState = toogleValidationMap.get(form.form.controls[this.workbasketOwner]);
              validationState ? toogleValidationMap.set(this.workbasketOwner, !validationState) :
                toogleValidationMap.set(this.workbasketOwner, true);
            resolve(new ResponseOwner({valid: true, field: ownerString}));
          }
        });

      return Promise.all([forFieldsPromise, ownerPromise]).then(values => {
        const responseOwner = new ResponseOwner(values[1]);
        if (!(values[0] && responseOwner.valid)) {
          if (!responseOwner.valid) {
            this.alertService.triggerAlert(new AlertModel(AlertType.WARNING, 'The ' + responseOwner.field + ' introduced is not valid.'))
          } else {
            this.alertService.triggerAlert(new AlertModel(AlertType.WARNING, 'There are some empty fields which are required.'))
          }
        }
        return values[0] && responseOwner.valid;
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
          items.length > 0 ?
          resolve(new ResponseOwner({valid: true, field: 'access id'})) :
          resolve(new ResponseOwner({valid: false, field: 'access id'}));
        })
      }));
    }

    let result = true;
    return Promise.all(ownerPromise).then(values => {
      let responseOwner;
      for (let i = 0; i < values.length; i++) {
        responseOwner = new ResponseOwner(values[i]);
        result = result && responseOwner.valid;
      }
      if (!result) {
        this.alertService.triggerAlert(new AlertModel(AlertType.WARNING, 'The ' + responseOwner.field + ' introduced is not valid.'))
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

function ResponseOwner(obj) {
  this.valid = obj.valid;
  this.field = obj.field;
}
