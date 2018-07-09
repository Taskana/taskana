import { Component, forwardRef, Input } from '@angular/core';
import { NG_VALUE_ACCESSOR, ControlValueAccessor } from '@angular/forms';

@Component({
    selector: 'taskana-type-ahead',
    template: 'dummydetail',
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            multi: true,
            useExisting: forwardRef(() => TaskanaTypeAheadMockComponent),
        }
    ]
})
export class TaskanaTypeAheadMockComponent implements ControlValueAccessor {
    @Input()
    placeHolderMessage;

    @Input()
    validationValue;

    writeValue(obj: any): void {

    }
    registerOnChange(fn: any): void {

    }
    registerOnTouched(fn: any): void {

    }
    setDisabledState?(isDisabled: boolean): void {

    }
}
