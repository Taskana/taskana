import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FieldErrorDisplayComponent } from './field-error-display.component';

describe('FieldErrorDisplayComponent', () => {
  let component: FieldErrorDisplayComponent;
  let fixture: ComponentFixture<FieldErrorDisplayComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FieldErrorDisplayComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FieldErrorDisplayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
