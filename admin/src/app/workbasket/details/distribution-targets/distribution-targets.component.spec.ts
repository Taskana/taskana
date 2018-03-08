import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DistributionTargetsComponent } from './distribution-targets.component';

describe('DistributionTargetsComponent', () => {
  let component: DistributionTargetsComponent;
  let fixture: ComponentFixture<DistributionTargetsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DistributionTargetsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DistributionTargetsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
