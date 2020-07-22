import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ClassificationReportComponent } from './classification-report.component';

describe('ClassificationReportComponent', () => {
  let component: ClassificationReportComponent;
  let fixture: ComponentFixture<ClassificationReportComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ClassificationReportComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClassificationReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
