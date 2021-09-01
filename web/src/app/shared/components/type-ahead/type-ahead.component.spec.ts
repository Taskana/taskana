import { ComponentFixture, fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';
import { DebugElement } from '@angular/core';
import { TypeAheadComponent } from './type-ahead.component';
import { AccessIdsService } from '../../services/access-ids/access-ids.service';
import { of } from 'rxjs';
import { NgxsModule } from '@ngxs/store';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatTooltipModule } from '@angular/material/tooltip';

const accessIdService: Partial<AccessIdsService> = {
  searchForAccessId: jest.fn().mockReturnValue(of([{ accessId: 'user-g-1', name: 'Gerda' }]))
};

describe('TypeAheadComponent with AccessId input', () => {
  let fixture: ComponentFixture<TypeAheadComponent>;
  let debugElement: DebugElement;
  let component: TypeAheadComponent;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          NgxsModule.forRoot([]),
          MatFormFieldModule,
          MatInputModule,
          MatAutocompleteModule,
          MatTooltipModule,
          BrowserAnimationsModule,
          FormsModule,
          ReactiveFormsModule
        ],
        declarations: [TypeAheadComponent],
        providers: [{ provide: AccessIdsService, useValue: accessIdService }]
      }).compileComponents();

      fixture = TestBed.createComponent(TypeAheadComponent);
      debugElement = fixture.debugElement;
      component = fixture.componentInstance;
      fixture.detectChanges();
    })
  );

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch name when typing in an access id', fakeAsync(() => {
    const input = debugElement.nativeElement.querySelector('.type-ahead__input-field');
    expect(input).toBeTruthy();
    input.value = 'user-g-1';
    input.dispatchEvent(new Event('input'));
    component.accessIdForm.get('accessId').updateValueAndValidity({ emitEvent: true });

    tick();
    expect(component.name).toBe('Gerda');
  }));

  it('should emit false when an invalid access id is set', fakeAsync(() => {
    const emitSpy = jest.spyOn(component.isFormValid, 'emit');
    component.displayError = true;
    component.accessIdForm.get('accessId').setValue('invalid-user');
    component.accessIdForm.get('accessId').updateValueAndValidity({ emitEvent: true });

    tick();
    fixture.detectChanges();
    expect(emitSpy).toHaveBeenCalledWith(false);
  }));

  it('should emit true when a valid access id is set', fakeAsync(() => {
    const emitSpy = jest.spyOn(component.isFormValid, 'emit');
    component.accessIdForm.get('accessId').setValue('user-g-1');
    component.accessIdForm.get('accessId').updateValueAndValidity({ emitEvent: true });

    tick();
    fixture.detectChanges();
    expect(emitSpy).toHaveBeenCalledWith(true);
  }));
});
