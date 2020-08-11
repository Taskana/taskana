import { Component, OnInit, Input } from '@angular/core';
import { highlight } from 'app/shared/animations/validation.animation';

@Component({
  selector: 'taskana-shared-field-error-display',
  templateUrl: './field-error-display.component.html',
  animations: [highlight],
  styleUrls: ['./field-error-display.component.scss']
})
export class FieldErrorDisplayComponent implements OnInit {
  @Input()
  displayError: boolean;

  @Input()
  errorMessage: string;

  @Input()
  validationTrigger: boolean;

  ngOnInit() {}
}
