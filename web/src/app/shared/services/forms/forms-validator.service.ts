import { NgForm } from '@angular/forms';
import { Injectable } from '@angular/core';
import { AlertService } from 'app/services/alert/alert.service';
import { AlertModel, AlertType } from 'app/models/alert';

@Injectable()
export class FormsValidatorService {
    constructor(
        private alertService: AlertService) { }

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
}
