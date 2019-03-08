import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DatePickerComponent } from './date-picker.component';
import { BsDatepickerModule, BsDatepickerConfig, ComponentLoaderFactory, PositioningService, BsLocaleService } from 'ngx-bootstrap';

describe('DatePickerComponent', () => {
  let component: DatePickerComponent;
  let fixture: ComponentFixture<DatePickerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [BsDatepickerModule],
      declarations: [DatePickerComponent],
      providers: [BsDatepickerConfig, ComponentLoaderFactory, PositioningService,
        BsLocaleService]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DatePickerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should receive the input properties', () => {
    component.placeholder = 'placeholder';
    component.value = '01/01/2019';
    component.id = 'id';
    component.name = 'name';
    expect(component.placeholder).toBe('placeholder');
    expect(component.value).toBe('01/01/2019');
    expect(component.id).toBe('id');
    expect(component.name).toBe('name');
  });

  it('should all properties have a value', () => {
    component.placeholder = 'test';
    component.value = '01/01/2019';
    component.id = 'id';
    component.name = 'name';
    expect(component.placeholder).not.toBeNull('');
    expect(component.placeholder).not.toBeNaN();
    expect(component.value).not.toBeNull('');
    expect(component.value).not.toBeNaN();
    expect(component.id).not.toBeNull('');
    expect(component.id).not.toBeNaN();
    expect(component.name).not.toBeNull('');
    expect(component.name).not.toBeNaN();
  });
});
