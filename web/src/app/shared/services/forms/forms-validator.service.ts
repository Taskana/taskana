import { NgForm } from '@angular/forms';
import { Injectable } from '@angular/core';
import { AlertService } from 'app/services/alert/alert.service';
import { AlertModel, AlertType } from 'app/models/alert';

@Injectable()
export class FormsValidatorService {

    public formSubmitAttempt = false;

    constructor(
        private alertService: AlertService) {
    }

    public validate(form: NgForm, toogleValidationMap: Map<any, boolean>): boolean {
        let valid = true;
        for (const control in form.form.controls) {
            if (form.form.controls[control].invalid) {
                const validationState = toogleValidationMap.get(control);
                validationState ? toogleValidationMap.set(control, !validationState) : toogleValidationMap.set(control, true);
                valid = false;
            }
        }
        if (!valid) {
            this.alertService.triggerAlert(new AlertModel(AlertType.WARNING, `There are some empty fields which are required.`))
        }
        return valid;
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
