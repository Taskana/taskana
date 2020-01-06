import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GeneralFieldsExtensionComponent } from './general-fields-extension.component';

xdescribe('GeneralFieldsExtensionComponent', () => {
  let component: GeneralFieldsExtensionComponent;
  let fixture: ComponentFixture<GeneralFieldsExtensionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [GeneralFieldsExtensionComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GeneralFieldsExtensionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
