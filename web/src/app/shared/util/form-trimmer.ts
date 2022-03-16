import { NgForm } from '@angular/forms';

export function trimForm(form: NgForm) {
  Object.keys(form.form.controls).forEach((controlName) => {
    let control = form.form.controls[controlName];
    if (typeof control.value === 'string') {
      control.setValue(control.value.trim());
    }
  });
}

export function trimObject(object: Object) {
  Object.keys(object).forEach((controlName) => {
    let prop = object[controlName];
    if (typeof prop === 'string') {
      object[controlName] = prop.trim();
    } else if (typeof prop === 'object' && prop !== null) {
      trimObject(prop);
    }
  });
}
