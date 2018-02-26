import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GeneralMessageModalComponent } from './general-message-modal.component';

describe('GeneralMessageModalComponent', () => {
  let component: GeneralMessageModalComponent;
  let fixture: ComponentFixture<GeneralMessageModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GeneralMessageModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GeneralMessageModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
